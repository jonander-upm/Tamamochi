package es.upm.miw.tamamochi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.lifecycle.ViewModelProvider;

import java.util.Date;

import es.upm.miw.tamamochi.R;
import es.upm.miw.tamamochi.domain.model.CharacterType;
import es.upm.miw.tamamochi.domain.model.Character;
import es.upm.miw.tamamochi.domain.model.TamamochiViewModel;
import es.upm.miw.tamamochi.domain.repositories.CharacterRepository;

public class CharacterCreationActivity extends AppCompatActivity {
    private TamamochiViewModel tamamochiViewModel;
    private int characterTypeIndex;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_creation);
        tamamochiViewModel = TamamochiViewModel.getInstance();
        characterTypeIndex = 0;
        setButtonListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setCharacterInSelector(characterTypeIndex);
    }

    private void setCharacterInSelector(int currentCharacterTypeIndex) {
        ImageView ivCharacter  = findViewById(R.id.ivCharacter);
        TextView tvCharacter = findViewById(R.id.tvCharacter);
        CharacterType characterType = CharacterType.values()[currentCharacterTypeIndex];
        ivCharacter.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(), characterType.getAdultDrawableId()));
        tvCharacter.setText(characterType.toString());
    }

    private void setButtonListeners() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.selectPrevButton:
                        manageSelectionIndex(--characterTypeIndex);
                        break;
                    case R.id.selectNextButton:
                        manageSelectionIndex(++characterTypeIndex);
                        break;
                    case R.id.createButton:
                        createCharacter();
                        startActivity(new Intent(CharacterCreationActivity.this, GameMainActivity.class));
                        break;
                    default:
                        break;
                }
            }
        };
        findViewById(R.id.selectNextButton).setOnClickListener(listener);
        findViewById(R.id.selectPrevButton).setOnClickListener(listener);
        findViewById(R.id.createButton).setOnClickListener(listener);
    }

    private void createCharacter() {
        EditText etCharacter = findViewById(R.id.etCharacter);
        Character character = new Character(CharacterType.values()[characterTypeIndex],
                etCharacter.getText().toString(),
                new Date());
        Character savedCharacter = CharacterRepository.getInstance().saveCharacter(character, tamamochiViewModel.getCurrentUser().getUid());
        tamamochiViewModel.setCharacter(savedCharacter);
    }

    private void manageSelectionIndex(int i) {
        characterTypeIndex = Math.floorMod(i, CharacterType.values().length);
        setCharacterInSelector(characterTypeIndex);
    }
}
