package ru.whitebeef.beefresources;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public final class BeefResources extends JavaPlugin implements Listener {


    private String URL = "";
    private String HASH = "";

    private List<String> commandsOnAccept = new ArrayList<>();
    private List<String> commandsOnFail = new ArrayList<>();
    private List<String> commandsOnLoad = new ArrayList<>();
    private List<String> commandsOnDeny = new ArrayList<>();

    private static BeefResources instance;

    public static BeefResources getInstance() {
        return instance;
    }

    private HashSet<String> sentResources = new HashSet<>();

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        getCommand("beefresources").setExecutor(new ResourcePackCommandExecutor());
        reload();
    }

    @Override
    public void onDisable() {

    }

    @EventHandler
    public void onPlayerConnect(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!sentResources.contains(player.getName())) {
            sentResources.add(player.getName());
            player.setResourcePack(URL, HASH);
        }
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        sentResources.remove(event.getPlayer().getName());
    }

    @EventHandler
    public void onResourcePackStatus(PlayerResourcePackStatusEvent event) {
        Player player = event.getPlayer();
        if (!sentResources.contains(player.getName()))
            return;
        Status status = event.getStatus();
        switch (status) {
            case ACCEPTED: {
                commandsOnAccept.forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                        command.replace("%player%", player.getName())));
                break;
            }
            case DECLINED: {
                commandsOnDeny.forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                        command.replace("%player%", player.getName())));
                sentResources.remove(player.getName());
                break;
            }
            case FAILED_DOWNLOAD: {
                commandsOnFail.forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                        command.replace("%player%", player.getName())));
                player.setResourcePack(URL, HASH);
                break;
            }
            case SUCCESSFULLY_LOADED: {
                commandsOnLoad.forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                        command.replace("%player%", player.getName())));
                sentResources.remove(event.getPlayer().getName());
                break;
            }
        }
    }

    public void reload() {
        instance = this;
        File config = new File(getDataFolder() + File.separator + "config.yml");
        FileConfiguration cfg = getConfig();
        if (!config.exists()) {
            getLogger().warning("Config is now exists. Creating new...");
            getConfig().options().copyDefaults(true);
            saveDefaultConfig();
            try {
                cfg.save(config);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        reloadConfig();
        cfg = getConfig();
        this.URL = cfg.getString("url");
        this.HASH = cfg.getString("hash");
        commandsOnAccept = cfg.getStringList("commands.accepted");
        commandsOnLoad = cfg.getStringList("commands.successfullyLoad");
        commandsOnDeny = cfg.getStringList("commands.declined");
        commandsOnFail = cfg.getStringList("commands.failedDownload");
    }

}
