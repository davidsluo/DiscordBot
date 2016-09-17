package commands.meme.commands;

import com.github.alphahelix00.discordinator.d4j.commands.CommandD4J;
import com.github.alphahelix00.discordinator.d4j.commands.utils.CommandUtils;
import commands.meme.Meme;
import commands.meme.MemeDatabase;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.MessageBuilder;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Created by David on 9/16/2016.
 */
public class RandomMeme extends CommandD4J {

    private static final String               prefix              = "?";
    private static final String               name                = "Meme";
    private static final String               description         = "Get a random dank may may.";
    private static final String               usage               = "?meme";
    private static final List<String>         aliases             = Collections.singletonList("meme");
    private static final boolean              isMain              = true;
    private static final boolean              isEnabled           = true;
    private static final boolean              isEssential         = false;
    private static final Map                  subCommandMap       = new HashMap<>();
    private static final Map                  subCommandNames     = new HashMap<>();
    private static final EnumSet<Permissions> permissions         = EnumSet.of(Permissions.READ_MESSAGES, Permissions.SEND_MESSAGES);
    private static final boolean              requireMention      = false;
    private static final boolean              allowPrivateMessage = true;
    private static final boolean              forcePrivateReply   = false;
    private static final boolean              removeCallMessage   = false;

    private static final MemeDatabase DB = MemeDatabase.getMemeDatabase();

    public RandomMeme() {
        super(prefix, name, description, usage, aliases, isMain, isEnabled, isEssential, subCommandMap, subCommandNames, permissions, requireMention, allowPrivateMessage, forcePrivateReply, removeCallMessage);
    }

    @Override
    public Optional execute(List<String> args, MessageReceivedEvent event, MessageBuilder builder) throws IllegalAccessException, InvocationTargetException {
        Meme meme = DB.loadRandomMeme();

        String message = (meme != null) ? meme.toString() : "No memes in DB. Add some with ?addmeme <name of meme> <link to meme>";

        CommandUtils.messageRequestBuffer(message, event, builder, "Meme");

        return Optional.empty();
    }
}
