package com.meekdev.mbsport;

import net.fabricmc.api.ModInitializer;
import com.meekdev.mbsport.config.ConfigManager;

public class Mbsport implements ModInitializer {
    public static final String MOD_ID = "mbsport";

    @Override
    public void onInitialize() {
        try {
            ConfigManager.load();
            MbsportDataGenerator.registerSounds();
            MbsportDataGenerator.registerParticles();
        } catch (Exception e) {
            System.err.println("Failed to initialize Mbsport: " + e.getMessage());
        }
    }
}