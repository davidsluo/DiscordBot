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

    private String botName = "DiscordBot";
    private String token   = "";


    public static Config getConfig() {
        return config;
    }

    private Config() {
        Properties props = loadProps();
        botName = props.getProperty("bot_name", botName);
        token = props.getProperty("token");
    }

    public String getBotName() {
        return botName;
    }

    public String getToken() {
        return token;
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


}
