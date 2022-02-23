package com.joojn.party;

import com.joojn.party.command.CustomCommands;
import com.joojn.party.command.PartyCommand;
import com.joojn.party.event.EventListener;
import org.bukkit.plugin.java.JavaPlugin;


public final class Main extends JavaPlugin {

    public static Main MAIN_INSTANCE;
    @Override
    public void onEnable() {
        // Plugin startup logic
        MAIN_INSTANCE = this;
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        getCommand("pgetdown").setExecutor(new CustomCommands());
        getCommand("party").setExecutor(new PartyCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
