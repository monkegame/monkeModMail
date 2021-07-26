package online.monkegame.monkemodmail;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import online.monkegame.monkemodmail.utils.ColorGenerator;
import online.monkegame.monkemodmail.utils.Database;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.UUID;

public class ReportCommand implements CommandExecutor {

    public FileConfiguration co;
    JDA j;
    public Database db;
    public ColorGenerator cg;
    public ReportCommand(FileConfiguration config, JDA jda) {
        this.co = config;
        this.j = jda;
        this.db = new Database();
        this.cg = new ColorGenerator();
    }

    /*
    TODO report counter
    get the amount of entries (so, sum suspicion ?)
    TODO sus counter
    db stuff
     */

    String reason;
    String u;
    @Override
    public boolean onCommand(CommandSender s, Command c, String a, String[] args) {
        if (s instanceof Player && args!=null) {
            MessageChannel ch = j.getTextChannelById(co.getString("logging-channel"));
            if (args.length < 2) {
                s.sendMessage("Please specify a reason!");
                return false;
            } else {
                u = Bukkit.getPlayerUniqueId(args[0]).toString();
                System.out.println(Arrays.toString(args));
                reason = getReason(args).replace(args[0], "");
                if (reason.contains("DROP TABLE") || reason.contains("drop table") || reason.matches("[#-]")) {
                    s.sendMessage("Nice try.");
                    return false;
                }
            }

            try {
                db.insertReport(u, reason);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            MessageEmbed e = new EmbedBuilder()
                    .setTitle("Report x")
                    .setDescription("``" + args[0] + "``" + " has been reported by ``" + s.getName() + "``")
                    .addField("Reason", "``" + reason + "``", false)
                    .addField("", "Suspicion: ", false)
                    .setColor(cg.randomColor())
                    .build();

            ch.sendMessageEmbeds(e).submit();
            s.sendMessage("Player reported successfully.");
            return true;
        } else {
            return false;
        }
    }

    public String getReason(String[] o) {
        String b = "";
        for (String a : o) {
            b = b + " " + a;
        }
        return b;
    }

}
