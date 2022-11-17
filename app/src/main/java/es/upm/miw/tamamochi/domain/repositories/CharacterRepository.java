package es.upm.miw.tamamochi.domain.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import es.upm.miw.tamamochi.domain.model.Character;
import es.upm.miw.tamamochi.domain.model.CharacterAge;

public class CharacterRepository {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mCharacterDatabaseReference;

    private static final CharacterRepository INSTANCE = new CharacterRepository();

    private CharacterRepository() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mCharacterDatabaseReference = mFirebaseDatabase.getReference().child("characters");
    }

    public static CharacterRepository getInstance() {
        return INSTANCE;
    }

    public Character saveCharacter(Character character, String uid) {
        String characterId = character.getCharacterId();
        if(characterId == null) {
            characterId = mCharacterDatabaseReference
                    .child(uid)
                    .push()
                    .getKey();
        }
        assert characterId != null;
        character.setCharacterId(characterId);
        mCharacterDatabaseReference
                .child(uid)
                .child(characterId)
                .setValue(character);
        return character;
    }

    public MutableLiveData<List<Character>> findByUid(String uid) {
        MutableLiveData<List<Character>> characterList = new MutableLiveData<>();
        mCharacterDatabaseReference.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Character> dbCharacters = new ArrayList<>();
                for(DataSnapshot dataSnapshotItem : dataSnapshot.getChildren()) {
                    dbCharacters.add(dataSnapshotItem.getValue(Character.class));
                }
                characterList.setValue(dbCharacters);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return characterList;
    }

    public MutableLiveData<Character> findByUidAndCharacterId(String uid, String characterId) {
        MutableLiveData<Character> character = new MutableLiveData<>();
        mCharacterDatabaseReference
                .child(uid)
                .equalTo(characterId, "characterId")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Character dbCharacter = dataSnapshot.getValue(Character.class);
                        character.setValue(dbCharacter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        return character;
    }

    public MutableLiveData<Character> findByUidAndAlive(String uid, boolean alive) {
        MutableLiveData<Character> character = new MutableLiveData<>();
        mCharacterDatabaseReference
                .child(uid)
                .orderByChild("alive")
                .equalTo(alive)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Character dbCharacter = null;
                for(DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    dbCharacter = childSnapshot.getValue(Character.class);
                }
                character.setValue(dbCharacter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return character;
    }

    public MutableLiveData<Character> findLatestCharacterByUid(String uid) {
        MutableLiveData<Character> character = new MutableLiveData<>();
        mCharacterDatabaseReference
                .child(uid)
                .orderByChild("characterBirthDate")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Character latestCharacter = null;
                        for(DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            Character dbCharacter = childSnapshot.getValue(Character.class);
                            assert dbCharacter != null;
                            if(latestCharacter == null || dbCharacter.getCharacterBirthDate().after(latestCharacter.getCharacterBirthDate())) {
                                latestCharacter = dbCharacter;
                            }
                        }
                        character.setValue(latestCharacter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        return character;
    }
}
