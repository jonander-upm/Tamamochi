package es.upm.miw.tamamochi.domain.model;

import java.util.Date;

public class Character {
    private String characterId;
    private CharacterType characterType;
    private String characterName;
    private Date characterBirthDate;
    private boolean alive;

    public Character(CharacterType characterType, String characterName, Date characterBirthDate, boolean alive) {
        this.characterType = characterType;
        this.characterName = characterName;
        this.characterBirthDate = characterBirthDate;
        this.alive = alive;
    }

    public String getCharacterId() {
        return characterId;
    }

    public void setCharacterId(String characterId) {
        this.characterId = characterId;
    }

    public CharacterType getCharacterType() {
        return characterType;
    }

    public void setCharacterType(CharacterType characterType) {
        this.characterType = characterType;
    }

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }

    public Date getCharacterBirthDate() {
        return characterBirthDate;
    }

    public void setCharacterBirthDate(Date characterBirthDate) {
        this.characterBirthDate = characterBirthDate;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }
}
