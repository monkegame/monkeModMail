package online.monkegame.monkemodmail.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import online.monkegame.monkemodmail.utils.ColorGenerator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ModmailCommand implements CommandExecutor {

    ColorGenerator g;
    JDA j;
    FileConfiguration conf;
    Logger l;

    public ModmailCommand(FileConfiguration c, JDA jda, Logger logger) {
        this.conf = c;
        this.j = jda;
        this.l = logger;
        this.g = new ColorGenerator();
    }

    Map<String, Short> cooldowns = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender s, Command c, String commandName, String[] args) {
        int cooldownTime = conf.getInt("command-cooldowns.modmail-cooldown");
        if (!(s instanceof Player)) {
            return false;
        } else {
            List<String> l = new ArrayList<>(Arrays.asList(args));
            MessageChannel ch = j.getTextChannelById(conf.getString("discord.logging-channel"));
            short secondsLeft;
            if (cooldowns.containsKey(s.getName())) {
                 secondsLeft = (short) (((cooldowns.get(s.getName()) / 1000) + cooldownTime) - (Instant.now().toEpochMilli() / 1000));
                if (secondsLeft>0) {
                    s.sendMessage("Please wait "+secondsLeft+" more seconds!");
                    return true;
                } else if (secondsLeft <= 0) {
                    cooldowns.remove(s.getName());
                }
            }
            MessageEmbed hlepPls = new EmbedBuilder()
                    .setTitle("``" + s.getName() + "`` has requested your help!")
                    .addField("This is what's wrong: ", "``"+l.stream().map(Object::toString).collect(Collectors.joining(" "))+"``" , false)
                    .setColor(g.randomColor())
                    .build();
            cooldowns.put(s.getName(), (short) (Instant.now().toEpochMilli()/1000));
            ch.sendMessageEmbeds(hlepPls).queue();
            return true;
        }
    }
}
