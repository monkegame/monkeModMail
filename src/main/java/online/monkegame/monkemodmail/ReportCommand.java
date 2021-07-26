package online.monkegame.monkemodmail;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class ReportCommand implements CommandExecutor {

    public FileConfiguration co;
    JDA j;
    public Database db;
    public ReportCommand(FileConfiguration config, JDA jda) {
        this.co = config;
        this.j = jda;
        this.db = new Database();
    }

    /*
    TODO report counter
    get the amount of entries (so, sum suspicion ?)
    TODO sus counter
    db stuff
     */

    String reason;

    @Override
    public boolean onCommand(CommandSender s, Command c, String a, String[] args) {
        if (s instanceof Player && args!=null) {
            MessageChannel ch = j.getTextChannelById(co.getString("logging-channel"));
            if (args.length < 2) {
                s.sendMessage("Please specify a reason!");
                return false;
            } else {
                reason = getReason(args).replace(args[0], "");
            }
            MessageEmbed e = new EmbedBuilder()
                    .setTitle("Report x")
                    .setDescription("``" + args[0] + "``" + " has been reported by ``" + s.getName() + "``")
                    .addField("Reason", reason, false)
                    .addField("", "Suspicion: ", false)
                    .build();

            ch.sendMessageEmbeds(e).submit();
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
