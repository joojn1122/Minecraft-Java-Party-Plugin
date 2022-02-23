package com.joojn.party.event;

import com.joojn.party.system.Party;
import com.joojn.party.system.PartyPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;

import static com.joojn.party.system.Messages.PARTY_MESSAGE;


public class EventListener implements Listener {
    public static HashMap<Player, PartyPlayer> partyPlayerHashMap = new HashMap<>();
    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        if(partyPlayerHashMap.get(event.getPlayer()) == null) partyPlayerHashMap.put(event.getPlayer(), new PartyPlayer(null));
    }

    @EventHandler
    public void onMessage(AsyncPlayerChatEvent event){
        Player p = event.getPlayer();
        if(event.getMessage().startsWith("@") && partyPlayerHashMap.get(p).isInParty()){
            event.setCancelled(true);
            Party party = partyPlayerHashMap.get(p).getParty();
            String message = event.getMessage().substring(1);
            party.getPlayers().forEach(pl -> {
                pl.sendMessage(PARTY_MESSAGE.replace("%player%",p.getName()).replace("%message%",message));
            });
            party.getOwner().sendMessage(PARTY_MESSAGE.replace("%player%",p.getName()).replace("%message%",message));
        }
    }
}
