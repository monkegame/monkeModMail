package online.monkegame.monkemodmail;

import online.monkegame.monkemodmail.utils.ColorGenerator;
import online.monkegame.monkemodmail.utils.Database;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Objects;

public class DiscordHandler extends ListenerAdapter {

    public FileConfiguration c;
    public Database db;
    public Main p;
    public ColorGenerator cg;
    public DiscordHandler(FileConfiguration config, Main plugin) {
        this.p = plugin;
        this.c = config;
        this.db = new Database(p);
        this.cg = new ColorGenerator();
    }

    Guild g;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        JDA j = event.getJDA();
        MessageChannel ch = event.getChannel();
        User a = event.getAuthor();
        String m = event.getMessage().getContentRaw();
        String p = c.getString("discord.bot-prefix");
        Member am = event.getMember();
        g = event.getGuild();
        Role ro = g.getRoles().stream().filter(r -> r.getName().equalsIgnoreCase(c.getString("access-role"))).findFirst().orElse(null);
        if (m.startsWith(p) && !a.isBot() && Objects.requireNonNull(am).getRoles().contains(ro)) {
            String[] co = m.substring(p.length()).split(" ");
            switch (co[0]) {

                case "log":
                    if (co.length>1) {
                        switch (co[1]) {
                            case "channel":
                                try {
                                    String channel = co[2];
                                    if (!channel.contains("<")) {
                                        c.set("logging-channel", channel);
                                    } else {
                                        channel.replaceAll("<#>", "");
                                        c.set("logging-channel", channel);
                                    }
                                } catch (IndexOutOfBoundsException e) {
                                    ch.sendMessage("Please specify a channel ID!").submit();
                                    break;
                                }
                                ch.sendMessage("Channel set successfully! Please restart the server to apply the changes!").submit();
                                break;
                            case "settings":
                                MessageEmbed lSE = new EmbedBuilder()
                                        .setTitle("Logging Settings")
                                        .addField("Logging channel", "<#" + c.getString("logging-channel").replace("\"", "") + ">", false)
                                        .addField("Settings role", c.getString("access-role"), false)
                                        .setColor(0x08adf4)
                                        .build();
                                ch.sendMessageEmbeds(lSE).submit();
                                break;
                            case "role":
                                try {
                                    if (c.contains("@")) {
                                        ch.sendMessage("Please specify the role's name instead of pinging it!").submit();
                                        break;
                                    }
                                    c.set("role", co[2]);
                                } catch (IndexOutOfBoundsException e) {
                                    ch.sendMessage("Please specify a role's name!").submit();
                                    break;
                                }
                                ch.sendMessage("Role set successfully! Please restart the server to apply the changes!").submit();
                                break;
                            default:
                                ch.sendMessage("Unknown command!").submit();
                                break;
                        }
                    } else {
                        ch.sendMessage(p + "log role/channel/settings").submit();
                    }
                    break;
                case "help":
                    MessageEmbed ca = new EmbedBuilder()
                            .setTitle("Help! What commands are there?")
                            .addField("log", "-``settings`` -> shows settings\n-``channel <channel>`` -> sets the channel where things will be logged\n-``role <nameOfRole>`` -> sets the role that can edit the settings",false)
                            .setColor(cg.randomColor())
                            .build();
                    ch.sendMessageEmbeds(ca).submit();
                default:
                    ch.sendMessage("Unknown command!").submit();
                    break;
            }
        } else if (m.startsWith(p) && !a.isBot()) {
            ch.sendMessage("You don't have permission to run the command!").submit();
        }
    }

}
