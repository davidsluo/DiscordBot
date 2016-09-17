package commands.meme.commands;

import com.github.alphahelix00.discordinator.d4j.commands.CommandD4J;
import com.github.alphahelix00.discordinator.d4j.commands.utils.CommandUtils;
import commands.meme.Meme;
import commands.meme.MemeDatabase;
import config.Config;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.MessageBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Created by David on 9/16/2016.
 */
public class AddMeme extends CommandD4J {

    private static final String               prefix              = "?";
    private static final String               name                = "Add Meme";
    private static final String               description         = "Add a meme to the database.";
    private static final String               usage               = "?addmeme <linkArg to meme or album> [nameArg of meme]";
    private static final List<String>         aliases             = Collections.singletonList("addmeme");
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

    private static final Logger LOGGER = LoggerFactory.getLogger(AddMeme.class);


    private static final String SUCCESS_PATTERN     = "%d meme%s successfully added.";
    private static final String INVALID_URL_PATTERN = "Invalid URL: ";
    private static final String INVALID_SYNTAX      = "Invalid syntax.";
    private static final String IMGUR_API_URL       = "https://api.imgur.com/3/album/";

    private static final String[] embeddableExtensions = {"png", "jpeg", "jpg", "gifv", "gif"};
    private static final String[] embeddableWebsites   = {"imgur.com", "youtube.com", "vimeo.com", "soundcloud.com", "twitch.tv", "gfycat.com"};

    public AddMeme() {
        super(prefix, name, description, usage, aliases, isMain, isEnabled, isEssential, subCommandMap, subCommandNames, permissions, requireMention, allowPrivateMessage, forcePrivateReply, removeCallMessage);
    }

    /**
     * Checks the url against a whitelist of url types.
     *
     * @param link The link to be checked.
     * @return If the link can be embedded.
     */
    private boolean isEmbeddable(URL link) {
        if (link == null)
            return false;

        for (String host : embeddableWebsites)
            if (link.getHost().contains(host))
                return true;

        for (String ext : embeddableExtensions)
            if (link.getPath().endsWith(ext))
                return true;

        return false;
    }

    /**
     * Tries to retrieve the links
     *
     * @param imgurAlbum The imgur album whose links are to be retrieved.
     * @return A map of links with the titles of each image mapped to the link.  If there is no title, the image ID is used instead.
     */
    private HashMap<String, String> getImgurAlbum(URL imgurAlbum) {
        LOGGER.info("Attempting to retrieve imgur album: " + imgurAlbum.toString());
        // Verify that url is valid imgur album.
        if (!imgurAlbum.toString().contains("imgur.com/a/")) {
            LOGGER.warn("Invalid imugr album: " + imgurAlbum.toString());
            return null;
        }

        String[] splitUrl = imgurAlbum.getPath().split("/");
        String   albumId  = splitUrl[splitUrl.length - 1];

        try {
            URL imgurApiUrl = new URL(IMGUR_API_URL + albumId);

            HttpURLConnection connection = (HttpURLConnection) imgurApiUrl.openConnection();

            connection.setRequestMethod("GET");

            connection.setRequestProperty("Authorization", "Client-ID " + Config.getConfig().getImgurClientId());

            int responseCode = connection.getResponseCode();

            if (responseCode != HttpURLConnection.HTTP_OK) {
                LOGGER.warn("Invalid imgur response code: " + responseCode);
                return null;
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

//            String line;
            StringBuilder response = new StringBuilder();
//
//            while ((line = in.readLine()) != null) {
//                response.append(line);
//            }

            for (String line = in.readLine(); line != null; response.append(line)) ;

            in.close();

            // Parse json
            JSONArray images = new JSONObject(response.toString()).getJSONObject("data").getJSONArray("images");

            HashMap<String, String> album = new HashMap<>();
            for (int i = 0; i < images.length(); i++) {
                Object title      = images.getJSONObject(i).get("title");
                String imageTitle = title instanceof String ? (String) title : images.getJSONObject(i).getString("id");
                String imageLink  = images.getJSONObject(i).getString("link");

                LOGGER.info("Adding meme: " + imageTitle + " - " + imageLink);

                album.put(imageTitle, imageLink);
            }

            LOGGER.info("Successufully retrieved imgur album " + imgurAlbum.toString());
            return album;
        } catch (MalformedURLException e) {
            LOGGER.warn("Invalid imgur API URL.", e);
            return null;
        } catch (IOException e) {
            LOGGER.warn("Error receiving album contents.", e);
            return null;
        }
    }

    /**
     * Validates the link
     *
     * @param link The link to be validated.
     * @return If the link is a valid link.
     */
    private URL validateLink(String link) {
        LOGGER.info("Validating link: " + link);
        try {
            return new URL(link);
        } catch (MalformedURLException e) {
            if (e.getMessage().contains("no protocol"))
                try {
                    return new URL("https://" + link);
                } catch (MalformedURLException e1) {
                    try {
                        return new URL("http://" + link);
                    } catch (MalformedURLException e2) {
                        return null;
                    }
                }
            else
                return null;

        }
    }

    private String getMemeName(List<String> args) {
        StringBuilder builder = new StringBuilder();

        for (int i = 1; i < args.size(); i++) {
            builder.append(args.get(i));
            if (i < args.size() - 1)
                builder.append(" ");
        }

        return builder.toString();
    }

    @Override
    public Optional execute(List<String> args, MessageReceivedEvent event, MessageBuilder builder) throws IllegalAccessException, InvocationTargetException {

        String message = "";
        String owner   = event.getMessage().getAuthor().getName();

        if (args.size() <= 0) {
            message = INVALID_SYNTAX;
        } else {

            URL linkURL = validateLink(args.get(0));

            if (linkURL != null && isEmbeddable(linkURL)) {
                if (linkURL.toString().contains("imgur.com/a/")) {
                    HashMap<String, String> images = getImgurAlbum(linkURL);

                    if (images != null) {

                        int counter = 0;

                        for (Map.Entry<String, String> image : images.entrySet()) {
                            DB.addMeme(new Meme(image.getKey(), image.getValue(), owner));
                            counter++;
                        }
                        message = String.format(SUCCESS_PATTERN, counter, counter == 1 ? "" : "s");
                    } else {
                        message = INVALID_URL_PATTERN + args.get(0);
                    }

                } else {
                    if (args.size() >= 2) {
                        String memeName = getMemeName(args);
                        DB.addMeme(new Meme(memeName, linkURL.toString(), owner));

                        message = String.format(SUCCESS_PATTERN, 1, "");
                    } else {
                        message = INVALID_SYNTAX;
                    }
                }
            } else {
                message = INVALID_URL_PATTERN + args.get(0);
            }

        }


        CommandUtils.messageRequestBuffer(message, event, builder, "Add Meme");
        return Optional.empty();
    }
}
