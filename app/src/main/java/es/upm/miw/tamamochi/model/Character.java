package es.upm.miw.tamamochi.model;

import java.util.Date;

public class Character {
    private CharacterType characterType;
    private String characterName;
    private Date characterBirthDate;

    public Character(CharacterType characterType, String characterName, Date characterBirthDate) {
        this.characterType = characterType;
        this.characterName = characterName;
        this.characterBirthDate = characterBirthDate;
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
}
