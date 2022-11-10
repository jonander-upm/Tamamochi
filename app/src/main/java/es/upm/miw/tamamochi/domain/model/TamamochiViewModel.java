package es.upm.miw.tamamochi.domain.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.Serializable;
import java.util.List;

import es.upm.miw.tamamochi.domain.model.CharacterStatus;
import es.upm.miw.tamamochi.domain.model.Environment;

public class TamamochiViewModel extends ViewModel implements Serializable {
    MutableLiveData<Integer> characterLife;
    MutableLiveData<List<CharacterStatus>> characterStatusList;
    MutableLiveData<Environment> environment;

    private static final TamamochiViewModel INSTANCE = new TamamochiViewModel();

    private TamamochiViewModel() {
        characterLife = new MutableLiveData<>();
        characterStatusList = new MutableLiveData<>();
        environment = new MutableLiveData<>();
    }

    public static TamamochiViewModel getInstance() {
        return INSTANCE;
    }

    public MutableLiveData<Integer> getCharacterLife() {
        return characterLife;
    }

    public void setCharacterLife(Integer characterLife) {
        this.characterLife.setValue(characterLife);
    }

    public MutableLiveData<List<CharacterStatus>> getCharacterStatusList() {
        return characterStatusList;
    }

    public void setCharacterStatus(List<CharacterStatus> characterStatusList) {
        this.characterStatusList.setValue(characterStatusList);
    }

    public MutableLiveData<Environment> getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment.setValue(environment);
        this.characterStatusList.setValue(CharacterStatus.getCharacterStatusList(environment));
    }
}
