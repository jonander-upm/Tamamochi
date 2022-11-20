package es.upm.miw.tamamochi.domain.model;

import android.util.Log;

import java.util.Date;

public class Character {
    private String characterId;
    private CharacterType characterType;
    private String characterName;
    private Date characterBirthDate;
    private Boolean alive;
    private Integer life;

    public Character() {
    }

    public Character(CharacterType characterType, String characterName, Date characterBirthDate) {
        this.characterType = characterType;
        this.characterName = characterName;
        this.characterBirthDate = characterBirthDate;
        this.alive = true;
        this.life = 100;
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

    public Integer getLife() {
        return life;
    }

    public void setLife(Integer life) {
        this.life = life;
    }

    public Boolean getAlive() {
        return alive;
    }

    public void setAlive(Boolean alive) {
        this.alive = alive;
    }

    public Integer getCharacterAgeDays() {
        long timestampNow = new Date().getTime();
        long timestampBirth = this.characterBirthDate.getTime();
        return Math.toIntExact((timestampNow - timestampBirth) / (1000 * 60 * 60 * 24));
    }

    @Override
    public String toString() {
        return "Character{" +
                "characterId='" + characterId + '\'' +
                ", characterType=" + characterType +
                ", characterName='" + characterName + '\'' +
                ", characterBirthDate=" + characterBirthDate +
                ", alive=" + alive +
                ", life=" + life +
                '}';
    }
}
