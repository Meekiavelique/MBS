package com.meekdev.mbsport.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.EndRodParticle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import com.meekdev.mbsport.MbsportDataGenerator;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class MbsportClient implements ClientModInitializer {

    private static final Map<String, PlayerData> playerDataMap = new HashMap<>();

    public static class PlayerData {
        public int consecutiveHits = 0;
        public boolean isInAir = false;
        public double cooldown = 0;
    }

    public static class WorldData {
        public boolean remadeSounds = false;
        public boolean insideStorySounds = false;
    }

    private static WorldData worldData = new WorldData();

    @Override
    public void onInitializeClient() {
        registerParticles();
        registerEvents();
        registerCommands();
    }

    private void registerParticles() {
        ParticleFactoryRegistry.getInstance().register(MbsportDataGenerator.DTOK_PARTICLE,
                (spriteProvider) -> (parameters, world, x, y, z, velocityX, velocityY, velocityZ) ->
                        new CustomParticle(world, x, y, z, spriteProvider, velocityX, velocityY, velocityZ));

        ParticleFactoryRegistry.getInstance().register(MbsportDataGenerator.DT_GOOD_PARTICLE,
                (spriteProvider) -> (parameters, world, x, y, z, velocityX, velocityY, velocityZ) ->
                        new CustomParticle(world, x, y, z, spriteProvider, velocityX, velocityY, velocityZ));

        ParticleFactoryRegistry.getInstance().register(MbsportDataGenerator.DT_GREAT_PARTICLE,
                (spriteProvider) -> (parameters, world, x, y, z, velocityX, velocityY, velocityZ) ->
                        new CustomParticle(world, x, y, z, spriteProvider, velocityX, velocityY, velocityZ));

        ParticleFactoryRegistry.getInstance().register(MbsportDataGenerator.DT_EXCELLENT_PARTICLE,
                (spriteProvider) -> (parameters, world, x, y, z, velocityX, velocityY, velocityZ) ->
                        new CustomParticle(world, x, y, z, spriteProvider, velocityX, velocityY, velocityZ));

        ParticleFactoryRegistry.getInstance().register(MbsportDataGenerator.REMAKE_OK_PARTICLE,
                (spriteProvider) -> (parameters, world, x, y, z, velocityX, velocityY, velocityZ) ->
                        new CustomParticle(world, x, y, z, spriteProvider, velocityX, velocityY, velocityZ));

        ParticleFactoryRegistry.getInstance().register(MbsportDataGenerator.REMAKE_GOOD_PARTICLE,
                (spriteProvider) -> (parameters, world, x, y, z, velocityX, velocityY, velocityZ) ->
                        new CustomParticle(world, x, y, z, spriteProvider, velocityX, velocityY, velocityZ));

        ParticleFactoryRegistry.getInstance().register(MbsportDataGenerator.REMAKE_GREAT_PARTICLE,
                (spriteProvider) -> (parameters, world, x, y, z, velocityX, velocityY, velocityZ) ->
                        new CustomParticle(world, x, y, z, spriteProvider, velocityX, velocityY, velocityZ));

        ParticleFactoryRegistry.getInstance().register(MbsportDataGenerator.REMAKE_EXCELLENT_PARTICLE,
                (spriteProvider) -> (parameters, world, x, y, z, velocityX, velocityY, velocityZ) ->
                        new CustomParticle(world, x, y, z, spriteProvider, velocityX, velocityY, velocityZ));

        ParticleFactoryRegistry.getInstance().register(MbsportDataGenerator.INSIDESTORY_OK_PARTICLE,
                (spriteProvider) -> (parameters, world, x, y, z, velocityX, velocityY, velocityZ) ->
                        new CustomParticle(world, x, y, z, spriteProvider, velocityX, velocityY, velocityZ));

        ParticleFactoryRegistry.getInstance().register(MbsportDataGenerator.INSIDESTORY_GOOD_PARTICLE,
                (spriteProvider) -> (parameters, world, x, y, z, velocityX, velocityY, velocityZ) ->
                        new CustomParticle(world, x, y, z, spriteProvider, velocityX, velocityY, velocityZ));

        ParticleFactoryRegistry.getInstance().register(MbsportDataGenerator.INSIDESTORY_GREAT_PARTICLE,
                (spriteProvider) -> (parameters, world, x, y, z, velocityX, velocityY, velocityZ) ->
                        new CustomParticle(world, x, y, z, spriteProvider, velocityX, velocityY, velocityZ));

        ParticleFactoryRegistry.getInstance().register(MbsportDataGenerator.INSIDESTORY_EXCELLENT_PARTICLE,
                (spriteProvider) -> (parameters, world, x, y, z, velocityX, velocityY, velocityZ) ->
                        new CustomParticle(world, x, y, z, spriteProvider, velocityX, velocityY, velocityZ));

        ParticleFactoryRegistry.getInstance().register(MbsportDataGenerator.SPARKLE_PARTICLE, EndRodParticle.Factory::new);
    }



    private void registerEvents() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && client.player.isOnGround()) {
                PlayerData data = playerDataMap.computeIfAbsent(client.player.getUuidAsString(), k -> new PlayerData());
                if (data.consecutiveHits > 0) {
                    data.consecutiveHits = 0;
                }
            }
        });

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!player.isOnGround() && player.getMainHandStack().isOf(Items.MACE)) {
                if (entity instanceof PlayerEntity targetPlayer && targetPlayer.isBlocking()) {
                    return getActionResult();
                }
                handleMaceHit(player, entity);
            }
            return getActionResult();
        });
    }

    private ActionResult getActionResult() {
        try {
            Field passField = ActionResult.class.getField("PASS");
            return (ActionResult) passField.get(null);
        } catch (NoSuchFieldException e) {
            try {
                Field passField = ActionResult.class.getField("field_5811");
                return (ActionResult) passField.get(null);
            } catch (Exception ex) {
                return ActionResult.SUCCESS;
            }
        } catch (Exception e) {
            return ActionResult.SUCCESS;
        }
    }

    private void handleMaceHit(PlayerEntity player, Entity target) {
        PlayerData data = playerDataMap.computeIfAbsent(player.getUuidAsString(), k -> new PlayerData());

        data.consecutiveHits++;

        SoundEvent soundEvent = null;
        ParticleEffect particleEffect = null;

        if (!worldData.remadeSounds && !worldData.insideStorySounds) {
            soundEvent = getSoundForHits("dt", data.consecutiveHits);
            particleEffect = getParticleForHits("dt", data.consecutiveHits);
        } else if (worldData.remadeSounds) {
            soundEvent = getSoundForHits("remake", data.consecutiveHits);
            particleEffect = getParticleForHits("remake", data.consecutiveHits);
        } else if (worldData.insideStorySounds) {
            soundEvent = getSoundForHits("is", data.consecutiveHits);
            particleEffect = getParticleForHits("insidestory", data.consecutiveHits);
        }

        if (soundEvent != null) {
            float pitch = 1.0f + ((data.consecutiveHits - 1) * 0.1f);

            if (pitch > 2.0f) {
                pitch = 2.0f;
            }

            player.getWorld().playSound(player, player.getBlockPos(), soundEvent, SoundCategory.PLAYERS, 1.0f, pitch);
        }

        if (particleEffect != null) {
            double particleX = target.getX();
            double particleY = target.getY() + target.getEyeHeight(target.getPose()) + 0.5;
            double particleZ = target.getZ();

            player.getWorld().addParticle(particleEffect, particleX, particleY, particleZ, 0, 0.1, 0);
        }
    }

    private ParticleEffect getParticleForHits(String prefix, int hits) {
        String particleName = switch (hits) {
            case 1 -> prefix.equals("dt") ? "dtok" : prefix + "_ok";
            case 2 -> prefix + "_good";
            case 3 -> prefix + "_great";
            default -> prefix + "_excellent";
        };

        Identifier id = Identifier.of("mace_bros_attack", particleName);
        return (ParticleEffect) Registries.PARTICLE_TYPE.getOrEmpty(id).orElse(null);
    }

    private SoundEvent getSoundForHits(String prefix, int hits) {
        String soundName = switch (hits) {
            case 1 -> prefix + "_ok";
            case 2 -> prefix + "_good";
            case 3 -> prefix + "_great";
            default -> prefix + "_excellent";
        };
        if (soundName.equals("is_good")) {
            soundName = "is_ok";
        }
        return Registries.SOUND_EVENT.getOrEmpty(Identifier.of("mace_bros_attack", soundName)).orElse(null);
    }

    private void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("mbsportsound")
                    .requires(source -> source.hasPermissionLevel(4))
                    .then(CommandManager.literal("InsideStory")
                            .executes(context -> {
                                setSoundMode("insidestory");
                                context.getSource().sendFeedback(() -> Text.literal("Inside Story sounds enabled!"), false);
                                return 1;
                            }))
                    .then(CommandManager.literal("DreamTeam")
                            .executes(context -> {
                                setSoundMode("dreamteam");
                                context.getSource().sendFeedback(() -> Text.literal("Dream Team sounds enabled!"), false);
                                return 1;
                            }))
                    .then(CommandManager.literal("Remake")
                            .executes(context -> {
                                setSoundMode("remake");
                                context.getSource().sendFeedback(() -> Text.literal("Remade sounds enabled!"), false);
                                return 1;
                            })));
        });
    }

    private SoundEvent getSoundForJumps(String prefix, int jumps) {
        String soundName = switch (jumps) {
            case 1 -> prefix + "_ok";
            case 2 -> prefix + "_good";
            case 3 -> prefix + "_great";
            default -> prefix + "_excellent";
        };
        return Registries.SOUND_EVENT.get(Identifier.of("mace_bros_attack", soundName));
    }

    private ParticleEffect getParticleForJumps(String prefix, int jumps) {
        String particleName = switch (jumps) {
            case 1 -> prefix.equals("dt") ? "dtok" : prefix + "_ok";
            case 2 -> prefix + "_good";
            case 3 -> prefix + "_great";
            default -> prefix + "_excellent";
        };
        return (ParticleEffect) Registries.PARTICLE_TYPE.get(Identifier.of("mace_bros_attack", particleName));
    }

    public class VersionUtils {
        public static String getMinecraftVersion() {
            ModContainer mc = FabricLoader.getInstance().getModContainer("minecraft").orElseThrow();
            return mc.getMetadata().getVersion().getFriendlyString();
        }

        public static boolean isAtLeast(String majorMinor) {
            String[] current = getMinecraftVersion().split("\\.");
            String[] target  = majorMinor.split("\\.");
            for (int i = 0; i < Math.min(current.length, target.length); i++) {
                int c = Integer.parseInt(current[i]);
                int t = Integer.parseInt(target[i]);
                if (c != t) return c > t;
            }
            return true;
        }
    }

    public static void setSoundMode(String mode) {
        switch (mode.toLowerCase()) {
            case "dreamteam" -> {
                worldData.remadeSounds = false;
                worldData.insideStorySounds = false;
            }
            case "remake" -> {
                worldData.remadeSounds = true;
                worldData.insideStorySounds = false;
            }
            case "insidestory" -> {
                worldData.remadeSounds = false;
                worldData.insideStorySounds = true;
            }
        }
    }
}