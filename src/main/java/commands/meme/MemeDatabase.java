package commands.meme;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;

/**
 * Created by David on 9/14/2016.
 */
public class MemeDatabase {

    private static final Logger LOGGER    = LoggerFactory.getLogger(MemeDatabase.class);
    private static final String MEME_FILE = "memebank.db";

    private static final String DATABASE = "meme_database";
    private static final String TABLE    = "meme_table";

    private enum DB_COLUMN {
        ID("INTEGER"),
        NAME("TEXT"),
        LINK("TEXT"),
        OWNER("TEXT"),
        TIMESTAMP("TIMESTAMP");

        final String colDataType;

        DB_COLUMN(String sqlDataType) {
            this.colDataType = sqlDataType;
        }
    }

    private static final MemeDatabase memes = new MemeDatabase();
    private ArrayList<Meme> memeArray;
    private Connection connection = null;

    public static MemeDatabase getMemeDatabase() {
        return memes;
    }

    private MemeDatabase() {
        memeArray = new ArrayList<>();

        connectToDatabase();
//        initDatabase();
//        selectDatabase();
        initTable();

        memeArray = loadAllMemes();
    }

    private void connectToDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + MEME_FILE);
            if (connection != null) {
                LOGGER.info("Connected to database.");
            } else {
                LOGGER.error("Could not connect to database.");
            }
        } catch (SQLException e) {
            LOGGER.error("ERROR CONNECTING TO DATABASE", e);
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            LOGGER.error("ERROR", e);
            e.printStackTrace();
        }
    }

    private void initDatabase() {
        LOGGER.info("Initializing database...");
        executeSQL(
                "CREATE DATABASE IF NOT EXISTS " + DATABASE + ";",
                "ERROR CREATING DATABASE"
        );
    }

    private void initTable() {
        LOGGER.info("Initializing table...");
        executeSQL(
                "CREATE TABLE IF NOT EXISTS " + TABLE + " (" +
                        DB_COLUMN.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        DB_COLUMN.NAME + " TEXT NOT NULL, " +
                        DB_COLUMN.LINK + " TEXT NOT NULL, " +
                        DB_COLUMN.OWNER + " TEXT, " +
                        DB_COLUMN.TIMESTAMP + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP);",
                "ERROR INITIALIZING TABLE"
        );
    }

    private void selectDatabase() {
        executeSQL("USE " + DATABASE + ");", "ERROR SELECTING DATABASE");
    }

//    private void executeSQL(String sql) {
//        executeSQL(sql, null);
//    }

    private void executeSQL(String sql, String errorMessage) {
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            if (errorMessage != null && !errorMessage.equals("")) {
                LOGGER.error(errorMessage, e);
            } else {
                LOGGER.error("ERROR EXECUTING SQL", e);
            }
        }
    }

//    private void loadAllMemes() {
//        LOGGER.info("Loading dank memes...");
//
//        ResultSet result = null;
//
//        try (Statement statement = connection.createStatement()) {
//            result = statement.executeQuery(
//                    "SELECT * FROM " + TABLE + ";"
//            );
//
//            while (result.next()) {
//                memeArray.add(
//                        new Meme(
//                                result.getInt(1),       //ID
//                                result.getString(2),    //Name
//                                result.getString(3),    //Link
//                                result.getString(4),    //Owner
//                                result.getTimestamp(5)  //Timestamp
//                        )
//                );
//            }
//
//        } catch (SQLException e) {
//            LOGGER.error("ERROR LOADING MEMES", e);
//        } finally {
//            if (result != null) {
//                try {
//                    result.close();
//                } catch (SQLException ignored) {
//                }
//            }
//        }
//    }

    public ArrayList<Meme> loadAllMemes() {
        return loadMeme(null, null);
    }

    // TODO: 9/14/2016 batch load meme method?
    // TODO: 9/14/2016 multiple constraints?
    public ArrayList<Meme> loadMeme(DB_COLUMN column, Object query) {

        String          whereQuery = "";
        ArrayList<Meme> memeArr    = new ArrayList<>();
        ResultSet       result     = null;

        if (column != null && query != null) {
            LOGGER.info("Loading specific meme(s) by " + column + "; query: " + query);
            whereQuery = " WHERE " + column + " = " + query;
        } else {
            LOGGER.info("Loading all memes...");
        }

        try (Statement statement = connection.createStatement()) {

            result = statement.executeQuery(
                    "SELECT * FROM " + TABLE + whereQuery
            );

            while (result.next()) {
                memeArr.add(
                        new Meme(
                                result.getInt(1),       //ID
                                result.getString(2),    //Name
                                result.getString(3),    //Link
                                result.getString(4),    //Owner
                                result.getTimestamp(5)  //Timestamp
                        )
                );
            }

            return memeArr;

        } catch (SQLException e) {
            LOGGER.error("ERROR LOADING SPECIFIC MEMES", e);
            return memeArr;
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
            } catch (SQLException e) {
                LOGGER.error("ERROR CLOSING RESULT SET", e);
                e.printStackTrace();
            }
        }

    }

    // TODO: 9/14/2016 batch remove meme method?
    // TODO: 9/14/2016 multiple constraints?
    public void removeMemeById(int id) {

        LOGGER.info("Deleting meme id " + id);

        try (PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM " + TABLE + " WHERE " + DB_COLUMN.ID + " = ?"
        )) {

            statement.setInt(1, id);

            if (statement.executeUpdate() > 0) {
                LOGGER.info("Successfully deleted meme ID: " + id);
            }

        } catch (SQLException e) {
            LOGGER.error("ERROR DELETING MEME", e);
        }
    }

    public void addMeme(Meme meme) {

        LOGGER.info("Adding " + meme.getName() + " meme.");

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO " + TABLE + " (" +
                        DB_COLUMN.NAME + ", " +
                        DB_COLUMN.LINK + ", " +
                        DB_COLUMN.OWNER + ", " +
                        DB_COLUMN.TIMESTAMP + ") VALUES (?, ?, ?, ?)"
        )) {


            statement.setString(1, meme.getName());
            statement.setString(2, meme.getLink());
            statement.setString(3, meme.getOwner());
            statement.setTimestamp(4, timestamp);


            // TODO: 9/14/2016 add verification that meme was added correctly?
            if (statement.executeUpdate() > 0) {
                LOGGER.info("Successfully added " + meme.getName() + ".");
            }
            memeArray.add(meme);
        } catch (SQLException e) {
            LOGGER.error("ERROR ADDING MEME", e);
        }
    }

    public Meme loadRandomMeme() {

        Meme      meme   = null;
        ResultSet result = null;

        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM " + TABLE + " ORDERY BY RANDOM() LIMIT 1)"
        )) {
            result = statement.executeQuery();

            if (result.next()) {
                meme = new Meme(
                        result.getInt(1),
                        result.getString(2),
                        result.getString(3),
                        result.getString(4),
                        result.getTimestamp(5)
                );
                LOGGER.info("Successfully loaded meme " + meme.getName() + ".");
            }
        } catch (SQLException e) {
            LOGGER.error("ERROR WHILE LOADING RANDOM MEME", e);
            e.printStackTrace();
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return meme;
    }
}
