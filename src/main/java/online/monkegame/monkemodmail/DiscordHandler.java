package online.monkegame.monkemodmail;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import online.monkegame.monkemodmail.utils.Database;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Objects;

public class DiscordHandler extends ListenerAdapter {

    public FileConfiguration c;
    public Database db;
    public DiscordHandler(FileConfiguration config) {
        this.c = config;
        this.db = new Database();
    }

    Guild g;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        JDA j = event.getJDA();
        MessageChannel ch = event.getChannel();
        User a = event.getAuthor();
        String m = event.getMessage().getContentRaw();
        String p = c.getString("prefix");
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
                                    c.set("logging-channel", co[2]);
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
                                        .addField("Suspicion threshold", c.getString("suspicion-threshold"),false)
                                        .setColor(0x08adf4)
                                        .build();
                                ch.sendMessageEmbeds(lSE).submit();
                                break;
                            case "role":
                                try {
                                    c.set("role", co[2]);
                                } catch (IndexOutOfBoundsException e) {
                                    ch.sendMessage("Please specify a role's name!").submit();
                                    break;
                                }
                                ch.sendMessage("Role set successfully! Please restart the server to apply the changes!").submit();
                                break;
                            case "threshold":
                                try {
                                    c.set("suspicion-threshold", co[2]);
                                } catch (IndexOutOfBoundsException ioobe) {
                                    ch.sendMessage("Please specify a threshold!").submit();
                                    break;
                                }
                                ch.sendMessage("Threshold set successfully! Please restart the server to apply the changes!").submit();
                                break;
                            default:
                                ch.sendMessage("Unknown command!").submit();
                                break;
                        }
                    } else {
                        ch.sendMessage(p + "log role/channel/threshold/settings").submit();
                    }
                    break;
                case "reduce":
                    //reduces the sus level for a user by x amount (take UUID or username input)
                    break;
                default:
                    ch.sendMessage("Unknown command!").submit();
                    break;
            }
        } else if (m.startsWith(p) && !a.isBot()) {
            ch.sendMessage("You don't have permission to run the command!").submit();
        }
    }

}
