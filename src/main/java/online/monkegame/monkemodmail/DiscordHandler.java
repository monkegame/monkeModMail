package online.monkegame.monkemodmail;

import net.dv8tion.jda.api.Permission;
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
        if (m.startsWith(p) && !a.isBot() && Objects.requireNonNull(am).hasPermission(Permission.MESSAGE_MANAGE)) {
            String[] co = m.substring(p.length()).split(" ");
            switch (co[0]) {

                case "log":
                    if (co.length>1) {
                        if ("channel".equals(co[1])) {
                            try {
                                String channel = co[2];
                                if (!channel.contains("<")) {
                                    c.set("discord.logging-channel", channel);
                                } else {
                                    channel.replaceAll("<#>", "");
                                    c.set("discord.logging-channel", channel);
                                }
                            } catch (IndexOutOfBoundsException e) {
                                ch.sendMessage("Please specify a channel ID!").queue();
                                break;
                            }
                            ch.sendMessage("Channel set successfully! Please restart the server to apply the changes!").queue();
                        } else {
                            ch.sendMessage("Unknown command!").queue();
                        }
                    }
                    break;
                case "modmail":
                    if (co.length>1) {
                        if ("channel".equals(co[1])) {
                            try {
                                String channel = co[2];
                                if (!channel.contains("<")) {
                                    c.set("discord.modmail-channel", channel);
                                } else {
                                    channel.replaceAll("<#>", "");
                                    c.set("discord.modmail-channel", channel);
                                }
                            } catch (IndexOutOfBoundsException e) {
                                ch.sendMessage("Please specify a channel ID!").queue();
                                break;
                            }
                            ch.sendMessage("Channel set successfully! Please restart the server to apply the changes!").queue();
                        } else {
                            ch.sendMessage("Unknown command!").queue();
                        }
                    }
                    break;
                case "settings":
                    MessageEmbed settings = new EmbedBuilder()
                        .setTitle("Current settings")
                        .addField("Logging channel","<#" + c.getString("discord.logging-channel").replace("\"", "") + ">", false)
                        .addField("Modmail channel","<#" + c.getString("discord.modmail-channel").replace("\"", "") + ">", false)
                        .setColor(0x18fa91)
                        .build();
                    ch.sendMessageEmbeds(settings).queue();
                    break;
                case "help":
                    MessageEmbed hlep = new EmbedBuilder()
                            .setTitle("Help! What commands are there?")
                            .addField("log", "-``channel <channel>`` -> sets the channel where reports will be logged",false)
                            .addField("modmail", "-``channel <channel>`` -> sets the channel where modmails will be sent", false)
                            .addField("setttings", "shows the current settings", false)
                            .setColor(cg.randomColor())
                            .build();
                    ch.sendMessageEmbeds(hlep).queue();
                    break;
                default:
                    ch.sendMessage("Unknown command!").queue();
                    break;
            }
        } else if (m.startsWith(p) && !a.isBot()) {
            ch.sendMessage("You don't have permission to run the command!").queue();
        }
    }

}
