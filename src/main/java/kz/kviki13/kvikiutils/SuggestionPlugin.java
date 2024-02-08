package kz.kviki13.kvikiutils;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class SuggestionPlugin extends JavaPlugin implements Listener {
    private JDA jda;
    private File configFile;
    FileConfiguration config = getConfig();
    private final String DISCORD_TOKEN = config.getString("bottoken");
    private final String DISCORD_CHANNEL_ID = config.getString("channelid");

    @Override
    public void onEnable() {
        loadConfig();
        jda = JDABuilder.createDefault(DISCORD_TOKEN).build();
    }

    public void onDisable() {
        saveConfig();
    }

    private void loadConfig() {
        configFile = new File(getDataFolder(), "config.yml");
        config = YamlConfiguration.loadConfiguration(configFile);

        // Проверка существования файла конфигурации, если его нет, создаем с дефолтными значениями
        if (!configFile.exists()) {
            config.set("bottoken", "your_token");
            config.set("channelid", "your_id");
            config.set("message", "§dСпасибо за ваше предложение!");
            config.set("servername", "YourServer");

            try {
                config.save(configFile);
            } catch (IOException e) {
                //noinspection CallToPrintStackTrace
                e.printStackTrace();
            }
        }
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCommand (@NotNull CommandSender sender, Command cmd, @NotNull String label, @NotNull String[]
            args){
        if (cmd.getName().equalsIgnoreCase("suggestion")) {
            if (args.length == 0) {
                sender.sendMessage(Objects.requireNonNull(config.getString("message")));
                return true;
            }

            StringBuilder suggestionText = new StringBuilder();
            for (String arg : args) {
                suggestionText.append(arg).append(" ");
            }

            sendToDiscord(sender.getName(), suggestionText.toString());
            sender.sendMessage(Objects.requireNonNull(config.getString("message")));
            return true;
        }

        return false;
    }

    private void sendToDiscord(String playerName, String suggestion) {
        String serverName = config.getString("servername");
        assert DISCORD_CHANNEL_ID != null;
        TextChannel channel = jda.getTextChannelById(DISCORD_CHANNEL_ID);

        if (channel != null) {
            channel.sendMessage("**Сервер: " + serverName + "\n**Новое предложение от " + playerName + "\n" + suggestion).queue();
        }
    }
}