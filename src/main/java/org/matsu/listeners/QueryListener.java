package org.matsu.listeners;

import java.util.function.Consumer;

import org.matsu.commands.Command;
import org.matsu.commands.CommandType;
import org.matsu.hops.HopDatabase;
import org.matsu.http.Requester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

public class QueryListener extends ListenerAdapter {

    HopDatabase hopDatabase = new HopDatabase();

    static Logger logger = LoggerFactory.getLogger(QueryListener.class);
    Requester http = new Requester();

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        logger.info("Slash command received");
        Command.getCommand(event.getName(), CommandType.SLASH_COMMAND).ifPresentOrElse(
                command -> command.accept(event),
                this::onUnknownCommand
        );
        logger.info("Returned reply");
    }

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        logger.info("On auto complete");
        Command.getCommand(event.getName(), CommandType.AUTO_COMPLETE).ifPresentOrElse(
                command -> command.accept(event),
                this::onUnknownCommand
        );
    }

    Consumer<Event> onUnknownCommand() {
        return event -> {
            if (event instanceof IReplyCallback replyable) {
                replyable.reply("Could not find command ");
            }
        };
    }
}
