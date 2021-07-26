package online.monkegame.monkemodmail.utils;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.UUID;

public class Database {

    public String path = System.getProperty("user.dir") + System.getProperty("file.separator") + "plugins" + System.getProperty("file.separator") + "monkeModMail" +  System.getProperty("file.separator");


    //creates database with corresponding tables
    public String createDB() throws IOException {

        File db = new File(path + "files.db");
        boolean dbY = db.createNewFile();
        if (dbY) {
            String reportsTable =
                    "CREATE TABLE IF NOT EXISTS reports(" +
                            "'uuid' TEXT, " +
                            "'reason' TEXT)";
            String suspicTable =
                    "CREATE TABLE IF NOT EXISTS suspicion(" +
                            "'uuid' TEXT, " +
                            "'level' INTEGER)";
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:"+ path + "files.db");
                 Statement stmt = conn.createStatement()) {
                stmt.executeQuery(reportsTable);
                stmt.executeQuery(suspicTable);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return "Database created!";
        } else {
            return "Database already exists, using existing database!";
        }
    }

    //inserts the report into the db
    public void insertReport(String uuid, String reason) throws SQLException {

        try (Connection c = DriverManager.getConnection("jdbc:sqlite:"+ path + "files.db");
             Statement p = c.createStatement()) {
            p.execute("INSERT INTO reports VALUES('"+uuid+"','"+reason+"')");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
