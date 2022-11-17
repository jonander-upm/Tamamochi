package es.upm.miw.tamamochi.activities;

import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import es.upm.miw.tamamochi.R;
import es.upm.miw.tamamochi.domain.model.Character;
import es.upm.miw.tamamochi.domain.model.TamamochiViewModel;
import es.upm.miw.tamamochi.domain.repositories.CharacterRepository;
import es.upm.miw.tamamochi.view.CharacterListViewAdapter;

public class CharacterListActivity extends AppCompatActivity {
    CharacterRepository characterRepository;
    TamamochiViewModel tamamochiViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_list);
        tamamochiViewModel = TamamochiViewModel.getInstance();
        characterRepository = CharacterRepository.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = tamamochiViewModel.getCurrentUser();
        characterRepository.findByUid(currentUser.getUid()).observe(this, new Observer<List<Character>>() {
            @Override
            public void onChanged(List<Character> characters) {
                ListView lvCharacters = findViewById(R.id.lvCharacters);
                lvCharacters.setAdapter(new CharacterListViewAdapter(getApplicationContext(), characters));
            }
        });
    }
}
