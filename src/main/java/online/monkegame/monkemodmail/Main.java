package online.monkegame.monkemodmail;

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
    public Main() {
        this.db = new Database();
    }

    @Override
    public void onEnable() {

        saveDefaultConfig();
        configuration = getConfig();
        getLogger().info("                _       __  __         _ __  __      _ _");
        getLogger().info(" _ __  ___ _ _ | |_____|  \\/  |___  __| |  \\/  |__ _(_) |");
        getLogger().info("| '  \\/ _ \\ ' \\| / / -_) |\\/| / _ \\/ _` | |\\/| / _` | | |");
        getLogger().info("|_|_|_\\___/_||_|_\\_\\___|_|  |_\\___/\\__,_|_|  |_\\__,_|_|_|");
        getLogger().info("---------------------------------------------------------");

        try {

            j = JDABuilder.createLight(getConfig().getString("discord-bot-token"), GatewayIntent.GUILD_MESSAGES)
                    .addEventListeners(new DiscordHandler(configuration))
                    .setMemberCachePolicy(MemberCachePolicy.ONLINE)
                    .build()
                    .awaitReady();

            getLogger().info("Connected to Discord!");

        } catch (InterruptedException | LoginException e) {

            getLogger().severe("Failed to start!");
            e.printStackTrace();

        }
        try {
            getLogger().info(db.createDB());
        } catch (IOException e) {
            e.printStackTrace();
        }
        getCommand("report").setExecutor(new ReportCommand(configuration, j));
    }

    @Override
    public void onDisable() {
        j.shutdown();
        saveConfig();
    }

}
