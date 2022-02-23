package com.joojn.party.command;

import com.joojn.party.system.Party;
import com.joojn.party.system.PartyPlayer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.joojn.party.Main.MAIN_INSTANCE;
import static com.joojn.party.event.EventListener.partyPlayerHashMap;
import static com.joojn.party.system.Messages.*;
import static org.bukkit.Bukkit.getServer;


public class PartyCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage(FOR_PLAYERS_ONLY);
            return true;
        }
        Player p = (Player) sender;
        
        if(args.length > 0 && (args[0].equalsIgnoreCase("accept") || args[0].equalsIgnoreCase("join"))){
            if(!(args.length > 1)){
                p.sendMessage(ACCEPT_INVALID);
                return true;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if(target == null){
                p.sendMessage(PLAYER_NOT_FOUND);
                return true;
            }
            PartyPlayer partyPlayer = partyPlayerHashMap.get(p);
            PartyPlayer targetPlayer = partyPlayerHashMap.get(target);

            if(partyPlayer.isInParty()){
                p.sendMessage(YOU_ALREADY_IN_PARTY);
                return true;
            }
            Party targetParty = targetPlayer.getParty();

            if(targetParty != null && partyPlayer.isPending(targetParty)){
                partyPlayer.removePending(targetParty);
                targetParty.addPLayer(p);
            }else{
                p.sendMessage(NOT_INVITED);
                return true;
            }

        }
        else if(args.length > 0 && args[0].equalsIgnoreCase("leave")){
            PartyPlayer partyPlayer = partyPlayerHashMap.get(p);
            if(!(partyPlayer.isInParty())){
                p.sendMessage(NOT_IN_PARTY);
                return true;
            }
            partyPlayer.getParty().leavePlayer(p);
        }
        else if(args.length > 0 && (args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("info"))){
            PartyPlayer partyPlayer = partyPlayerHashMap.get(p);
            if(!(partyPlayer.isInParty())){
                p.sendMessage(NOT_IN_PARTY);
                return true;
            }
            Party party = partyPlayer.getParty();
            p.sendMessage("§8§m-----------------");
            p.sendMessage("§8• §eOwner: ");
            String form = Bukkit.getPlayer(party.getOwner().getName()) == null ? "§7" : "§f";
            p.sendMessage(String.format("§6» %s%s", form, party.getOwner().getName()));
            p.sendMessage(" ");
            p.sendMessage("§8• §eMembers: §r");
            String players = "§6» ";
            for(int i=0;i<party.getPlayers().size();i++){
                String name = party.getPlayers().get(i).getName();
                form = Bukkit.getPlayer(name) == null ? "§7" : "§f";
                if(party.getPlayers().size() == i+1){
                    players += form + name + " §e(§r" + party.getPlayers().size() + "§e)";
                    continue;
                }
                players += form + name + "§r, ";
            }
            p.sendMessage(players);
            p.sendMessage("§8§m-----------------");
        }
        else if(args.length > 0 && args[0].equalsIgnoreCase("warp")){
            PartyPlayer partyPlayer = partyPlayerHashMap.get(p);
            if(!(partyPlayer.isInParty())){
                p.sendMessage(NOT_IN_PARTY);
                return true;
            }
            if(partyPlayer.getParty().getOwner() != p){
                p.sendMessage(NOT_OWNER);
                return true;
            }
            partyPlayer.getParty().warp();
        }
        else if(args.length > 0 && (args[0].equalsIgnoreCase("owner") || args[0].equalsIgnoreCase("leader"))){
            PartyPlayer pp = partyPlayerHashMap.get(p);
            if(!(args.length > 1)){
                p.sendMessage(PROMOTE_INVALID);
                return true;
            }
            if(!(pp.isInParty())){
                p.sendMessage(NOT_IN_PARTY);
                return true;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if(target == null){
                p.sendMessage(PLAYER_NOT_FOUND.replace("%player%", args[0]));
                return true;
            }
            Party party = pp.getParty();
            if(party.getOwner() != p){
                p.sendMessage(NOT_OWNER);
                return true;
            }

            if(!(party.getPlayers().contains(target))){
                p.sendMessage(PLAYER_NOT_FOUND.replace("%player%", target.getName()));
                return true;
            }
            party.setOwner(target);
        }
        else if(args.length > 0 && (args[0].equalsIgnoreCase("kick"))){
            PartyPlayer pp = partyPlayerHashMap.get(p);
            if(!(args.length > 1)){
                p.sendMessage(KICK_INVALID);
                return true;
            }
            if(!(pp.isInParty())){
                p.sendMessage(NOT_IN_PARTY);
                return true;
            }

            Party party = pp.getParty();
            if(party.getOwner() != p){
                p.sendMessage(NOT_OWNER);
                return true;
            }
            if(party.getPlayerByName(args[1]) == null){
                p.sendMessage(PLAYER_NOT_FOUND.replace("%player%", args[1]));
                return true;
            }

            party.kick(party.getPlayerByName(args[1]));
        }
        else if(args.length > 0 && args[0].equalsIgnoreCase("help")){
            showHelp(p);
        }
        else if(args.length > 0 && args[0].equalsIgnoreCase("disband")){
            PartyPlayer pp = partyPlayerHashMap.get(p);
            if(!(pp.isInParty())){
                p.sendMessage(NOT_IN_PARTY);
                return true;
            }

            Party party = pp.getParty();
            if(party.getOwner() != p){
                p.sendMessage(NOT_OWNER);
                return true;
            }
            party.disbandParty();
        }
        else if(args.length > 0 && args[0].equalsIgnoreCase("reload")){
            if(!(p.isOp())){
                return true;
            }
            p.sendMessage(PARTY_PREFIX+"§fReloading Party plugin..");
            for(Player op : Bukkit.getOnlinePlayers()){
                if(partyPlayerHashMap.get(op) == null) partyPlayerHashMap.put(op, new PartyPlayer(null));
            }
            p.sendMessage(PARTY_PREFIX+"§fParty plugin reloaded..");
        }
        else if(args.length > 0){
            Player target;
            String playerStr;
            if(args.length > 1 && args[0].equalsIgnoreCase("invite")){
                target = Bukkit.getPlayer(args[1]);
                playerStr = args[1];
            }else {
                target = Bukkit.getPlayer(args[0]);
                playerStr = args[0];
            }
            if(target == null){
                p.sendMessage(PLAYER_NOT_FOUND.replace("%player%", playerStr));
                return true;
            }
            if(target == p){
                p.sendMessage(CANT_INVITE_YOURSELF);
                return true;
            }
            PartyPlayer partyPlayer = partyPlayerHashMap.get(p);
            PartyPlayer targetPlayer = partyPlayerHashMap.get(target);
            Party playerParty = partyPlayer.getParty();


            if(partyPlayer.isInParty() && targetPlayer.isInParty() && partyPlayer.getParty().getPlayers().contains(target)){
                p.sendMessage(ALREADY_IN_PARTY.replace("%player%", target.getName()));
                return true;
            }
            if(partyPlayer.isInParty() && partyPlayer.getParty().getOwner() != p){
                p.sendMessage(NOT_OWNER);
                return true;
            }
            if(targetPlayer.isPending(playerParty)){
                p.sendMessage(ALREADY_INVITED.replace("%player%", target.getName()));
                return true;
            }

            if(!(partyPlayer.isInParty())){
                playerParty = new Party(p);
                partyPlayer.setParty(playerParty);
                p.sendMessage(PARTY_CREATED);
            }
            sendPartyInvite(target, p);
            p.sendMessage(PARTY_INVITE2.replace("%player%",target.getName()));
            targetPlayer.addPending(playerParty);
            Party finalPlayerParty = playerParty;
            getServer().getScheduler().runTaskLater(MAIN_INSTANCE, new Runnable() {
                public void run() {
                    targetPlayer.removePending(finalPlayerParty);
                }}, (long) 1200);
        }
        else showHelp(p);

        return true;
    }
    public void showHelp(Player p){
        p.sendMessage("§8§m-----------------------------------------------");
        p.sendMessage("§6 Party plugin §8» §r§njoojn");
        p.sendMessage(" ");
        p.sendMessage("§6• /party help §8» §fshow help");
        p.sendMessage("§6• /party accept <player> §8» §faccept party invite");
        p.sendMessage("§6• /party leave §8» §fleave the party");
        p.sendMessage("§6• /party owner <player> §8» §fset new leader of the party");
        p.sendMessage("§6• /party kick <player> §8» §fkick a player from the party");
        p.sendMessage("§6• /party disband §8» §fdisband party");
        if(p.isOp()) p.sendMessage("§6• /party reload §8» §freload party plugin");
        p.sendMessage("§6• @ §8» §fparty chat");
        p.sendMessage("§8§m-----------------------------------------------");
    }
    public void sendPartyInvite(Player target, Player sender){
        TextComponent text = new TextComponent(PARTY_PREFIX+"§7>> §a§lClick here to accept!§r §7<<");
        text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept "+sender.getName()));
        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] {new TextComponent("§7Click to accept")}));

        target.sendMessage(String.format(PARTY_PREFIX+"§e%s §finvited you to the party!",sender.getName()));
        target.spigot().sendMessage(text);
    }
}
