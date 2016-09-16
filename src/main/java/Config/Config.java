package config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

/**
 * Created by David on 9/14/2016.
 */
public class Config {
    private static final String CONFIG_PATH = "config.prop";
    private static final Logger LOGGER      = LoggerFactory.getLogger(Config.class);
    private static       Config config      = new Config();

    private String botName       = "DiscordBot";
    private String discordToken  = "";
    private String imgurClientId = "";


    public static Config getConfig() {
        return config;
    }

    private Config() {
        Properties props = loadProps();
        botName = props.getProperty("bot_name", botName);
        discordToken = props.getProperty("discord_token");
        imgurClientId = props.getProperty("imgur_client_id");
    }

    private Properties loadProps() {
        Properties props = new Properties();
        try (InputStream stream = new FileInputStream(new File(CONFIG_PATH))) {
            props.load(stream);
        } catch (FileNotFoundException e) {
            LOGGER.warn(CONFIG_PATH + " file not found!");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return props;
    }

    public String getBotName() {
        return botName;
    }

    public String getDiscordToken() {
        return discordToken;
    }

    public String getImgurClientId() {
        return imgurClientId;
    }
}
