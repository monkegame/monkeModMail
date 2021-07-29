package online.monkegame.monkemodmail.utils;

import online.monkegame.monkemodmail.Main;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SuspicionLevel {

    public Database db;
    public Main plugin;
    public SuspicionLevel(Main plugin) {
        this.plugin = plugin;
        this.db = new Database(plugin);
    }

        //similar reports +
        //amount of reports ++
        //playtime ~
    Float output;
    public String checkSuspicion(String uuid, Player p) {

        List<Integer> a = new ArrayList<>();
        String reportType =
                "SELECT reason " +
                "FROM reports " +
                "WHERE uuid='" + uuid + "'";
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + db.path + "files.db");
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(reportType);
            while (rs.next()) {
                a.add(rs.getInt("reason"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Object[] similarReports = a.toArray();

        int values = 0;
        int size = similarReports.length;
        for (Object o : similarReports) {
            values = values + (Integer.parseInt(o.toString()));
        }

        float averageReport = (values * 0.7f) / size;
        int reports = db.countReports();
        int playtime = p.getPlayer().getStatistic(Statistic.PLAY_ONE_MINUTE) / 7200;
        output = (Float)((float)(averageReport * (reports * 0.37f) - Math.pow(0.01 * playtime, 1.12)+1));

        if (output <= 0 || output.isNaN()) {
            return "Distrust too low to make a judgement!";
        } else if (output >10) {
            return String.format("%.2f", output) + "\n**This player has very high distrust!**";
        } else {
            return String.format("%.2f", output);
        }
    }

}
