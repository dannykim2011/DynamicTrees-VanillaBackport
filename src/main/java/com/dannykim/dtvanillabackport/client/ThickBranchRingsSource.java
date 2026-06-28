package com.dannykim.dtvanillabackport.client;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.Optional;

public final class ThickBranchRingsSource implements SpriteSource {
    public static final Codec<ThickBranchRingsSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("resource").forGetter(source -> source.resourceId)
    ).apply(instance, ThickBranchRingsSource::new));

    static SpriteSourceType TYPE;
    private final ResourceLocation resourceId;

    public ThickBranchRingsSource(final ResourceLocation resourceId) {
        this.resourceId = resourceId;
    }

    @Override
    public void run(final ResourceManager resourceManager, final Output output) {
        final ResourceLocation textureFile = TEXTURE_ID_CONVERTER.idToFile(this.resourceId);
        final Optional<Resource> resource = resourceManager.getResource(textureFile);
        if (resource.isEmpty()) {
            return;
        }

        final ResourceLocation thickId = new ResourceLocation(
                this.resourceId.getNamespace(),
                this.resourceId.getPath() + "_thick"
        );
        output.add(thickId, () -> {
            final SpriteContents base = SpriteLoader.loadSprite(thickId, resource.get());
            if (base == null) {
                return null;
            }
            try {
                return new ThickBranchRingsSprite(thickId, base);
            } finally {
                base.close();
            }
        });
    }

    @Override
    public SpriteSourceType type() {
        return TYPE;
    }
}
