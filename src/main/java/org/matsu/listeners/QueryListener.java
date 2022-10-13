package org.matsu.listeners;

import java.util.List;

import org.matsu.crawler.HopDatabase;
import org.matsu.http.Requester;
import org.matsu.pojo.Hop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public class QueryListener extends ListenerAdapter {

    HopDatabase hopDatabase = new HopDatabase();

    static Logger logger = LoggerFactory.getLogger(QueryListener.class);
    Requester http = new Requester();

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        logger.info("Slash command received");
        // TODO: Check command
        handleHopSlashCommand(event);
        logger.info("Returned reply");
    }

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        logger.info("On auto complete");
        // TODO: Check command

        handleHopAutoComplete(event);
    }

    void handleHopSlashCommand(SlashCommandInteractionEvent event) {
        String hopName = event.getOption("hop_name").getAsString();
        Hop hop = hopDatabase.findHop(hopName);
        if (hop.equals(Hop.NOT_FOUND)) {
            // Handle not found
        }

        String hopUrl = hopDatabase.getHopUrl(hopName);

        MessageEmbed embed = new EmbedBuilder()
            .setColor(0x7CFC00)
            .setTitle(hop.name(), hopUrl)
            .addField(new Field("Purpose", "Dual", true))
            .addField(new Field("Country", "USA", true))
            .addField(new Field("Similiar hops:", "Simcoe, Citra, etc.", false))
            .build();

        MessageCreateData message = new MessageCreateBuilder()
            .setEmbeds(embed)
            .build();

        event.reply(message).queue();

    }

    void handleHopAutoComplete(CommandAutoCompleteInteractionEvent event) {
        List<Choice> hopChoicesMatching = hopDatabase.getHopChoicesMatching(event.getFocusedOption().getValue());
        event.replyChoices(hopChoicesMatching).queue();
    }
}
