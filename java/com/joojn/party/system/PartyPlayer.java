package com.joojn.party.system;

import java.util.*;

public class PartyPlayer {

    public Party party;
    Set<Party> pendingList = new HashSet<>();

    public PartyPlayer(Party party){
        this.party = party;
    }
    public boolean isInParty(){
        return this.party != null;
    }

    public void addPending(Party party){
        pendingList.add(party);
    }
    public void removePending(Party party){
        pendingList.remove(party);
    }
    public boolean isPending(Party party){
        return pendingList.contains(party);
    }

    public void setParty(Party party){
        this.party = party;
    }

    public Party getParty() {
        return party;
    }
}
