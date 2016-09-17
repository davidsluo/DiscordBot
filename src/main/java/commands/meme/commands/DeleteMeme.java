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
public class DeleteMeme extends CommandD4J{

    private static final String               prefix              = "?";
    private static final String               name                = "Delete Meme";
    private static final String               description         = "Delete a specified meme by its ID";
    private static final String               usage               = "?delmeme";
    private static final List<String>         aliases             = Collections.singletonList("delmeme");
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

    public DeleteMeme() {
        super(prefix, name, description, usage, aliases, isMain, isEnabled, isEssential, subCommandMap, subCommandNames, permissions, requireMention, allowPrivateMessage, forcePrivateReply, removeCallMessage);
    }

    @Override
    public Optional execute(List<String> args, MessageReceivedEvent event, MessageBuilder builder) throws IllegalAccessException, InvocationTargetException {
        String message = "";

        final String INVALID_SYNTAX = "Syntax error.";
        final String DOES_NOT_EXIST;
        final String SUCCESS;

        if (args.size() >= 1) {
            try {
                int id = Integer.valueOf(args.get(0));

                DOES_NOT_EXIST = "Meme with ID **" + id + "** does not exist.";
                SUCCESS = "Meme with ID **" + id + "** deleted.";

                ArrayList<Meme> meme = DB.loadMeme(MemeDatabase.DB_COLUMN.ID, id);

                if (meme.size() == 0) {
                    message = DOES_NOT_EXIST;
                } else {
                    DB.removeMemeById(id);
                    message = SUCCESS;
                }

            } catch (NumberFormatException e) {
                message = INVALID_SYNTAX;
            }
        } else {
            message = INVALID_SYNTAX;
        }

        CommandUtils.messageRequestBuffer(message, event, builder, name);
        return Optional.empty();
    }
}