package com.meekdev.mbsport;

import net.minecraft.sound.SoundEvent;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;

public class MbsportDataGenerator {

    public static final SoundEvent DT_OK = SoundEvent.of(Identifier.of("mace_bros_attack", "dt_ok"));
    public static final SoundEvent DT_GOOD = SoundEvent.of(Identifier.of("mace_bros_attack", "dt_good"));
    public static final SoundEvent DT_GREAT = SoundEvent.of(Identifier.of("mace_bros_attack", "dt_great"));
    public static final SoundEvent DT_EXCELLENT = SoundEvent.of(Identifier.of("mace_bros_attack", "dt_excellent"));
    public static final SoundEvent REMAKE_OK = SoundEvent.of(Identifier.of("mace_bros_attack", "remake_ok"));
    public static final SoundEvent REMAKE_GOOD = SoundEvent.of(Identifier.of("mace_bros_attack", "remake_good"));
    public static final SoundEvent REMAKE_GREAT = SoundEvent.of(Identifier.of("mace_bros_attack", "remake_great"));
    public static final SoundEvent REMAKE_EXCELLENT = SoundEvent.of(Identifier.of("mace_bros_attack", "remake_excellent"));
    public static final SoundEvent IS_OK = SoundEvent.of(Identifier.of("mace_bros_attack", "is_ok"));
    public static final SoundEvent IS_GREAT = SoundEvent.of(Identifier.of("mace_bros_attack", "is_great"));
    public static final SoundEvent IS_EXCELLENT = SoundEvent.of(Identifier.of("mace_bros_attack", "is_excellent"));

    public static final SimpleParticleType DTOK_PARTICLE = FabricParticleTypes.simple(true);
    public static final SimpleParticleType DT_GOOD_PARTICLE = FabricParticleTypes.simple(true);
    public static final SimpleParticleType DT_GREAT_PARTICLE = FabricParticleTypes.simple(true);
    public static final SimpleParticleType DT_EXCELLENT_PARTICLE = FabricParticleTypes.simple(true);
    public static final SimpleParticleType REMAKE_OK_PARTICLE = FabricParticleTypes.simple(true);
    public static final SimpleParticleType REMAKE_GOOD_PARTICLE = FabricParticleTypes.simple(true);
    public static final SimpleParticleType REMAKE_GREAT_PARTICLE = FabricParticleTypes.simple(true);
    public static final SimpleParticleType REMAKE_EXCELLENT_PARTICLE = FabricParticleTypes.simple(true);
    public static final SimpleParticleType INSIDESTORY_OK_PARTICLE = FabricParticleTypes.simple(true);
    public static final SimpleParticleType INSIDESTORY_GOOD_PARTICLE = FabricParticleTypes.simple(true);
    public static final SimpleParticleType INSIDESTORY_GREAT_PARTICLE = FabricParticleTypes.simple(true);
    public static final SimpleParticleType INSIDESTORY_EXCELLENT_PARTICLE = FabricParticleTypes.simple(true);
    // This DefaultParticleType gets called when you want to use your particle in code.
    public static final SimpleParticleType SPARKLE_PARTICLE = FabricParticleTypes.simple();

    public static void registerSounds() {
        Registry.register(Registries.SOUND_EVENT, Identifier.of("mace_bros_attack", "dt_ok"), DT_OK);
        Registry.register(Registries.SOUND_EVENT, Identifier.of("mace_bros_attack", "dt_good"), DT_GOOD);
        Registry.register(Registries.SOUND_EVENT, Identifier.of("mace_bros_attack", "dt_great"), DT_GREAT);
        Registry.register(Registries.SOUND_EVENT, Identifier.of("mace_bros_attack", "dt_excellent"), DT_EXCELLENT);
        Registry.register(Registries.SOUND_EVENT, Identifier.of("mace_bros_attack", "remake_ok"), REMAKE_OK);
        Registry.register(Registries.SOUND_EVENT, Identifier.of("mace_bros_attack", "remake_good"), REMAKE_GOOD);
        Registry.register(Registries.SOUND_EVENT, Identifier.of("mace_bros_attack", "remake_great"), REMAKE_GREAT);
        Registry.register(Registries.SOUND_EVENT, Identifier.of("mace_bros_attack", "remake_excellent"), REMAKE_EXCELLENT);
        Registry.register(Registries.SOUND_EVENT, Identifier.of("mace_bros_attack", "is_ok"), IS_OK);
        Registry.register(Registries.SOUND_EVENT, Identifier.of("mace_bros_attack", "is_great"), IS_GREAT);
        Registry.register(Registries.SOUND_EVENT, Identifier.of("mace_bros_attack", "is_excellent"), IS_EXCELLENT);
    }

    public static void registerParticles() {
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of("mace_bros_attack", "dtok"), DTOK_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of("mace_bros_attack", "dt_good"), DT_GOOD_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of("mace_bros_attack", "dt_great"), DT_GREAT_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of("mace_bros_attack", "dt_excellent"), DT_EXCELLENT_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of("mace_bros_attack", "remake_ok"), REMAKE_OK_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of("mace_bros_attack", "remake_good"), REMAKE_GOOD_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of("mace_bros_attack", "remake_great"), REMAKE_GREAT_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of("mace_bros_attack", "remake_excellent"), REMAKE_EXCELLENT_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of("mace_bros_attack", "insidestory_ok"), INSIDESTORY_OK_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of("mace_bros_attack", "insidestory_good"), INSIDESTORY_GOOD_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of("mace_bros_attack", "insidestory_great"), INSIDESTORY_GREAT_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of("mace_bros_attack", "insidestory_excellent"), INSIDESTORY_EXCELLENT_PARTICLE);
        // Register our custom particle type in the mod initializer.
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of("mace_bros_attack", "sparkle_particle"), SPARKLE_PARTICLE);
    }
}