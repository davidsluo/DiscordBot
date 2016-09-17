package commands.meme.commands;

import com.github.alphahelix00.discordinator.d4j.commands.CommandD4J;
import com.github.alphahelix00.discordinator.d4j.commands.utils.CommandUtils;
import com.github.alphahelix00.ordinator.commands.MainCommand;
import commands.MonospaceTable;
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
public class ListMemes extends CommandD4J {

    private static final String               prefix              = "?";
    private static final String               name                = "List Memes";
    private static final String               description         = "List all memes.";
    private static final String               usage               = "?listmemes";
    private static final List<String>         aliases             = Arrays.asList("listmemes", "memelist");
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

    public ListMemes() {
        super(prefix, name, description, usage, aliases, isMain, isEnabled, isEssential, subCommandMap, subCommandNames, permissions, requireMention, allowPrivateMessage, forcePrivateReply, removeCallMessage);
    }

    @Override
    public Optional execute(List<String> list, MessageReceivedEvent event, MessageBuilder builder) throws IllegalAccessException, InvocationTargetException {
        String message;

        MonospaceTable table = new MonospaceTable("ID", "Name", "Owner", "Date Added", "Link");

        for (Meme meme : DB.getMemeArray())
            table.add(String.valueOf(meme.getId()), meme.getName(), meme.getOwner(), String.valueOf(meme.getTimestamp()), meme.getLink());

        message = "```" + table.toString() + "```";

        CommandUtils.messageRequestBuffer(message, event, builder, name);
        return Optional.empty();
    }
}
