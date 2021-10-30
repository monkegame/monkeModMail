package online.monkegame.monkemodmail.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import online.monkegame.monkemodmail.utils.ColorGenerator;
import online.monkegame.monkemodmail.utils.Database;
import online.monkegame.monkemodmail.utils.PlayerboundMessages;
import online.monkegame.monkemodmail.utils.SuspicionLevel;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import online.monkegame.monkemodmail.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.sql.SQLException;
import java.time.Instant;
import java.util.*;

public class ReportCommand implements CommandExecutor {

    public FileConfiguration co;
    JDA j;
    public Database db;
    public ColorGenerator cg;
    public SuspicionLevel sl;
    public PlayerboundMessages pbm;
    public Main plugin;
    public ReportCommand(FileConfiguration config, JDA jda, Main plugin) {
        this.plugin = plugin;
        this.sl = new SuspicionLevel(plugin);
        this.co = config;
        this.j = jda;
        this.db = new Database(plugin);
        this.cg = new ColorGenerator();
        this.pbm = new PlayerboundMessages(plugin);
    }

    String uAsString;
    UUID u;
    Short reason;
    Player p;
    Map<String, Long> cooldowns = new HashMap<>();
    @Override
    public boolean onCommand(CommandSender s, Command c, String a, String[] args) {
        int cooldownTime = co.getInt("command-cooldowns.report-cooldown");
        List<Object> l = getReasons(co);
        if (s instanceof Player && args != null && !s.getName().equals(args[0])) {
            MessageChannel ch = j.getTextChannelById(co.getString("discord.logging-channel"));
            long secondsLeft = 0;
            if (cooldowns.containsKey(s.getName())) {
                secondsLeft = ((cooldowns.get(s.getName()) / 1000) + cooldownTime) - (Instant.now().toEpochMilli() / 1000);
                if (secondsLeft > 0) {
                    s.sendMessage("Please wait "+secondsLeft+" more seconds!");
                    return true;
                } else {
                    cooldowns.remove(s.getName());
                }
            }

            if (args.length < 2 || !args[1].matches("[0-9]")) {
                s.sendMessage("Please specify a report category!");
                pbm.sendReasons(s, l);
                return false;
            } else {
                    cooldowns.put(s.getName(), Instant.now().toEpochMilli());
                    u = Bukkit.getPlayerUniqueId(args[0]);
                    uAsString = u.toString();
                    int countReports = db.countReportsPerPlayer(uAsString);
                    p = Bukkit.getPlayer(u);
                    reason = Short.parseShort(args[1]);
                    if ((reason > l.size() || reason < 1) && args[1].matches("[0-9]")) {
                        s.sendMessage("Bad ID! Here are the report category IDs you can use:");
                        cooldowns.remove(s.getName());
                        pbm.sendReasons(s, l);
                        return true;
                    }

                    try {
                        db.insertReport(uAsString, reason);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    MessageEmbed e = new EmbedBuilder()
                            .setTitle("Report " + db.countReports())
                            .setDescription("``" + args[0] + "``" + " has been reported by ``" + s.getName() + "``")
                            .addField("Player has been reported " + countReports + " time(s)", "", false)
                            .addField("Reported for ", "``" + l.get(reason - 1) + "``", false)
                            .addField("", "Distrust: " + sl.checkSuspicion(uAsString, p), false)
                            .setColor(cg.randomColor())
                            .build();
                    ch.sendMessageEmbeds(e).queue();
                    s.sendMessage(Component.text("Player reported successfully.").color(NamedTextColor.GREEN));
                return true;
                }
            } else if (args!=null && s.getName().equals(args[0])) {
                s.sendMessage(Component.text("You can't report yourself!").color(NamedTextColor.RED));
                return true;
            }
        return false;
    }

    //gets the report categories from the configuration file
    public List<Object> getReasons(FileConfiguration conf) {

        return new ArrayList<>(conf.getList("report-reasons"));

    }
}