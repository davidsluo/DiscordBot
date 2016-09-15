package commands.meme;

import com.github.alphahelix00.discordinator.d4j.commands.utils.CommandUtils;
import com.github.alphahelix00.ordinator.commands.MainCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.util.MessageBuilder;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by David on 9/15/2016.
 */
public class MemeCommands {

    MemeDatabase database;

    private Logger LOGGER = LoggerFactory.getLogger(MemeCommands.class);

    public MemeCommands() {
        database = MemeDatabase.getMemeDatabase();
    }

    @MainCommand(
            prefix = "!",
            name = "Meme",
            alias = {"meme"},
            description = "Get a random dank may may",
            usage = "!meme"
    )
    public void meme(List<String> args, MessageReceivedEvent event, MessageBuilder builder) {
        Meme meme = database.loadRandomMeme();

        String message = (meme != null) ? meme.toString() : "No memes in database. Add some with !addmeme <name of meme> <link to meme>";

        CommandUtils.messageRequestBuffer(message, event, builder, "Meme");
    }

    @MainCommand(
            prefix = "!",
            name = "Add Meme",
            alias = {"addmeme"},
            description = "Add a meme to the database",
            usage = "!addmeme <name of meme> <link to meme>"
    )
    public void addMeme(List<String> args, MessageReceivedEvent event, MessageBuilder builder) {

        String       message;
        final String SUCCESS        = "Successfully added meme **" + args.get(0) + "**.";
        final String INVALID_URL    = "Invalid URL: " + args.get(1) + ".";
        final String INVALID_SYNTAX = "Invalid syntax.";

        if (args.size() >= 2) {
            // TODO: 9/15/2016 add this url checking to meme object?
            try {
                new URL(args.get(1));

                database.addMeme(new Meme(args.get(0), args.get(1), event.getMessage().getAuthor().getName()));
                message = SUCCESS;
            } catch (MalformedURLException e) {
                if (e.getMessage().equals("no protocol: " + args.get(1))) {
                    try {
                        new URL("http://" + args.get(1));

                        database.addMeme(new Meme(args.get(0), "http://" + args.get(1), event.getMessage().getAuthor().getName()));
                        message = SUCCESS;

                    } catch (MalformedURLException e1) {
                        message = INVALID_URL;
                    }
                } else {
                    message = INVALID_URL;
                }
            }
        } else {
            message = INVALID_SYNTAX;
        }

        CommandUtils.messageRequestBuffer(message, event, builder, "Add Meme");
    }

//    @MainCommand(
//            prefix = "!",
//            name = "Delete Meme",
//            alias = {"delmeme", "deletememe"},
//            description = "Delete a meme"
//    )

//    @MainCommand(
//            prefix = "!",
//            name = "test command",
//            alias = {"test"},
//            description = "test command"
//    )
//    public void test(List<String> args, MessageReceivedEvent event, MessageBuilder builder) {
//        String message = "";
//
//        for (String arg : args) {
//            message += arg;
//        }
//
//        CommandUtils.messageRequestBuffer(message, event, builder, "test command");
//    }
}
