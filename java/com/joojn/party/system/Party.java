package com.joojn.party.system;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

import static com.joojn.party.event.EventListener.partyPlayerHashMap;
import static com.joojn.party.system.Messages.*;

public class Party {

    Player owner;
    List<Player> partyList;

    public Party(Player owner){
        this.owner = owner;
        this.partyList = new ArrayList<>();
    }

    public Party(Player owner, Player... players){
        this.owner = owner;
        this.partyList = Arrays.asList(players);
    }

    public void addPLayer(Player p){
        partyPlayerHashMap.get(p).setParty(this);
        if(!(this.partyList.contains(p))) partyList.add(p);

        owner.sendMessage(PARTY_JOIN.replace("%player%", p.getName()));
        partyList.forEach(pl -> {
            pl.sendMessage(PARTY_JOIN.replace("%player%", p.getName()));
        });
    }
    public boolean removePlayer(Player p){
        if(p == this.getOwner()){
            if(partyList.size() == 0){
                disbandParty();
                return false;
            }
            int r = new Random().nextInt(partyList.size());
            setOwner(partyList.get(r));
        }
        partyPlayerHashMap.get(p).setParty(null);
        if(this.partyList.contains(p)) partyList.remove(p);
        return true;
    }

    public void leavePlayer(Player p){
        if(removePlayer(p)) p.sendMessage(YOU_PARTY_LEAVE);;

        owner.sendMessage(PARTY_LEAVE.replace("%player%", p.getName()));
        partyList.forEach(pl -> {
            pl.sendMessage(PARTY_LEAVE.replace("%player%", p.getName()));
        });
    }

    public Player getOwner(){return owner;}

    public void setOwner(Player newOwner){
        this.partyList.add(this.owner);
        this.partyList.remove(newOwner);
        this.owner = newOwner;
        owner.sendMessage(YOU_WERE_PROMOTED);
        this.partyList.forEach(pl -> {
            pl.sendMessage(PLAYER_WAS_PROMOTED.replace("%player%", owner.getName()));
        });
    }

    public List<Player> getPlayers(){
        return partyList;
    }
    public void disbandParty(){
        PartyPlayer owner = partyPlayerHashMap.get(this.owner);
        owner.setParty(null);
        this.owner.sendMessage(PARTY_DISBAND);
        getPlayers().forEach(pl -> {
            PartyPlayer partyPlayer = partyPlayerHashMap.get(pl);
            partyPlayer.setParty(null);
            pl.sendMessage(PARTY_DISBAND);
        });
    }

    public void warp(){
        if(owner.getWorld().getName().equalsIgnoreCase("lobby")){
            getPlayers().forEach(pl -> {
                pl.performCommand("lobby");
            });
        }
        else if(owner.getWorld().getName().equalsIgnoreCase("getdown")){
            getPlayers().forEach(pl -> {
                pl.performCommand("getdown");
            });
        }
        owner.sendMessage(PARTY_WARPED);
        partyList.forEach(pl -> {
            pl.sendMessage(PARTY_WARPED);
        });
    }

    public void kick(Player p){
        removePlayer(p);

        p.sendMessage(YOU_WERE_KICKED);
        owner.sendMessage(PLAYER_WAS_KICKED.replace("%player%", p.getName()));
        partyList.forEach(pl -> {
             pl.sendMessage(PLAYER_WAS_KICKED.replace("%player%", p.getName()));
        });
    }

    public Player getPlayerByName(String playerName){
        Player p = null;
        for(Player pl : partyList){
            if(pl.getName().equalsIgnoreCase(playerName)){
                p = pl;
                break;
            }
        }
        return p;
    }
}
