package online.monkegame.monkemodmail.utils;

import online.monkegame.monkemodmail.Main;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SuspicionLevel {

    public Database db;
    public Main plugin;
    public SuspicionLevel(Main plugin) {
        this.plugin = plugin;
        this.db = new Database(plugin);
    }

    Float output;
    public String checkSuspicion(String uuid, Player p) {
        int reports = db.countReports();
        //gets the report weights from the database
        //this list will hold them before they're converted to an array
        List<Integer> a = new ArrayList<>();
        String reportType =
                "SELECT reason " +
                "FROM reports " +
                "WHERE uuid='" + uuid + "'";
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + db.path + "files.db");
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(reportType);
            while (rs.next()) {
                //adds report weights to the list
                a.add(rs.getInt("reason"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Object[] similarReports = a.toArray();
        //clunky conversion but it works
        int values = 0;
        int size = similarReports.length;
        for (Object o : similarReports) {
            values = values + (int) o;
        }

        //this bit does the magic calculation stuff
        if (size == 0 || values == 0 ) {
            return "Distrust too low to make a judgement!";
        }
        float averageReport = (values) / size;
        int playtime = p.getPlayer().getStatistic(Statistic.PLAY_ONE_MINUTE) / 7200;
        output = (Float)((float)(averageReport * (reports * 0.37f) - Math.pow(0.01 * playtime, 1.12)+1));
        //and returns it
        if (output <= 0 || output.isNaN()) {
            return "Distrust too low to make a judgement!";
        } else if (output >10) {
            return String.format("%.2f", output) + "\n**This player has very high distrust!**";
        } else {
            return String.format("%.2f", output);
        }
    }

}
