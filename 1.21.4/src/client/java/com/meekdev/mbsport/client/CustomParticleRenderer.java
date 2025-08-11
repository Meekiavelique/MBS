package com.meekdev.mbsport.client;

import com.meekdev.mbsport.config.ConfigManager;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class CustomParticleRenderer extends SpriteBillboardParticle {
    private final TextureManager textureManager;
    private final String particleType;

    protected CustomParticleRenderer(ClientWorld world, double x, double y, double z,
                                     SpriteProvider spriteProvider, TextureManager textureManager,
                                     String particleType, double velocityX, double velocityY, double velocityZ) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.textureManager = textureManager;
        this.particleType = particleType;

        loadCustomTexture(spriteProvider);

        this.maxAge = 40;
        this.scale(ConfigManager.getConfig().particleScale);
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;
        this.red = 1.0f;
        this.green = 1.0f;
        this.blue = 1.0f;
        this.alpha = 1.0f;
    }

    private void loadCustomTexture(SpriteProvider spriteProvider) {
        try {
            String customTexturePath = ConfigManager.getConfig().getTextureForParticle(particleType);

            if (isExternalFile(customTexturePath)) {
                Identifier customTextureId = textureManager.getCustomTextureId(particleType);
                if (customTextureId != null) {
                    MinecraftClient client = MinecraftClient.getInstance();
                    if (client != null && client.getTextureManager().getTexture(customTextureId) != null) {
                        this.setSprite(spriteProvider.getSprite(0, 1));
                        return;
                    }
                }
            } else {
                Sprite customSprite = textureManager.getCustomSprite(particleType);
                if (customSprite != null) {
                    this.setSprite(customSprite);
                    return;
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load custom texture for particle " + particleType + ": " + e.getMessage());
        }

        this.setSprite(spriteProvider.getSprite(0, 1));
    }

    private boolean isExternalFile(String path) {
        return path.startsWith("/") || (path.contains(":") && !path.startsWith("minecraft:") && !path.startsWith("mace_bros_attack:"));
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;

        if (this.age++ >= this.maxAge) {
            this.markDead();
        }

        this.velocityY -= 0.04 * this.gravityStrength;
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        this.velocityX *= 0.98;
        this.velocityY *= 0.98;
        this.velocityZ *= 0.98;

        this.alpha = 1.0f - ((float) this.age / (float) this.maxAge);
    }
}