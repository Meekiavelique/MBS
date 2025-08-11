package com.meekdev.mbsport.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.particle.EndRodParticle;
import net.minecraft.util.ActionResult;
import com.meekdev.mbsport.config.ConfigManager;
import com.meekdev.mbsport.config.ComboConfig;
import com.meekdev.mbsport.combat.ComboSystem;
import com.meekdev.mbsport.data.PlayerData;
import com.meekdev.mbsport.client.TextureManager;
import com.meekdev.mbsport.MbsportDataGenerator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MbsportClient implements ClientModInitializer {

    private static ComboConfig config;
    private static ComboSystem comboSystem;
    private static TextureManager textureManager;
    private static final Map<String, PlayerData> clientPlayerData = new ConcurrentHashMap<>();

    @Override
    public void onInitializeClient() {
        try {
            ConfigManager.load();
            config = ConfigManager.getConfig();
            comboSystem = new ComboSystem(config);
            textureManager = new TextureManager(config);

            registerParticles();
            registerEvents();
            ComboHudOverlay.register();
        } catch (Exception e) {
            System.err.println("Failed to initialize MbsportClient: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void registerParticles() {
        try {
            ParticleFactoryRegistry registry = ParticleFactoryRegistry.getInstance();

            registry.register(MbsportDataGenerator.DTOK_PARTICLE, spriteProvider ->
                    (parameters, world, x, y, z, velocityX, velocityY, velocityZ) ->
                            new CustomParticleRenderer(world, x, y, z, spriteProvider, getTextureManager(), "dtok", velocityX, velocityY, velocityZ));

            registry.register(MbsportDataGenerator.DT_GOOD_PARTICLE, spriteProvider ->
                    (parameters, world, x, y, z, velocityX, velocityY, velocityZ) ->
                            new CustomParticleRenderer(world, x, y, z, spriteProvider, getTextureManager(), "dt_good", velocityX, velocityY, velocityZ));

            registry.register(MbsportDataGenerator.DT_GREAT_PARTICLE, spriteProvider ->
                    (parameters, world, x, y, z, velocityX, velocityY, velocityZ) ->
                            new CustomParticleRenderer(world, x, y, z, spriteProvider, getTextureManager(), "dt_great", velocityX, velocityY, velocityZ));

            registry.register(MbsportDataGenerator.DT_EXCELLENT_PARTICLE, spriteProvider ->
                    (parameters, world, x, y, z, velocityX, velocityY, velocityZ) ->
                            new CustomParticleRenderer(world, x, y, z, spriteProvider, getTextureManager(), "dt_excellent", velocityX, velocityY, velocityZ));

            registry.register(MbsportDataGenerator.REMAKE_OK_PARTICLE, spriteProvider ->
                    (parameters, world, x, y, z, velocityX, velocityY, velocityZ) ->
                            new CustomParticleRenderer(world, x, y, z, spriteProvider, getTextureManager(), "remake_ok", velocityX, velocityY, velocityZ));

            registry.register(MbsportDataGenerator.REMAKE_GOOD_PARTICLE, spriteProvider ->
                    (parameters, world, x, y, z, velocityX, velocityY, velocityZ) ->
                            new CustomParticleRenderer(world, x, y, z, spriteProvider, getTextureManager(), "remake_good", velocityX, velocityY, velocityZ));

            registry.register(MbsportDataGenerator.REMAKE_GREAT_PARTICLE, spriteProvider ->
                    (parameters, world, x, y, z, velocityX, velocityY, velocityZ) ->
                            new CustomParticleRenderer(world, x, y, z, spriteProvider, getTextureManager(), "remake_great", velocityX, velocityY, velocityZ));

            registry.register(MbsportDataGenerator.REMAKE_EXCELLENT_PARTICLE, spriteProvider ->
                    (parameters, world, x, y, z, velocityX, velocityY, velocityZ) ->
                            new CustomParticleRenderer(world, x, y, z, spriteProvider, getTextureManager(), "remake_excellent", velocityX, velocityY, velocityZ));

            registry.register(MbsportDataGenerator.INSIDESTORY_OK_PARTICLE, spriteProvider ->
                    (parameters, world, x, y, z, velocityX, velocityY, velocityZ) ->
                            new CustomParticleRenderer(world, x, y, z, spriteProvider, getTextureManager(), "insidestory_ok", velocityX, velocityY, velocityZ));

            registry.register(MbsportDataGenerator.INSIDESTORY_GOOD_PARTICLE, spriteProvider ->
                    (parameters, world, x, y, z, velocityX, velocityY, velocityZ) ->
                            new CustomParticleRenderer(world, x, y, z, spriteProvider, getTextureManager(), "insidestory_good", velocityX, velocityY, velocityZ));

            registry.register(MbsportDataGenerator.INSIDESTORY_GREAT_PARTICLE, spriteProvider ->
                    (parameters, world, x, y, z, velocityX, velocityY, velocityZ) ->
                            new CustomParticleRenderer(world, x, y, z, spriteProvider, getTextureManager(), "insidestory_great", velocityX, velocityY, velocityZ));

            registry.register(MbsportDataGenerator.INSIDESTORY_EXCELLENT_PARTICLE, spriteProvider ->
                    (parameters, world, x, y, z, velocityX, velocityY, velocityZ) ->
                            new CustomParticleRenderer(world, x, y, z, spriteProvider, getTextureManager(), "insidestory_excellent", velocityX, velocityY, velocityZ));

            registry.register(MbsportDataGenerator.SPARKLE_PARTICLE, EndRodParticle.Factory::new);
        } catch (Exception e) {
            System.err.println("Failed to register particles: " + e.getMessage());
        }
    }

    private void registerEvents() {
        try {
            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                try {
                    if (client.player != null && comboSystem != null) {
                        comboSystem.updatePlayers(client.player);

                        PlayerData serverData = comboSystem.getPlayerData(client.player.getUuidAsString());
                        if (serverData != null) {
                            clientPlayerData.put(client.player.getUuidAsString(), serverData);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error in client tick: " + e.getMessage());
                }
            });

            AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
                try {
                    if (comboSystem != null && comboSystem.canTriggerCombo(player, entity, player.getMainHandStack())) {
                        comboSystem.handleComboHit(player, entity, player.getMainHandStack());
                    }
                } catch (Exception e) {
                    System.err.println("Error handling combo hit: " + e.getMessage());
                }
                return ActionResult.PASS;
            });
        } catch (Exception e) {
            System.err.println("Failed to register events: " + e.getMessage());
        }
    }

    public static PlayerData getPlayerData(String playerUuid) {
        return clientPlayerData.get(playerUuid);
    }

    private static TextureManager getTextureManager() {
        if (textureManager == null) {
            textureManager = new TextureManager(ConfigManager.getConfig());
        }
        return textureManager;
    }

    public static ComboConfig getConfig() {
        if (config == null) {
            config = ConfigManager.getConfig();
        }
        return config;
    }

    public static void reloadTextures() {
        try {
            if (textureManager != null) {
                textureManager.clearCache();
            }

            if (comboSystem != null) {
                comboSystem = new ComboSystem(ConfigManager.getConfig());
            }
        } catch (Exception e) {
            System.err.println("Failed to reload textures: " + e.getMessage());
        }
    }
}