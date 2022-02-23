package com.joojn.party.command;

import com.joojn.party.system.PartyPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.joojn.party.event.EventListener.partyPlayerHashMap;

public class CustomCommands implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage("This command is for players only!");
            return true;
        }
        Player p = (Player) sender;
        PartyPlayer pp = partyPlayerHashMap.get(p);
        if(cmd.getName().equalsIgnoreCase("pgetdown")){
            if(pp.isInParty() && pp.getParty().getOwner() == p){
                pp.getParty().getPlayers().forEach(pl -> {
                    pl.performCommand("getdown");
                });
            }
            p.performCommand("getdown");
        }

        return true;
    }
}
