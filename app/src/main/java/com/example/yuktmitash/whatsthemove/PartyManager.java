package com.example.yuktmitash.whatsthemove;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

public class PartyManager {
    private Party party;
    private DatabaseReference reference;

    PartyManager(Party party, DatabaseReference reference) {
        this.party = party;
        this.reference = reference;
    }

    public void pushToFirebase() {
        reference.child("parties").child(party.getFireid()).setValue(party);
    }
}
