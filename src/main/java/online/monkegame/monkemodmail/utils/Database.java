package online.monkegame.monkemodmail.utils;

import online.monkegame.monkemodmail.Main;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;
import org.checkerframework.checker.units.qual.A;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class Database {

    public String path = System.getProperty("user.dir") + System.getProperty("file.separator") + "plugins" + System.getProperty("file.separator") + "monkeModMail" +  System.getProperty("file.separator");

    String reportsTable =
            "CREATE TABLE IF NOT EXISTS reports(" +
                    "'uuid' TEXT, " +
                    "'reason' TEXT)";
    private Main plugin;
    public Database(Main plugin) {
        this.plugin = plugin;
    }

    //creates database with corresponding tables
    public void createDB(Logger l) throws IOException {
        Thread thread = new Thread();
        thread.start();
        File db = new File(path + "files.db");
        boolean dbY = db.createNewFile();
        if (dbY) {
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + path + "files.db");
                 Statement stmt = conn.createStatement()) {
                stmt.execute(reportsTable);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            l.info("Database created!");
        } else {
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + path + "files.db");
                 Statement stmt = conn.createStatement()) {
                stmt.execute(reportsTable);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            l.info("Database already exists, using existing database!");
        }
    }

    //inserts the report into the db
    public void insertReport(String uuid, Short reason) throws SQLException {
        BukkitScheduler b = Bukkit.getScheduler();
        b.runTaskLaterAsynchronously(plugin, ()-> {
            try (Connection c = DriverManager.getConnection("jdbc:sqlite:" + path + "files.db");
                 Statement p = c.createStatement()) {
                p.execute("INSERT INTO reports VALUES('" + uuid + "','" + reason + "')");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, 10L);
    }

    //gets the amount of reports
    public int countReports() {
        AtomicInteger amount = new AtomicInteger();
        BukkitScheduler b = Bukkit.getScheduler();
        b.runTaskAsynchronously(plugin, () -> {

            String amountOfReports =
                    "SELECT COUNT(reason) AS reports " +
                    "FROM reports;";
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + path + "files.db");
                 Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery(amountOfReports);
                while (rs.next()) {
                    amount.set(rs.getInt("reports"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        return amount.get() +1;
    }

    public int countReportsPerPlayer(String uuid) {
        AtomicInteger amount = new AtomicInteger();
        BukkitScheduler b = Bukkit.getScheduler();
        b.runTaskAsynchronously(plugin, () -> {

            String amountOfReports =
                    "SELECT COUNT(reason) AS reports " +
                    "FROM reports " +
                    "WHERE uuid='"+uuid+"';";
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + path + "files.db");
                 Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery(amountOfReports);
                while (rs.next()) {
                    amount.set(rs.getInt("reports"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        return amount.get() +1;
    }

}
