package storage;

/** @file DatabaseStorage.java
 * Code that connects the rest of the application to the database. Persistent storage layer of the server-side code.
 */

import security.HashedPassword;

import java.sql.*;

/**
 * JDBC-based class that connects the application to the database layer. Uses JDBC to talk to MariaDB/MySQL.
 */
public class DatabaseStorage {
    /**
     * The database connection.
     */
    private Connection connection;

    /**
     * Parameterized SQL query to check if a user with a given username/email exists.
     */
    private PreparedStatement userExistsStatement;

    /**
     * Parameterized SQL query to create a new user.
     */
    private PreparedStatement createUserStatement;

    /**
     * Parameterized SQL query to validate a user (for logging in).
     */
    private PreparedStatement validateUserStatement;

    private PreparedStatement createMediaStatement;

    private PreparedStatement getMediaStatement;

    private PreparedStatement createCardStatement;

    private PreparedStatement getCardStatement;

    private PreparedStatement createTagStatement;

    private PreparedStatement getTagIdStatement;

    private PreparedStatement getHashedPasswordStatement;

    private PreparedStatement tagCardStatement;

    /**
     * Create a new DatabaseStorage object and connect to the DB.
     */
    public DatabaseStorage() {
        try {
            connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/carecards", "root", "none");
            userExistsStatement = connection.prepareStatement(Queries.userExists);
            createUserStatement = connection.prepareStatement(Queries.createUser, Statement.RETURN_GENERATED_KEYS);
            validateUserStatement = connection.prepareStatement(Queries.validateUser);
            createMediaStatement = connection.prepareStatement(Queries.createMedia, Statement.RETURN_GENERATED_KEYS);
            getMediaStatement = connection.prepareStatement(Queries.getMedia);
            createCardStatement = connection.prepareStatement(Queries.createCard, Statement.RETURN_GENERATED_KEYS);
            getCardStatement = connection.prepareStatement(Queries.getCard);
            createTagStatement = connection.prepareStatement(Queries.createTag, Statement.RETURN_GENERATED_KEYS);
            getTagIdStatement = connection.prepareStatement(Queries.getTagId);
            getHashedPasswordStatement = connection.prepareStatement(Queries.getHashedPassword);
            tagCardStatement = connection.prepareStatement(Queries.tagCard, Statement.RETURN_GENERATED_KEYS);
        }
        catch (SQLException exception) {
            exception.printStackTrace();
            connection = null;
        }
    }

    public synchronized int userExists(String username_or_email) {
        try {
            userExistsStatement.setString(1, username_or_email);
            userExistsStatement.setString(2, username_or_email);
            ResultSet results = userExistsStatement.executeQuery();
            if (results.first()) {
                return results.getInt("user_id");
            }
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
        return -1;
    }

    public synchronized int createUser(String username, String display_name, byte[] password_hash, byte[] password_salt, String email) {
        try {
            createUserStatement.setString(1, username);
            createUserStatement.setString(2, display_name);
            createUserStatement.setBytes(3, password_hash);
            createUserStatement.setBytes(4, password_salt);
            createUserStatement.setString(5, email);
            createUserStatement.executeUpdate();
            ResultSet generated_keys = createUserStatement.getGeneratedKeys();
            if (generated_keys.first()) {
                return generated_keys.getInt("user_id");
            }
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
        return -1;
    }

    public synchronized HashedPassword getHashedPassword(int user_id) {
        try {
            getHashedPasswordStatement.setInt(1, user_id);
            ResultSet results = getHashedPasswordStatement.executeQuery();
            if (results.first()) {
                return new HashedPassword(results.getBytes("password_salt"), results.getBytes("password_hash"));
            }
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public synchronized int validateUser(String username_or_email, byte[] password_hash) {
        try {
            validateUserStatement.setString(1, username_or_email);
            validateUserStatement.setString(2, username_or_email);
            validateUserStatement.setBytes(3, password_hash);
            ResultSet results = validateUserStatement.executeQuery();
            if (results.first()) {
                return results.getInt("user_id");
            }
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
        return -1;
    }

    public synchronized int createMedia(String mime_type, byte[] content) {
        try {
            createMediaStatement.setString(1, mime_type);
            createMediaStatement.setBytes(2, content);
            createMediaStatement.executeUpdate();
            ResultSet results = createMediaStatement.getGeneratedKeys();
            if (results.first()) {
                return results.getInt("media_id");
            }
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
        return -1;
    }

    public synchronized Media getMedia(int media_id) {
        try {
            getMediaStatement.setInt(1, media_id);
            ResultSet results = getMediaStatement.executeQuery();
            if (results.first()) {
                String media_mime_type = results.getString("media_mime_type");
                byte[] media_content = results.getBytes("media_content");
                return new Media(media_id, media_mime_type, media_content);
            }
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public synchronized int createCard(int user_id, int media_id, String title, String caption) {
        try {
            createCardStatement.setInt(1, user_id);
            createCardStatement.setInt(2, media_id);
            createCardStatement.setString(3, title);
            createCardStatement.setString(4, caption);
            createCardStatement.executeUpdate();
            ResultSet results = createCardStatement.getGeneratedKeys();
            if (results.first()) {
                return results.getInt("card_id");
            }
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
        return -1;
    }

    public synchronized Card getCard(int card_id) {
        try {
            getCardStatement.setInt(1, card_id);
            getCardStatement.setInt(2, card_id);
            getCardStatement.setInt(3, card_id);
            ResultSet results = getCardStatement.executeQuery();
            if (results.first()) {
                int user_id = results.getInt("user_id");
                int media_id = results.getInt("media_id");
                String title = results.getString("title");
                String caption = results.getString("caption");
                int likes = results.getInt("likes");
                String[] tags = results.getString("tags").split(",");
                return new Card(card_id, user_id, media_id, title, caption, likes, tags);
            }
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    private synchronized int getTagId(String content) {
        try {
            getTagIdStatement.setString(1, content);
            ResultSet results = getTagIdStatement.executeQuery();
            if (results.first()) {
                return results.getInt("tag_id");
            }
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
        return -1;
    }

    public synchronized int createTagOrFindExisting(String content) {
        try {
            createTagStatement.setString(1, content);
            createTagStatement.executeUpdate();
            ResultSet results = createTagStatement.getGeneratedKeys();
            if (results == null) {
                return getTagId(content);
            }
            if (results.first()) {
                return results.getInt("tag_id");
            }
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
        return -1;
    }

    public synchronized int tagCard(int card_id, int tag_id) {
        try {
            tagCardStatement.setInt(1, card_id);
            tagCardStatement.setInt(2, tag_id);
            tagCardStatement.executeUpdate();
            ResultSet results = tagCardStatement.getGeneratedKeys();
            if (results.first()) {
                return results.getInt("tagging_id");
            }
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
        return -1;
    }
}
