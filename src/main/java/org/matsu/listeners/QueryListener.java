package org.matsu.listeners;

import org.matsu.commands.CommandType;
import org.matsu.commands.Commands;
import org.matsu.db.HopDatabase;
import org.matsu.http.Requester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class QueryListener extends ListenerAdapter {

    HopDatabase hopDatabase = new HopDatabase();

    static Logger logger = LoggerFactory.getLogger(QueryListener.class);
    Requester http = new Requester();

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        logger.info("Slash command received");
        Commands.getCommand(event.getName(), CommandType.SLASH_COMMAND)
            .ifPresentOrElse(
                    command -> command.apply(event),
                    () -> {}
            );
        logger.info("Returned reply");
    }

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        logger.info("On auto complete");
        Commands.getCommand(event.getName(), CommandType.AUTO_COMPLETE)
            .ifPresentOrElse(
                    command -> command.apply(event),
                    () -> {}
            );
    }

}
