package online.monkegame.monkemodmail;

import online.monkegame.monkemodmail.commands.ModmailCommand;
import online.monkegame.monkemodmail.commands.ReportCommand;
import online.monkegame.monkemodmail.utils.Database;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public class Main extends JavaPlugin {

    public FileConfiguration configuration;
    public JDA j;
    public Database db;
    public Main main = this;
    public Main() {
        this.db = new Database(this);
    }

    @Override
    public void onEnable() {

        saveDefaultConfig();
        configuration = getConfig();
        getLogger().info("Config loaded!");
        getLogger().info("                _       __  __         _ __  __      _ _");
        getLogger().info(" _ __  ___ _ _ | |_____|  \\/  |___  __| |  \\/  |__ _(_) |");
        getLogger().info("| '  \\/ _ \\ ' \\| / / -_) |\\/| / _ \\/ _` | |\\/| / _` | | |");
        getLogger().info("|_|_|_\\___/_||_|_\\_\\___|_|  |_\\___/\\__,_|_|  |_\\__,_|_|_|");
        getLogger().info("---------------------------------------------------------");
        try {
            j = JDABuilder.createLight(getConfig().getString("discord.bot-token"), GatewayIntent.GUILD_MESSAGES)
                    .addEventListeners(new DiscordHandler(configuration, main))
                    .setMemberCachePolicy(MemberCachePolicy.ONLINE)
                    .build()
                    .awaitReady();

            getLogger().info("Connected to Discord!");

        } catch (InterruptedException | LoginException e) {

            getLogger().severe(e.getMessage());
            getLogger().severe("Failed to start! Please configure the plugin!");

        }
        try {
            db.createDB(this.getLogger());
        } catch (IOException e) {
            e.printStackTrace();
        }
        getCommand("modmail").setExecutor(new ModmailCommand(configuration, j, this.getLogger()));
        getCommand("report").setExecutor(new ReportCommand(configuration, j, main));
    }
    @Override
    public void onDisable() {
        if (j!=null) {
            j.shutdown();
        }
        saveConfig();
    }

}
