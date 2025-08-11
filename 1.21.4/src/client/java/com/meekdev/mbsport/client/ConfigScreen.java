package com.meekdev.mbsport.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import com.meekdev.mbsport.config.ConfigManager;
import com.meekdev.mbsport.config.ComboConfig;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ConfigScreen {

    private static Identifier backgroundTexture = null;

    public static Screen createConfigScreen(Screen parent) {
        ComboConfig config = ConfigManager.getConfig();
        loadCustomBackground(config.configBackgroundTexture);

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.literal("MBSport Configuration"))
                .setDefaultBackgroundTexture(backgroundTexture != null ? backgroundTexture : Identifier.of("minecraft", "textures/block/obsidian.png"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        ConfigCategory weapons = builder.getOrCreateCategory(Text.literal("Weapons"));
        weapons.addEntry(entryBuilder.startBooleanToggle(Text.literal("Enable Mace Combo"), config.enableMaceCombo)
                .setDefaultValue(true)
                .setTooltip(Text.literal("Enable combo system for maces"))
                .setSaveConsumer(newValue -> config.enableMaceCombo = newValue)
                .build());

        weapons.addEntry(entryBuilder.startBooleanToggle(Text.literal("Enable Sword Combo"), config.enableSwordCombo)
                .setDefaultValue(true)
                .setTooltip(Text.literal("Enable combo system for swords"))
                .setSaveConsumer(newValue -> config.enableSwordCombo = newValue)
                .build());

        weapons.addEntry(entryBuilder.startBooleanToggle(Text.literal("Enable Axe Combo"), config.enableAxeCombo)
                .setDefaultValue(true)
                .setTooltip(Text.literal("Enable combo system for axes"))
                .setSaveConsumer(newValue -> config.enableAxeCombo = newValue)
                .build());

        ConfigCategory requirements = builder.getOrCreateCategory(Text.literal("Requirements"));
        requirements.addEntry(entryBuilder.startBooleanToggle(Text.literal("Require Critical Hit"), config.requireCriticalHit)
                .setDefaultValue(false)
                .setTooltip(Text.literal("Only trigger combos on critical hits"))
                .setSaveConsumer(newValue -> config.requireCriticalHit = newValue)
                .build());

        requirements.addEntry(entryBuilder.startBooleanToggle(Text.literal("Require Airborne"), config.requireAirborne)
                .setDefaultValue(true)
                .setTooltip(Text.literal("Only trigger combos when player is airborne"))
                .setSaveConsumer(newValue -> config.requireAirborne = newValue)
                .build());

        requirements.addEntry(entryBuilder.startBooleanToggle(Text.literal("Block Shield Hits"), config.checkShieldBlocking)
                .setDefaultValue(true)
                .setTooltip(Text.literal("Prevent combos when target is blocking with shield"))
                .setSaveConsumer(newValue -> config.checkShieldBlocking = newValue)
                .build());

        ConfigCategory timing = builder.getOrCreateCategory(Text.literal("Timing"));
        timing.addEntry(entryBuilder.startIntSlider(Text.literal("Combo Timeout (ms)"), config.comboTimeout, 500, 10000)
                .setDefaultValue(1000)
                .setTooltip(Text.literal("Time before combo resets"))
                .setSaveConsumer(newValue -> config.comboTimeout = newValue)
                .build());

        ConfigCategory particles = builder.getOrCreateCategory(Text.literal("Particles"));
        particles.addEntry(entryBuilder.startFloatField(Text.literal("Particle Scale"), config.particleScale)
                .setDefaultValue(2.0f)
                .setMin(0.1f)
                .setMax(10.0f)
                .setTooltip(Text.literal("Size multiplier for particles"))
                .setSaveConsumer(newValue -> config.particleScale = newValue)
                .build());

        ConfigCategory audio = builder.getOrCreateCategory(Text.literal("Audio"));
        audio.addEntry(entryBuilder.startIntSlider(Text.literal("Pitch Increment %"), config.pitchIncrementPercent, 0, 100)
                .setDefaultValue(10)
                .setTooltip(Text.literal("Pitch increase per combo hit"))
                .setSaveConsumer(newValue -> config.pitchIncrementPercent = newValue)
                .build());

        audio.addEntry(entryBuilder.startIntSlider(Text.literal("Max Pitch %"), config.maxPitchPercent, 100, 300)
                .setDefaultValue(200)
                .setTooltip(Text.literal("Maximum pitch multiplier"))
                .setSaveConsumer(newValue -> config.maxPitchPercent = newValue)
                .build());

        audio.addEntry(entryBuilder.startEnumSelector(Text.literal("Sound Pack"), ComboConfig.SoundPack.class, config.soundPack)
                .setDefaultValue(ComboConfig.SoundPack.DREAM_TEAM)
                .setTooltip(Text.literal("Select sound pack"))
                .setSaveConsumer(newValue -> config.soundPack = newValue)
                .build());

        ConfigCategory appearance = builder.getOrCreateCategory(Text.literal("Appearance"));
        appearance.addEntry(entryBuilder.startStrField(Text.literal("Config Background"), config.configBackgroundTexture)
                .setDefaultValue("minecraft:textures/block/obsidian.png")
                .setTooltip(Text.literal("Custom background texture for config screen (supports file paths)"))
                .setSaveConsumer(newValue -> config.configBackgroundTexture = newValue)
                .build());

        ConfigCategory textures = builder.getOrCreateCategory(Text.literal("Textures"));
        textures.addEntry(entryBuilder.startStrField(Text.literal("DT OK Texture"), config.dtOkTexture)
                .setDefaultValue("mace_bros_attack:particle/dtok")
                .setTooltip(Text.literal("Custom texture path for DT OK particle (supports file paths)"))
                .setSaveConsumer(newValue -> config.dtOkTexture = newValue)
                .build());

        textures.addEntry(entryBuilder.startStrField(Text.literal("DT Good Texture"), config.dtGoodTexture)
                .setDefaultValue("mace_bros_attack:particle/dt_good")
                .setTooltip(Text.literal("Custom texture path for DT Good particle (supports file paths)"))
                .setSaveConsumer(newValue -> config.dtGoodTexture = newValue)
                .build());

        textures.addEntry(entryBuilder.startStrField(Text.literal("DT Great Texture"), config.dtGreatTexture)
                .setDefaultValue("mace_bros_attack:particle/dt_great")
                .setTooltip(Text.literal("Custom texture path for DT Great particle (supports file paths)"))
                .setSaveConsumer(newValue -> config.dtGreatTexture = newValue)
                .build());

        textures.addEntry(entryBuilder.startStrField(Text.literal("DT Excellent Texture"), config.dtExcellentTexture)
                .setDefaultValue("mace_bros_attack:particle/dt_excellent")
                .setTooltip(Text.literal("Custom texture path for DT Excellent particle (supports file paths)"))
                .setSaveConsumer(newValue -> config.dtExcellentTexture = newValue)
                .build());

        textures.addEntry(entryBuilder.startStrField(Text.literal("Remake OK Texture"), config.remakeOkTexture)
                .setDefaultValue("mace_bros_attack:particle/remake_ok")
                .setTooltip(Text.literal("Custom texture path for Remake OK particle (supports file paths)"))
                .setSaveConsumer(newValue -> config.remakeOkTexture = newValue)
                .build());

        textures.addEntry(entryBuilder.startStrField(Text.literal("Remake Good Texture"), config.remakeGoodTexture)
                .setDefaultValue("mace_bros_attack:particle/remake_good")
                .setTooltip(Text.literal("Custom texture path for Remake Good particle (supports file paths)"))
                .setSaveConsumer(newValue -> config.remakeGoodTexture = newValue)
                .build());

        textures.addEntry(entryBuilder.startStrField(Text.literal("Remake Great Texture"), config.remakeGreatTexture)
                .setDefaultValue("mace_bros_attack:particle/remake_great")
                .setTooltip(Text.literal("Custom texture path for Remake Great particle (supports file paths)"))
                .setSaveConsumer(newValue -> config.remakeGreatTexture = newValue)
                .build());

        textures.addEntry(entryBuilder.startStrField(Text.literal("Remake Excellent Texture"), config.remakeExcellentTexture)
                .setDefaultValue("mace_bros_attack:particle/remake_excellent")
                .setTooltip(Text.literal("Custom texture path for Remake Excellent particle (supports file paths)"))
                .setSaveConsumer(newValue -> config.remakeExcellentTexture = newValue)
                .build());

        textures.addEntry(entryBuilder.startStrField(Text.literal("Inside Story OK Texture"), config.insideStoryOkTexture)
                .setDefaultValue("mace_bros_attack:particle/insidestory_ok")
                .setTooltip(Text.literal("Custom texture path for Inside Story OK particle (supports file paths)"))
                .setSaveConsumer(newValue -> config.insideStoryOkTexture = newValue)
                .build());

        textures.addEntry(entryBuilder.startStrField(Text.literal("Inside Story Good Texture"), config.insideStoryGoodTexture)
                .setDefaultValue("mace_bros_attack:particle/insidestory_good")
                .setTooltip(Text.literal("Custom texture path for Inside Story Good particle (supports file paths)"))
                .setSaveConsumer(newValue -> config.insideStoryGoodTexture = newValue)
                .build());

        textures.addEntry(entryBuilder.startStrField(Text.literal("Inside Story Great Texture"), config.insideStoryGreatTexture)
                .setDefaultValue("mace_bros_attack:particle/insidestory_great")
                .setTooltip(Text.literal("Custom texture path for Inside Story Great particle (supports file paths)"))
                .setSaveConsumer(newValue -> config.insideStoryGreatTexture = newValue)
                .build());

        textures.addEntry(entryBuilder.startStrField(Text.literal("Inside Story Excellent Texture"), config.insideStoryExcellentTexture)
                .setDefaultValue("mace_bros_attack:particle/insidestory_excellent")
                .setTooltip(Text.literal("Custom texture path for Inside Story Excellent particle (supports file paths)"))
                .setSaveConsumer(newValue -> config.insideStoryExcellentTexture = newValue)
                .build());

        builder.setSavingRunnable(() -> {
            try {
                ConfigManager.save();
                MbsportClient.reloadTextures();
                loadCustomBackground(config.configBackgroundTexture);
            } catch (Exception e) {
                System.err.println("Failed to save config: " + e.getMessage());
            }
        });

        return builder.build();
    }

    private static void loadCustomBackground(String backgroundPath) {
        try {
            if (backgroundPath == null || backgroundPath.isEmpty()) {
                backgroundTexture = Identifier.of("minecraft", "textures/block/obsidian.png");
                return;
            }

            if (backgroundPath.startsWith("/") || (backgroundPath.contains(":") &&
                    !backgroundPath.startsWith("minecraft:") && !backgroundPath.startsWith("mace_bros_attack:"))) {

                if (Files.exists(Paths.get(backgroundPath))) {
                    try (FileInputStream inputStream = new FileInputStream(backgroundPath)) {
                        NativeImage image = NativeImage.read(inputStream);
                        if (image != null) {
                            NativeImageBackedTexture texture = new NativeImageBackedTexture(image);
                            Identifier textureId = Identifier.of("mace_bros_attack", "config_background_" + System.currentTimeMillis());

                            MinecraftClient client = MinecraftClient.getInstance();
                            if (client != null) {
                                client.getTextureManager().registerTexture(textureId, texture);
                                backgroundTexture = textureId;
                                return;
                            }
                        }
                    }
                }
            } else {
                Identifier id = Identifier.tryParse(backgroundPath);
                if (id != null) {
                    backgroundTexture = id;
                    return;
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load custom background: " + e.getMessage());
        }

        backgroundTexture = Identifier.of("minecraft", "textures/block/obsidian.png");
    }

    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ConfigScreen::createConfigScreen;
    }
}