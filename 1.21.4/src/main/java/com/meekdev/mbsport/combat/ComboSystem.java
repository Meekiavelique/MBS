package com.meekdev.mbsport.combat;

import com.meekdev.mbsport.config.ComboConfig;
import com.meekdev.mbsport.data.PlayerData;
import com.meekdev.mbsport.enums.ComboType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Iterator;

public class ComboSystem {
    private final Map<String, PlayerData> playerDataMap = new ConcurrentHashMap<>();
    private final ComboConfig config;
    private long lastCleanup = 0;
    private static final long CLEANUP_INTERVAL = 60000;

    public ComboSystem(ComboConfig config) {
        this.config = config;
    }

    public boolean canTriggerCombo(PlayerEntity player, Entity target, ItemStack weapon) {
        if (!config.isWeaponEnabled(weapon)) return false;
        if (config.requireAirborne && player.isOnGround()) return false;
        if (config.checkShieldBlocking && target instanceof LivingEntity living && living.isBlocking()) return false;
        if (config.requireCriticalHit && !isCriticalHit(player)) return false;
        return true;
    }

    public void handleComboHit(PlayerEntity player, Entity target, ItemStack weapon) {
        PlayerData data = playerDataMap.computeIfAbsent(player.getUuidAsString(), k -> new PlayerData());

        ComboType currentComboType = getComboType(weapon);
        long currentTime = System.currentTimeMillis();

        if (data.lastComboType != currentComboType || data.isComboExpired(currentTime, config.comboTimeout)) {
            data.reset();
            data.lastComboType = currentComboType;
        }

        data.consecutiveHits++;
        data.lastHitTime = currentTime;

        triggerEffects(player, target, currentComboType, data.consecutiveHits);
    }

    public void updatePlayers(PlayerEntity player) {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastCleanup > CLEANUP_INTERVAL) {
            cleanupOldEntries(currentTime);
            lastCleanup = currentTime;
        }

        PlayerData data = playerDataMap.get(player.getUuidAsString());
        if (data != null) {
            if (data.isComboExpired(currentTime, config.comboTimeout)) {
                data.reset();
                return;
            }

            if (data.lastComboType == ComboType.MACE && player.isOnGround() && data.consecutiveHits > 0 && config.requireAirborne) {
                data.reset();
            }
        }
    }

    public PlayerData getPlayerData(String playerUuid) {
        return playerDataMap.get(playerUuid);
    }

    private void cleanupOldEntries(long currentTime) {
        Iterator<Map.Entry<String, PlayerData>> iterator = playerDataMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, PlayerData> entry = iterator.next();
            if (entry.getValue().isComboExpired(currentTime, config.comboTimeout * 5)) {
                iterator.remove();
            }
        }
    }

    private boolean isCriticalHit(PlayerEntity player) {
        return player.fallDistance > 0.0f && !player.isOnGround() && !player.isClimbing() &&
                !player.isTouchingWater() && !player.hasStatusEffect(net.minecraft.entity.effect.StatusEffects.BLINDNESS) &&
                !player.hasVehicle() && player.getAttackCooldownProgress(0.5f) > 0.9f;
    }

    private ComboType getComboType(ItemStack weapon) {
        if (weapon.isOf(Items.MACE)) return ComboType.MACE;

        String itemId = Registries.ITEM.getId(weapon.getItem()).toString();

        if (itemId.contains("sword")) return ComboType.SWORD;
        if (itemId.contains("axe")) return ComboType.AXE;

        return ComboType.NONE;
    }

    private void triggerEffects(PlayerEntity player, Entity target, ComboType comboType, int hits) {
        String prefix = getComboPrefix(comboType);
        SoundEvent soundEvent = getSoundForHits(prefix, hits);
        ParticleEffect particleEffect = getParticleForHits(prefix, hits);

        if (soundEvent != null) {
            try {
                float pitch = Math.min(1.0f + ((hits - 1) * config.getPitchIncrement()), config.getMaxPitch());
                player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), soundEvent, SoundCategory.PLAYERS, 1.0f, pitch, 0L);
            } catch (Exception e) {
                player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), net.minecraft.sound.SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1.0f, 1.0f, 0L);
            }
        }

        if (particleEffect != null) {
            spawnParticle(player, target, particleEffect);
        }
    }

    private String getComboPrefix(ComboType comboType) {
        return switch (config.soundPack) {
            case INSIDE_STORY -> "insidestory";
            case REMAKE -> "remake";
            default -> "dt";
        };
    }

    private void spawnParticle(PlayerEntity player, Entity target, ParticleEffect particleEffect) {
        double particleX = target.getX();
        double particleY = target.getY() + target.getEyeHeight(target.getPose()) + 0.5;
        double particleZ = target.getZ();
        player.getWorld().addParticle(particleEffect, particleX, particleY, particleZ, 0, 0.1, 0);
    }

    private ParticleEffect getParticleForHits(String prefix, int hits) {
        String particleName = switch (hits) {
            case 1 -> prefix.equals("dt") ? "dtok" : prefix + "_ok";
            case 2 -> prefix + "_good";
            case 3 -> prefix + "_great";
            default -> prefix + "_excellent";
        };

        try {
            Identifier id = Identifier.of("mace_bros_attack", particleName);
            var particleType = Registries.PARTICLE_TYPE.get(id);
            if (particleType != null) {
                return (ParticleEffect) particleType;
            }

        } catch (Exception e) {
        }

        return ParticleTypes.CRIT;
    }

    private SoundEvent getSoundForHits(String prefix, int hits) {
        String soundName = switch (hits) {
            case 1 -> prefix + "_ok";
            case 2 -> prefix + "_good";
            case 3 -> prefix + "_great";
            default -> prefix + "_excellent";
        };

        if (soundName.equals("insidestory_good")) {
            soundName = "insidestory_ok";
        }

        try {
            Identifier id = Identifier.of("mace_bros_attack", soundName);
            SoundEvent sound = Registries.SOUND_EVENT.get(id);
            if (sound != null) {
                return sound;
            }

        } catch (Exception e) {
        }

        return null;
    }
}