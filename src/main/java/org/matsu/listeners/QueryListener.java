package org.matsu.listeners;

import java.util.List;

import org.matsu.crawler.HopDatabase;
import org.matsu.http.Requester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;

public class QueryListener extends ListenerAdapter {

    HopDatabase hopDatabase = new HopDatabase();

    static Logger logger = LoggerFactory.getLogger(QueryListener.class);
    Requester http = new Requester();

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        logger.info("Slash command received");

        String hopName = event.getOption("hop_name").getAsString();
        String hopUrl = hopDatabase.getHopUrl(hopName);

        event.reply(hopUrl).queue();
        logger.info("Returned reply");
    }

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        logger.info("On auto complete");

        List<Choice> hopChoicesMatching = hopDatabase.getHopChoicesMatching(event.getFocusedOption().getValue());
        event.replyChoices(hopChoicesMatching).queue();
    }
}
