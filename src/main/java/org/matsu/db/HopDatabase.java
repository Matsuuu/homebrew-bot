package org.matsu.db;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.matsu.http.Requester;
import org.matsu.pojo.Hop;
import org.matsu.pojo.HopData;
import org.matsu.scrape.SeleniumConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.interactions.commands.Command.Choice;

public class HopDatabase {

    static Logger logger = LoggerFactory.getLogger(HopDatabase.class);
    Requester http = new Requester();

    Map<String, Hop> hops = new HashMap<>();
    List<Choice> hopChoices = new ArrayList<>();

    public HopDatabase() {
        scrapeHopMaverickHopList();
        hopsToChoices();
    }

    public Hop findHop(String hopName) {
        return hops.getOrDefault(hopName.toLowerCase(), Hop.NOT_FOUND);
    }

    public String getHopUrl(Hop hop) {
        String hopUrlSuffix =  hop.url();
        return Hop.HOP_SITE_BASE + hopUrlSuffix;
    }

    public String getHopApiDataUrl(String hopDataId) {
        return Hop.HOP_DATA_API_URL + hopDataId;
    }

    void hopsToChoices() {
        hops.values().forEach(hop -> hopChoices.add(new Choice(hop.name(), hop.dataName())));
    }

    public List<Choice> getHopChoices() {
        return hopChoices;
    }

    public List<Choice> getHopChoicesMatching(String stringToMatch) {
        return hopChoices.stream()
            .filter(choice -> choice.getName().contains(stringToMatch) 
                    || choice.getAsString().contains(stringToMatch))
            .limit(25)
            .toList();
    }

    void scrapeHopMaverickHopList() {
        String hopSiteData = http.get(Hop.HOP_SITE_URL);
        //logger.info(hopSiteData);
        String hopDataString = hopSiteData.substring(
                hopSiteData.indexOf("var hops = [") + "var hops = ".length(),
                hopSiteData.indexOf("var autoc") - 4
        );

        String formattedHopDataString = hopDataString.replaceAll("value", "\"value\"")
            .replaceAll("data", "\"data\"") 
            + "]"; // Add the remaining closing tag as we need to remove the existing one as it's invalid json

        ObjectMapper mapper = new ObjectMapper();
        try {
            List<Hop> hopList = Arrays.asList(mapper.readValue(formattedHopDataString, Hop[].class));
            hops = hopList.stream().collect(Collectors.toMap(Hop::dataName, Function.identity()));
            logger.info("Successfully scraped hopmaverick hops.");
        } catch (JsonProcessingException e) {
            logger.error("Failed to get hop data from hopmaverick");
            e.printStackTrace();
        }
    }

    HopData scrapeHopData(Hop hop) {
        SeleniumConfig selenium = SeleniumConfig.getInstance();
        selenium.getPage(getHopUrl(hop));

        selenium.scrollToBottom();
        selenium.clickPage();
        
        File hopChartImage = screenShotHopChart(hop, selenium);

        String sourceCode = selenium.driver.getPageSource();
        String hopDataId = getHopDataId(sourceCode);

        List<Hop> hopPairings = getHopPairings(hop, sourceCode);

        Map<String, String> hopDataFields = fetchHopData(hopDataId, selenium);

        HopData hopData = new HopData(
                hopDataFields.get("Country"),
                hopDataFields.get("Purpose"),
                hopDataFields.get("Alpha Acids"),
                hopDataFields.get("Beta Acids"),
                hopDataFields.get("Profile"),
                hopChartImage,
                hopPairings
        );

        return hopData;
    }

    String getHopDataId(String sourceCode) {
        Pattern pattern = Pattern.compile("data-hop=\"(\\d+)\"");
        Matcher matcher = pattern.matcher(sourceCode);
        if (matcher.find()) {
            return matcher.group(1);
        }
        // Shouldn't happen
        // TODO: Handle this case somehow
        return "";
    }

    File screenShotHopChart(Hop hop, SeleniumConfig selenium) {
        try {
            Thread.sleep(2000);
        } catch(Exception ex) {}

        // Hide all of that ad and gdpr stuff
        selenium.hideElement("header");
        selenium.hideElement("#gridlove-header-responsive");
        selenium.hideElement("[id^=AdThrive_Footer]");
        selenium.hideElement("#gdpr-consent-tool-wrapper");

        // Pad the element a little bit to improve the screenshot
        selenium.padElement("#aromaChart");

        File screenshot = selenium.screenshotElement(By.id("aromaChart"));
        return screenshot;
    }

    Map<String, String> fetchHopData(String hopDataId, SeleniumConfig selenium) {
        selenium.getPage(getHopApiDataUrl(hopDataId));
        
        selenium.waitForElement(By.cssSelector("[id^=embedframe]"), 2000);
        List<WebElement> listElements = selenium.getElements(By.tagName("li"));

        Map<String, String> hopDataFields = listElements.stream()
            .map(WebElement::getText)
            .map(text -> text.split(":"))
            .collect(Collectors.toMap(textParts -> textParts[0], textParts -> textParts[1]));

        return hopDataFields;
    } 

    List<Hop> getHopPairings(Hop hop, String sourceCode) {
        logger.info("Getting hop pairings");
        Pattern pattern = Pattern.compile("(We found that)(.*?)(hops)");
        Matcher matcher = pattern.matcher(sourceCode);
        if (matcher.find()) {
            logger.info("Found pairings for hop");
            String pairings = matcher.group(2);
            Pattern namePattern = Pattern.compile(">(\\w+)<");
            Matcher nameMatcher = namePattern.matcher(pairings);

            List<Hop> foundPairings = new ArrayList<>();
            while (nameMatcher.find()) {
                String pairingName = nameMatcher.group(1).toLowerCase();
                logger.info("Pairing: " + pairingName);
                if (hops.containsKey(pairingName)) {
                    foundPairings.add(hops.get(pairingName));
                }
            }
            return foundPairings;
        }

        return Collections.emptyList();
    }


    public Hop getHopWithHopData(Hop hop) {
        if (hop.hopData() != null) {
            logger.info("Hop data cache hit");
            return hop;
        }

        logger.info(hop.toString());
        logger.info("Hop data not found. Initiating data fetching...");
        HopData hopData = scrapeHopData(hop);

        Hop initializedHop = new Hop(hop.name(), hop.dataName(), hopData);
        hops.put(hop.dataName().toLowerCase(), initializedHop);
        return initializedHop;
    }
}
