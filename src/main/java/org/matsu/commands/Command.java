package org.matsu.commands;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.matsu.hops.HopCommandHandler;

import net.dv8tion.jda.api.events.Event;

public enum Command {


    HOP_SLASH("hops", CommandType.SLASH_COMMAND, HopCommandHandler::handleHopSlashCommand),
    HOP_AUTOCOMPLETE("hops", CommandType.AUTO_COMPLETE, HopCommandHandler::handleHopAutoComplete)
        ;

    private static final List<Command> commands = List.of(Command.values());

    private String commandString;
    private CommandType type;
    private Consumer<Event> callback;

    private Command(String commandString, CommandType type, Consumer<Event> callback) {
        this.commandString = commandString;
        this.type = type;
        this.callback = callback;
    }

    public static Optional<Command> getCommand(String commandString, CommandType type) {
        return commands.stream()
            .filter(comm -> comm.getCommandString().equals(commandString) && comm.getType().equals(type))
            .findFirst();
    }

    public void accept(Event event) {
        getCallback().accept(event);
    }

    public String getCommandString() {
        return commandString;
    }

    public CommandType getType() {
        return type;
    }

    public Consumer<Event> getCallback() {
        return callback;
    }
}
