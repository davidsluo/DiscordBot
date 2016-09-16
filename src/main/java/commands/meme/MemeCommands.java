package commands.meme;

import com.github.alphahelix00.discordinator.d4j.commands.utils.CommandUtils;
import com.github.alphahelix00.ordinator.commands.MainCommand;
import commands.MonospaceTable;
import config.Config;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.util.MessageBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Created by David on 9/15/2016.
 */
// TODO: 9/16/2016 Make each command their own class?
public class MemeCommands {

    MemeDatabase database;

    private Logger LOGGER = LoggerFactory.getLogger(MemeCommands.class);

    public MemeCommands() {
        database = MemeDatabase.getMemeDatabase();
    }

    @MainCommand(
            prefix = "?",
            name = "Meme",
            alias = {"meme"},
            description = "Get a random dank may may",
            usage = "?meme"
    )
    public void meme(List<String> args, MessageReceivedEvent event, MessageBuilder builder) {
        Meme meme = database.loadRandomMeme();

        String message = (meme != null) ? meme.toString() : "No memes in database. Add some with ?addmeme <name of meme> <link to meme>";

        CommandUtils.messageRequestBuffer(message, event, builder, "Meme");
    }

    @MainCommand(
            prefix = "?",
            name = "Add Meme",
            alias = {"addmeme"},
            description = "Add a meme to the database",
            usage = "?addmeme <name of meme> <link to meme>"
    )
    public void addMeme(List<String> args, MessageReceivedEvent event, MessageBuilder builder) {

        String name;
        String link;
        String owner;
        String message = "";

        URL          url;
        final String SUCCESS;
        final String INVALID_URL;
        final String INVALID_SYNTAX = "Invalid syntax.";
        final String IMGUR_API_URL = "https://api.imgur.com/3/album/";

        final String[] embeddableExtensions = {
                "png",
                "jpeg",
                "jpg",
                "gifv",
                "gif"
//                "mp4",
        };

        final String[] embeddableWebsites = {
                "imgur.com",
                "youtube.com",
                "vimeo.com",
                "soundcloud.com",
                "twitch.tv",
                "gfycat.com"
        };

//        not perfect, but works well enough, i think
        Function<URL, Boolean> isEmbeddable = s -> {
            for (String host : embeddableWebsites)
                if (s.getHost().equals(host))
                    return true;

            for (String ext : embeddableExtensions)
                if (s.getPath().endsWith(ext))
                    return true;

            return false;
        };

        if (args.size() >= 2) {
            SUCCESS = "Successfully added meme **" + args.get(0) + "**.";
            INVALID_URL = "Invalid URL: " + args.get(1) + ".";

            name = args.get(0);
            link = args.get(1);
            owner = event.getMessage().getAuthor().getName();

            while (message.equals("")) {
                try {
                    url = new URL(link);

                    if (isEmbeddable.apply(url)) {

                        database.addMeme(new Meme(name, link, owner));

                        message = SUCCESS;

                    } else {
                        message = INVALID_URL;
                    }

                } catch (MalformedURLException e) {
                    if (e.getMessage().substring(0, 12).equals("no protocol: ")) {
                        link = "http://" + link;
                    } else {
                        message = INVALID_URL;
                    }
                }
            }
        } else if (args.size() >= 1) {

            link = args.get(0);

            INVALID_URL = "Invalid URL: " + args.get(0) + ".";

            while (message.equals("")) {
                try {
                    url = new URL(link);

                    if (!(url.getHost().equals("imgur.com") || url.getHost().equals("i.imgur.com"))) {
                        message = INVALID_URL;
                        break;
                    }

                    String[] splitPath = url.getPath().split("/");

                    if (splitPath.length != 3 || !splitPath[1].equals("a")) {
                        message = INVALID_URL;
                        break;
                    }

                    URL imgurUrl = new URL(IMGUR_API_URL + splitPath[2]);

                    HttpURLConnection connection = (HttpURLConnection) imgurUrl.openConnection();

                    connection.setRequestMethod("GET");

                    connection.setRequestProperty("Authorization", "Client-ID " + Config.getConfig().getImgurClientId());

                    int responseCode = connection.getResponseCode();

                    if (responseCode != HttpURLConnection.HTTP_OK) {
                        message = "Error retrieving album data. HTTP Response code: " + responseCode;
                        break;
                    }

                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(connection.getInputStream())
                    );

                    String line;

                    StringBuilder response = new StringBuilder();

                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }

                    in.close();

                    JSONObject json = new JSONObject(response.toString()).getJSONObject("data");
                    JSONArray images = json.getJSONArray("images");

                    for (int i = 0; i < images.length(); i++) {
                        Object title = images.getJSONObject(i).get("title");
                        String imageTitle =
                                title instanceof String ? (String) title : images.getJSONObject(i).getString("id");
                        String imageLink = images.getJSONObject(i).getString("link");

                        // TODO: 9/16/2016 unecessary?
                        if (imageTitle == null || imageTitle.equals("")) {
                            imageTitle = images.getJSONObject(i).getString("id");
                        }

                        database.addMeme(new Meme(imageTitle, imageLink, event.getMessage().getAuthor().getName()));
                    }

                    message = "Added " + images.length() + " new memes.";

                } catch (MalformedURLException e) {
                    if (e.getMessage().substring(0, 12).equals("no protocol: ")) {
                        link = "http://" + link;
                    } else {
                        message = INVALID_URL;
                    }
                } catch (IOException e) {
                    message = "Error: " + e.getMessage();
                }
            }

        } else {
            message = INVALID_SYNTAX;
        }

        CommandUtils.messageRequestBuffer(message, event, builder, "Add Meme");
    }

    @MainCommand(
            prefix = "?",
            name = "Delete Meme",
            alias = {"delmeme", "deletememe"},
            description = "Delete a meme by meme ID. Use !listmemes to get a list of memes and their IDs.",
            usage = "?delmeme <meme id>"

    )
    public void delMeme(List<String> args, MessageReceivedEvent event, MessageBuilder builder) {
        String message = "";

        final String INVALID_SYNTAX = "Syntax error.";
        final String DOES_NOT_EXIST;
        final String SUCCESS;

        if (args.size() >= 1) {
            try {
                int id = Integer.valueOf(args.get(0));

                DOES_NOT_EXIST = "Meme with ID **" + id + "** does not exist";
                SUCCESS = "Meme with ID **" + id + "** deleted";

                ArrayList<Meme> meme = database.loadMeme(MemeDatabase.DB_COLUMN.ID, id);

                if (meme.size() == 0) {
                    message = DOES_NOT_EXIST;
                } else {
                    database.removeMemeById(id);
                    message = SUCCESS;
                }

            } catch (NumberFormatException e) {
                message = INVALID_SYNTAX;
            }
        } else {
            message = INVALID_SYNTAX;
        }

        CommandUtils.messageRequestBuffer(message, event, builder, "Delete Meme");
    }


    @MainCommand(
            prefix = "?",
            name = "List Memes",
            alias = {"listmemes", "memelist"},
            description = "List all memes",
            usage = "!listmemes"
    )
    public void listMemes(List<String> args, MessageReceivedEvent event, MessageBuilder builder) {

        String message;

        MonospaceTable table = new MonospaceTable("ID", "Name", "Owner", "Date Added", "Link");

        for (Meme meme : database.getMemeArray())
            table.add(String.valueOf(meme.getId()), meme.getName(), meme.getOwner(), String.valueOf(meme.getTimestamp()), meme.getLink());

        message = "```" + table.toString() + "```";

        CommandUtils.messageRequestBuffer(message, event, builder, "List Memes");
    }

//    @MainCommand(
//            prefix = "?",
//            name = "test command",
//            alias = {"test"},
//            description = "test command"
//    )
//    public void test(List<String> args, MessageReceivedEvent event, MessageBuilder builder) {
//        String message = "";
//        URL    url;
//
//        try {
//            url = new URL(args.get(0));
//            message = url.getFile();
//
//        } catch (IOException e) {
//            message = e.getMessage();
//        }
//
//        CommandUtils.messageRequestBuffer(message, event, builder, "test command");
//    }

}
