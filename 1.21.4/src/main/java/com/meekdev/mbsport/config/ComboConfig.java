package com.meekdev.mbsport.config;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class ComboConfig {

    public boolean enableMaceCombo = true;
    public boolean enableSwordCombo = true;
    public boolean enableAxeCombo = true;
    public boolean requireCriticalHit = false;
    public boolean requireAirborne = true;
    public boolean checkShieldBlocking = true;
    public int comboTimeout = 1000;
    public int pitchIncrementPercent = 10;
    public int maxPitchPercent = 200;
    public float particleScale = 2.0f;
    public SoundPack soundPack = SoundPack.DREAM_TEAM;
    public String configBackgroundTexture = "minecraft:textures/block/obsidian.png";

    public String dtOkTexture = "mace_bros_attack:particle/dtok";
    public String dtGoodTexture = "mace_bros_attack:particle/dt_good";
    public String dtGreatTexture = "mace_bros_attack:particle/dt_great";
    public String dtExcellentTexture = "mace_bros_attack:particle/dt_excellent";
    public String remakeOkTexture = "mace_bros_attack:particle/remake_ok";
    public String remakeGoodTexture = "mace_bros_attack:particle/remake_good";
    public String remakeGreatTexture = "mace_bros_attack:particle/remake_great";
    public String remakeExcellentTexture = "mace_bros_attack:particle/remake_excellent";
    public String insideStoryOkTexture = "mace_bros_attack:particle/insidestory_ok";
    public String insideStoryGoodTexture = "mace_bros_attack:particle/insidestory_good";
    public String insideStoryGreatTexture = "mace_bros_attack:particle/insidestory_great";
    public String insideStoryExcellentTexture = "mace_bros_attack:particle/insidestory_excellent";

    public enum SoundPack {
        DREAM_TEAM,
        REMAKE,
        INSIDE_STORY
    }

    public boolean isWeaponEnabled(ItemStack stack) {
        if (stack.isOf(Items.MACE)) return enableMaceCombo;

        String itemId = Registries.ITEM.getId(stack.getItem()).toString();

        if (itemId.contains("sword")) return enableSwordCombo;
        if (itemId.contains("axe")) return enableAxeCombo;

        return false;
    }

    public float getPitchIncrement() {
        return pitchIncrementPercent / 100.0f;
    }

    public float getMaxPitch() {
        return maxPitchPercent / 100.0f;
    }

    public String getTextureForParticle(String particleName) {
        return switch (particleName) {
            case "dtok" -> dtOkTexture;
            case "dt_good" -> dtGoodTexture;
            case "dt_great" -> dtGreatTexture;
            case "dt_excellent" -> dtExcellentTexture;
            case "remake_ok" -> remakeOkTexture;
            case "remake_good" -> remakeGoodTexture;
            case "remake_great" -> remakeGreatTexture;
            case "remake_excellent" -> remakeExcellentTexture;
            case "insidestory_ok" -> insideStoryOkTexture;
            case "insidestory_good" -> insideStoryGoodTexture;
            case "insidestory_great" -> insideStoryGreatTexture;
            case "insidestory_excellent" -> insideStoryExcellentTexture;
            default -> "mace_bros_attack:particle/" + particleName;
        };
    }
}