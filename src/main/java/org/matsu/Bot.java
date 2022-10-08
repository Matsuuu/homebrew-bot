package org.matsu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class Bot {

    static Logger logger = LoggerFactory.getLogger(Bot.class);

    public static void main(String[] args) {
        if (args.length <= 0) {
            logger.error("Please provide the auth token");
            return;
        }

        String token = args[0];

        logger.info("Beginning initialization");

        JDABuilder builder = JDABuilder.createDefault(token);
        builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE)
            .setBulkDeleteSplittingEnabled(true)
            .setCompression(Compression.NONE)
            .setActivity(Activity.watching("TV"));

        JDA jda = builder.build();
        
        logger.info("Bot initialized!");
    }
}
