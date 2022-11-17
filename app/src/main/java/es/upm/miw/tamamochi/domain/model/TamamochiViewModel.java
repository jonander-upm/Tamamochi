package es.upm.miw.tamamochi.domain.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;
import java.util.List;

import es.upm.miw.tamamochi.domain.model.pojos.measurements.Measurement;
import es.upm.miw.tamamochi.domain.model.pojos.weather.ExternalWeather;
import es.upm.miw.tamamochi.domain.repositories.CharacterRepository;

public class TamamochiViewModel extends ViewModel implements Serializable {
    FirebaseUser currentUser;
    MutableLiveData<Character> character;
    MutableLiveData<List<CharacterStatus>> characterStatusList;
    MutableLiveData<Measurement> internalEnvironment;
    MutableLiveData<ExternalWeather> externalEvironment;

    CharacterRepository characterRepository;

    private static final TamamochiViewModel INSTANCE = new TamamochiViewModel();

    private TamamochiViewModel() {
        character = new MutableLiveData<>();
        characterStatusList = new MutableLiveData<>();
        internalEnvironment = new MutableLiveData<>();
        externalEvironment = new MutableLiveData<>();
        characterRepository = CharacterRepository.getInstance();
    }

    public static TamamochiViewModel getInstance() {
        return INSTANCE;
    }

    public MutableLiveData<Character> getCharacter() {
        return character;
    }

    public void setCharacter(Character character) {
        this.character.setValue(character);
        characterRepository.saveCharacter(character, currentUser.getUid());
    }

    public MutableLiveData<List<CharacterStatus>> getCharacterStatusList() {
        return characterStatusList;
    }

    public void setCharacterStatus(List<CharacterStatus> characterStatusList) {
        this.characterStatusList.setValue(characterStatusList);
    }

    public MutableLiveData<Measurement> getInternalEnvironment() {
        return internalEnvironment;
    }

    public void setInternalEnvironment(Measurement internalEnvironment) {
        this.internalEnvironment.setValue(internalEnvironment);
        this.characterStatusList.setValue(CharacterStatus.getCharacterStatusList(internalEnvironment));
    }

    public MutableLiveData<ExternalWeather> getExternalEvironment() {
        return externalEvironment;
    }

    public void setExternalEvironment(ExternalWeather externalEvironment) {
        this.externalEvironment.setValue(externalEvironment);
    }

    public FirebaseUser getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(FirebaseUser currentUser) {
        this.currentUser = currentUser;
        character = characterRepository.findLatestCharacterByUid(currentUser.getUid());
    }
}
