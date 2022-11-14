package es.upm.miw.tamamochi.domain.model;

import es.upm.miw.tamamochi.R;

public enum CharacterType {
    AXOLITTLE(0,0, R.drawable.axolittle_default,0, 0),
    CAPYBARBARA(0,0,R.drawable.ic_launcher_foreground,0, 0); // TODO add character drawables

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
}
