package org.matsu.handlers;

import java.io.File;
import java.util.List;

import org.matsu.db.HopDatabase;
import org.matsu.http.Requester;
import org.matsu.pojo.Hop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public class HopCommandHandler {
    static HopDatabase hopDatabase = new HopDatabase();

    static Logger logger = LoggerFactory.getLogger(HopCommandHandler.class);
    static Requester http = new Requester();

    public static void handleHopSlashCommand(Event ev) {
        if (!(ev instanceof SlashCommandInteractionEvent event)) {
            return;
        }

        String hopName = event.getOption("hop_name").getAsString();
        Hop hop = hopDatabase.findHop(hopName);
        if (hop.equals(Hop.NOT_FOUND)) {
            event.reply("Hop not found. Please use the autocompletion feature to find hops.")
                .setEphemeral(true)
                .queue();
            return;
        }

        event.reply("Processing your request. Hold on a second.")
            .setEphemeral(true)
            .queue();
        // event.deferReply();


        String hopUrl = hopDatabase.getHopUrl(hop);
        File hopDataImage = hopDatabase.getHopDataChart(hop);

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

        event.getChannel().sendMessage(message).queue();
        //event.reply(message).queue();

    }

    public static void handleHopAutoComplete(Event ev) {
        logger.info("handleHopAutoComplete");
        if (!(ev instanceof CommandAutoCompleteInteractionEvent event)) {
            return;
        }

        logger.info("Handling request");
        List<Choice> hopChoicesMatching = hopDatabase.getHopChoicesMatching(event.getFocusedOption().getValue());
        event.replyChoices(hopChoicesMatching).queue();
    }
}
