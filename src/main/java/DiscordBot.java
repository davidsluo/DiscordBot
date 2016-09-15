import com.github.alphahelix00.discordinator.d4j.handler.CommandHandlerD4J;
import com.github.alphahelix00.ordinator.Ordinator;
import commands.memes.MemeCommand;
import config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.util.DiscordException;

/**
 * Created by David on 9/13/2016.
 */
public class DiscordBot {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiscordBot.class);

    private static final DiscordBot bot = new DiscordBot();
    private       IDiscordClient    client;
    private final Config            config;
    private       CommandHandlerD4J commandHandler;

    public static DiscordBot getBot() {
        return bot;
    }

    private DiscordBot() {
        config = Config.getConfig();
        try {
            LOGGER.info("Logging in...");
            login();
        } catch (DiscordException e) {
            e.printStackTrace();
        }

        commandHandler = (CommandHandlerD4J) Ordinator.getCommandRegistry().getCommandHandler();

        registerCommands();
    }

    private void registerCommands() {
        commandHandler.registerAnnotatedCommands(new MemeCommand());
    }

    public void login() throws DiscordException {
        client = new ClientBuilder().withToken(config.getToken()).login();

    }

    public IDiscordClient getClient() {
        return client;
    }

}
