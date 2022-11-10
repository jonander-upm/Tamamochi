package es.upm.miw.tamamochi.domain.model;

import java.util.List;

public class TamamochiGame {
    Character character;
    Environment environment;

    public TamamochiGame(Character character, Environment environment) {
        this.character = character;
        this.environment = environment;
    }

    public Character getCharacter() {
        return character;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public List<CharacterStatus> getCharacterStatusList() {
        return CharacterStatus.getCharacterStatusList(environment);
    }
}
