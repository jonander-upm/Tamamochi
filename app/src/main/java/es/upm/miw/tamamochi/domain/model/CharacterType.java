package es.upm.miw.tamamochi.domain.model;

import es.upm.miw.tamamochi.R;

public enum CharacterType {
    AXOLITTLE(R.drawable.axolittle_egg,R.drawable.axolittle_baby, R.drawable.axolittle_default,R.drawable.axolittle_old, R.drawable.axolittle_dead),
    GECKY(R.drawable.gecky_egg,R.drawable.gecky_baby,R.drawable.gecky,R.drawable.gecky_old, R.drawable.gecky_dead);

    final int eggDrawableId;
    final int babyDrawableId;
    final int adultDrawableId;
    final int oldDrawableId;
    final int deadDrawableId;

    private CharacterType(int eggDrawableId, int babyDrawableId, int adultDrawableId, int oldDrawableId, int deadDrawableId) {
        this.eggDrawableId = eggDrawableId;
        this.babyDrawableId = babyDrawableId;
        this.adultDrawableId = adultDrawableId;
        this.oldDrawableId = oldDrawableId;
        this.deadDrawableId = deadDrawableId;
    }

    public int getEggDrawableId() {
        return eggDrawableId;
    }

    public int getBabyDrawableId() {
        return babyDrawableId;
    }

    public int getAdultDrawableId() {
        return adultDrawableId;
    }

    public int getOldDrawableId() {
        return oldDrawableId;
    }

    public int getDeadDrawableId() {
        return deadDrawableId;
    }

    public int getDrawableIdByAge(CharacterAge characterAge) {
        int characterDrawableId;
        switch (characterAge) {
            case EGG:
                characterDrawableId = this.getEggDrawableId();
                break;
            case BABY:
                characterDrawableId = this.getBabyDrawableId();
                break;
            case ADULT:
                characterDrawableId = this.getAdultDrawableId();
                break;
            case OLD:
                characterDrawableId = this.getOldDrawableId();
                break;
            default:
                characterDrawableId = this.getDeadDrawableId();
                break;
        }
        return characterDrawableId;
    }
}
