package org.matsu.crawler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.matsu.http.Requester;
import org.matsu.pojo.Hop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.interactions.commands.Command.Choice;

public class HopDatabase {

    static Logger logger = LoggerFactory.getLogger(HopDatabase.class);
    Requester http = new Requester();

    Map<String, Hop> hops = new HashMap<>();
    List<Choice> hopChoices = new ArrayList<>();

    public final String HOP_SITE_BASE = "https://beermaverick.com";
    final String HOP_SITE_URL = HOP_SITE_BASE + "/hops/";

    public HopDatabase() {
        scrapeHopMaverickHopList();
        hopsToChoices();
    }

    public String getHopUrl(String hopName) {
        String hopUrlSuffix =  hops.getOrDefault(hopName.toLowerCase(), Hop.NOT_FOUND()).url();
        return HOP_SITE_BASE + hopUrlSuffix;
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
        String hopSiteData = http.get(HOP_SITE_URL);
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
}
