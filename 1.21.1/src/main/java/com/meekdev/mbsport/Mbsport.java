package com.meekdev.mbsport;

import net.fabricmc.api.ModInitializer;

public class Mbsport implements ModInitializer {
    public static final String MOD_ID = "mbsport";

    @Override
    public void onInitialize() {
        com.meekdev.mbsport.MbsportDataGenerator.registerSounds();
        com.meekdev.mbsport.MbsportDataGenerator.registerParticles();
    }
}