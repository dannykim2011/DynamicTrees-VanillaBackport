package com.dannykim.dtvanillabackport.resources;

import com.dannykim.dtvanillabackport.registry.DTVBRegistries;
import com.dtteam.dynamictrees.api.resource.ResourceAccessor;
import com.dtteam.dynamictrees.api.resource.loading.AbstractResourceLoader;
import com.dtteam.dynamictrees.api.resource.loading.preparation.TextResourcePreparer;
import com.dtteam.dynamictrees.tree.species.Species;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.List;

public final class CactusFlowerCompatibilityLoader extends AbstractResourceLoader<List<String>> {
    private static final ResourceLocation PILLAR_CACTUS =
            ResourceLocation.fromNamespaceAndPath("dynamictreesplus", "pillar_cactus");
    private static final ResourceLocation PIPE_CACTUS =
            ResourceLocation.fromNamespaceAndPath("dynamictreesplus", "pipe_cactus");
    private static final ResourceLocation MEGA_CACTUS =
            ResourceLocation.fromNamespaceAndPath("dynamictreesplus", "mega_cactus");

    public CactusFlowerCompatibilityLoader() {
        super(new TextResourcePreparer("dtvanillabackport/cactus_flower_compatibility"));
    }

    @Override
    public void applyOnReload(final ResourceAccessor<List<String>> resourceAccessor, final ResourceManager resourceManager) {
        this.addFeature(PILLAR_CACTUS, true, false);
        this.addFeature(PIPE_CACTUS, false, true);
        this.addFeature(MEGA_CACTUS, false, false);
    }

    private void addFeature(final ResourceLocation speciesName, final boolean pillar, final boolean pipe) {
        final var feature = pillar ? DTVBRegistries.PILLAR_CACTUS_FLOWER_GEN_FEATURE
                : pipe ? DTVBRegistries.PIPE_CACTUS_FLOWER_GEN_FEATURE
                : DTVBRegistries.CACTUS_FLOWER_GEN_FEATURE;
        Species.REGISTRY.getOptional(speciesName).ifPresent(species -> {
            final boolean alreadyAdded = species.getGenFeatures().stream()
                    .anyMatch(configuration -> configuration.getGenFeature() == feature);
            if (!alreadyAdded) {
                species.addGenFeature(feature);
            }
        });
    }
}
