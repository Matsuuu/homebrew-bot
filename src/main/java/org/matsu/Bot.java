package org.matsu;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.matsu.listeners.QueryListener;
import org.matsu.scrape.SeleniumConfig;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class Bot {

    static Logger logger = LoggerFactory.getLogger(Bot.class);

    public static void main(String[] args) throws IOException {
        if (args.length <= 0) {
            logger.error("Please provide the auth token");
            return;
        }

        SeleniumConfig selenium = new SeleniumConfig();
        selenium.getPage("https://beermaverick.com/hop/citra/");
        selenium.hideElement("[id^=AdThrive_Footer]");
        selenium.hideElement("#gdpr-consent-tool-wrapper");
        byte[] screenshot = selenium.screenshotElement(By.id("aromaChart"));
        Path currentPath = Path.of("hop-images/hopname.png");
        
        Files.write(currentPath, screenshot);

        String token = args[0];

        logger.info("Beginning initialization");

        JDA bot = initialize(token);

        OptionData slashOption = new OptionData(OptionType.STRING, "hop_name", "Hop name", true, true);
        bot.upsertCommand("hops", "Get information on given hop")
            .addOptions(slashOption)
            .queue();
        
        logger.info("Bot initialized!");
    }

    static JDA initialize(String token) {
        JDABuilder builder = JDABuilder.createDefault(token);
        builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE)
            .setBulkDeleteSplittingEnabled(true)
            .setCompression(Compression.NONE)
            .addEventListeners(new QueryListener())
            ;
        return builder.build();
    }
}
