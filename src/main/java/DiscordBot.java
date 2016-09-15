import com.github.alphahelix00.discordinator.d4j.DiscordinatorModule;
import commands.meme.MemeModule;
import config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RateLimitException;

/**
 * Created by David on 9/13/2016.
 */
public class DiscordBot {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiscordBot.class);

    private static final DiscordBot bot = new DiscordBot();
    private       IDiscordClient client;
    private final Config         config;

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

        client.getModuleLoader().loadModule(new DiscordinatorModule());
        client.getModuleLoader().loadModule(new MemeModule());
    }

    @EventSubscriber
    public void onReady(ReadyEvent event) {
        try {
            client.changeUsername(config.getBotName());
        } catch (DiscordException | RateLimitException e) {
            e.printStackTrace();
        }
    }

    public void login() throws DiscordException {
        client = new ClientBuilder().withToken(config.getToken()).login();

        client.getDispatcher().registerListener(this);


    }

    public IDiscordClient getClient() {
        return client;
    }

}
