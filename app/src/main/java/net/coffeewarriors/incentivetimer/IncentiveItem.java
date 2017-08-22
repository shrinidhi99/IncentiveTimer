package net.coffeewarriors.incentivetimer;


import java.io.Serializable;

public class IncentiveItem implements Serializable {

    private String mName;
    private int mChance;
    private boolean mUnlocked = false;

    public IncentiveItem(String name, int chance){
        mName = name;
        mChance = chance;
    }

    public String getName() {
        return mName;
    }

    public void setName(String newName) {
        mName = newName;
    }

    public int getChance() {
        return mChance;
    }

    public void setChance(int newChance) {
        mChance = newChance;
    }

    public boolean isUnlocked() {
        return mUnlocked;
    }

    public void unlock() {
        mUnlocked = true;
    }

    public void lock() {
        mUnlocked = false;
    }
}
