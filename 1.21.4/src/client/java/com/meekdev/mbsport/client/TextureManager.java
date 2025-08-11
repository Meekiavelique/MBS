package com.meekdev.mbsport.client;

import com.meekdev.mbsport.config.ComboConfig;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TextureManager implements SimpleSynchronousResourceReloadListener {
    private final ComboConfig config;
    private final Map<String, Sprite> customSprites = new ConcurrentHashMap<>();
    private final Map<String, Identifier> customTextures = new ConcurrentHashMap<>();
    private volatile SpriteAtlasTexture particleAtlas;

    public TextureManager(ComboConfig config) {
        this.config = config;
        ResourceManagerHelper.get(net.minecraft.resource.ResourceType.CLIENT_RESOURCES)
                .registerReloadListener(this);
    }

    @Override
    public Identifier getFabricId() {
        return Identifier.of("mace_bros_attack", "texture_manager");
    }

    @Override
    public void reload(ResourceManager resourceManager) {
        customSprites.clear();
        customTextures.clear();
        particleAtlas = null;
    }

    public Sprite getCustomSprite(String particleName) {
        if (customSprites.containsKey(particleName)) {
            return customSprites.get(particleName);
        }

        try {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client == null) return null;

            if (particleAtlas == null) {
                var atlasManager = client.getSpriteAtlas(SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE);
                if (atlasManager instanceof SpriteAtlasTexture atlas) {
                    particleAtlas = atlas;
                } else {
                    return null;
                }
            }

            String customTexturePath = config.getTextureForParticle(particleName);

            if (isExternalFile(customTexturePath)) {
                return loadExternalTexture(particleName, customTexturePath);
            }

            Identifier textureId = Identifier.tryParse(customTexturePath);
            if (textureId != null && particleAtlas != null) {
                Sprite sprite = particleAtlas.getSprite(textureId);
                if (sprite != null) {
                    customSprites.put(particleName, sprite);
                    return sprite;
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load custom texture for " + particleName + ": " + e.getMessage());
        }

        return null;
    }

    private boolean isExternalFile(String path) {
        return path.startsWith("/") || path.contains(":") && !path.startsWith("minecraft:") && !path.startsWith("mace_bros_attack:");
    }

    private Sprite loadExternalTexture(String particleName, String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                System.err.println("External texture file not found: " + filePath);
                return null;
            }

            if (customTextures.containsKey(particleName)) {
                Identifier existingId = customTextures.get(particleName);
                if (particleAtlas != null) {
                    Sprite sprite = particleAtlas.getSprite(existingId);
                    if (sprite != null) {

                        return sprite;
                    }
                }
            }

            MinecraftClient client = MinecraftClient.getInstance();
            if (client == null) return null;

            try (InputStream inputStream = new FileInputStream(path.toFile())) {
                NativeImage image = NativeImage.read(inputStream);
                if (image != null) {
                    NativeImageBackedTexture texture = new NativeImageBackedTexture(image);
                    Identifier textureId = Identifier.of("mace_bros_attack", "external_" + particleName + "_" + System.currentTimeMillis());

                    client.getTextureManager().registerTexture(textureId, texture);
                    customTextures.put(particleName, textureId);

                    return null;
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load external texture " + filePath + ": " + e.getMessage());
        }
        return null;
    }

    public boolean hasCustomTexture(String particleName) {
        String customTexture = config.getTextureForParticle(particleName);
        String defaultTexture = "mace_bros_attack:particle/" + particleName;
        return !customTexture.equals(defaultTexture);
    }

    public void clearCache() {
        customSprites.clear();
        customTextures.clear();
        particleAtlas = null;
    }

    public Identifier getCustomTextureId(String particleName) {
        return customTextures.get(particleName);
    }
}