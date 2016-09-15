package commands.meme;

import com.github.alphahelix00.discordinator.d4j.handler.CommandHandlerD4J;
import com.github.alphahelix00.ordinator.Ordinator;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.modules.IModule;

/**
 * Created by David on 9/15/2016.
 */
public class MemeModule implements IModule {
    private IDiscordClient client;

    @Override
    public boolean enable(IDiscordClient client) {
        this.client = client;
        CommandHandlerD4J commandHandlerD4J = (CommandHandlerD4J) Ordinator.getCommandRegistry().getCommandHandler();

        commandHandlerD4J.registerAnnotatedCommands(new MemeCommands());

        return true;
    }

    @Override
    public void disable() {

    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getAuthor() {
        return null;
    }

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public String getMinimumDiscord4JVersion() {
        return null;
    }
}
