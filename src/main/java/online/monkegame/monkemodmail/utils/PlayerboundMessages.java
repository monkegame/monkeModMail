package online.monkegame.monkemodmail.utils;


import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import online.monkegame.monkemodmail.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.List;

public class PlayerboundMessages {

    private final ColorGenerator CG;
    public Main m;

    public PlayerboundMessages(Main plugin) {
        this.m = plugin;
        this.CG = new ColorGenerator();
    }

    public void sendReasons(CommandSender s, List<String> l) {

        BukkitScheduler b = Bukkit.getScheduler();
        b.runTaskAsynchronously(m, ()-> {
            TextComponent c = Component.text()
                    .content("Valid reasons:")
                    .color(NamedTextColor.RED)
                    .decoration(TextDecoration.ITALIC, false).build();
            s.sendMessage(c);
            for (String a : l) {
                s.sendMessage(Component.text((l.indexOf(a) + 1) + ": " + a).decoration(TextDecoration.ITALIC, false).color(NamedTextColor.YELLOW));
            }
        });
    }


}
