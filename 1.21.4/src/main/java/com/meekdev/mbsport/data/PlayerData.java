package com.meekdev.mbsport.data;

import com.meekdev.mbsport.enums.ComboType;

public class PlayerData {
    public int consecutiveHits = 0;
    public boolean isInAir = false;
    public double cooldown = 0;
    public long lastHitTime = 0;
    public ComboType lastComboType = ComboType.NONE;

    public void reset() {
        consecutiveHits = 0;
        lastComboType = ComboType.NONE;
    }

    public boolean isComboExpired(long currentTime, int timeoutMs) {
        return currentTime - lastHitTime > timeoutMs;
    }
}