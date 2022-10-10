package org.matsu;

import org.matsu.listeners.QueryListener;
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

    public static void main(String[] args) {
        if (args.length <= 0) {
            logger.error("Please provide the auth token");
            return;
        }

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
