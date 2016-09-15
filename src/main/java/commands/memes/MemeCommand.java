package commands.memes;

import com.github.alphahelix00.ordinator.commands.MainCommand;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.util.MessageBuilder;

import java.util.List;

/**
 * Created by David on 9/14/2016.
 */
public class MemeCommand {

    public MemeCommand() {

    }

    @MainCommand(
            prefix = "!",
            name = "meme",
            alias = {"meme"},
            description = "memes"
    )
    public void meme(List<String> args, MessageReceivedEvent event, MessageBuilder messageBuilder) {

    }
}
