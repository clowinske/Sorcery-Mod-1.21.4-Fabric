package net.code7y7.sorcerymod.entity.client;

public interface ModTrackedPlayerData {
    int getLeftHandCharge();
    int getRightHandCharge();
    int getCorruption();
    int getLeftHandSpell();
    int getRightHandSpell();

    void setLeftHandCharge(int value);
    void setRightHandCharge(int value);
    void setCorruption(int value);
    void setLeftHandSpell(int value);
    void setRightHandSpell(int value);
}
