package es.upm.miw.tamamochi.domain.model;

import android.util.Log;

import java.util.Date;

public enum CharacterAge {
    EGG(1, 0),
    BABY(72, 2),
    ADULT(240, 1),
    OLD(360, 3),
    DEAD(432, 0),
    NONE(0, 0);

    final int ageHours;
    final int drainMultiplier;

    private CharacterAge(int ageHours, int drainMultiplier) {
        this.ageHours = ageHours;
        this.drainMultiplier = drainMultiplier;
    }

    public int getAgeHours() {
        return ageHours;
    }

    public static CharacterAge getCharacterAge(Date birthDate) {
        Date now = new Date();
        double ageHours = (double) (now.getTime() - birthDate.getTime()) / 3600000;
        for(CharacterAge characterAge : values()) {
            if(ageHours <= characterAge.getAgeHours()) {
                return characterAge;
            }
        }
        return CharacterAge.NONE;
    }

    public int getDrainMultiplier() {
        return drainMultiplier;
    }
}
