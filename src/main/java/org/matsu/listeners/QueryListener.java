package org.matsu.listeners;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.matsu.db.HopDatabase;
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
import net.dv8tion.jda.api.utils.FileUpload;
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
        event.deferReply();
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
            event.reply("Hop not found. Please use the autocompletion feature to find hops.");
            return;
            // Handle not found
        }

        String hopUrl = hopDatabase.getHopUrl(hop);
        File hopDataImage = null;
        try {
            hopDataImage = hopDatabase.getHopDataChart(hop);
        } catch (IOException e) {
            e.printStackTrace();
        }

        MessageEmbed embed = new EmbedBuilder()
            .setColor(0x7CFC00)
            .setTitle(hop.name(), hopUrl)
            .setImage("attachment://" + hop.imageName())
            .addField(new Field("Purpose", "Dual", true))
            .addField(new Field("Country", "USA", true))
            .addField(new Field("Similiar hops:", "Simcoe, Citra, etc.", false))
            .build();

        MessageCreateData message = new MessageCreateBuilder()
            .setFiles(FileUpload.fromData(hopDataImage, hop.imageName()))
            .setEmbeds(embed)
            .build();

        event.reply(message).queue();

    }

    void handleHopAutoComplete(CommandAutoCompleteInteractionEvent event) {
        List<Choice> hopChoicesMatching = hopDatabase.getHopChoicesMatching(event.getFocusedOption().getValue());
        event.replyChoices(hopChoicesMatching).queue();
    }
}
