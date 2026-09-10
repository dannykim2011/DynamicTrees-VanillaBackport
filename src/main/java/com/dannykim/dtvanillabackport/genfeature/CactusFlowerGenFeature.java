package com.dannykim.dtvanillabackport.genfeature;

import com.blackgear.vanillabackport.core.VanillaBackport;
import com.dannykim.dtvanillabackport.registry.DTVBRegistries;
import com.dtteam.dynamictrees.systems.genfeature.GenFeature;
import com.dtteam.dynamictrees.systems.genfeature.GenFeatureConfiguration;
import com.dtteam.dynamictrees.systems.genfeature.context.PostGenerationContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public final class CactusFlowerGenFeature extends GenFeature {
    private static final ResourceLocation CACTUS_BRANCH =
            ResourceLocation.fromNamespaceAndPath("dynamictreesplus", "cactus_branch");
    private final boolean pillar;
    private final boolean pipe;

    public CactusFlowerGenFeature(final ResourceLocation registryName, final boolean pillar, final boolean pipe) {
        super(registryName);
        this.pillar = pillar;
        this.pipe = pipe;
    }

    @Override
    protected void registerProperties() {
        this.register(PLACE_CHANCE);
    }

    @Override
    public GenFeatureConfiguration createDefaultConfiguration() {
        return super.createDefaultConfiguration().with(PLACE_CHANCE, 0.25F);
    }

    @Override
    protected boolean postGenerate(final GenFeatureConfiguration configuration, final PostGenerationContext context) {
        if (!context.isWorldGen() || !Boolean.TRUE.equals(VanillaBackport.COMMON_CONFIG.hasCactusFlowers.get())) {
            return false;
        }

        final LevelAccessor level = context.level();
        if (!level.getBiome(context.pos()).is(Biomes.DESERT)) {
            return false;
        }
        final Block flower = DTVBRegistries.DYNAMIC_CACTUS_FLOWER.get();

        boolean placed = false;
        for (final BlockPos endPoint : context.endPoints()) {
            final BlockState endState = level.getBlockState(endPoint);
            if (!CACTUS_BRANCH.equals(BuiltInRegistries.BLOCK.getKey(endState.getBlock()))
                    || context.random().nextFloat() > configuration.get(PLACE_CHANCE)) {
                continue;
            }

            final BlockPos flowerPos = endPoint.above();
            final BlockState flowerState = flower.defaultBlockState()
                    .setValue(com.dannykim.dtvanillabackport.block.DynamicCactusFlowerBlock.PILLAR,
                            this.pillar && endState.getValues().entrySet().stream()
                                    .noneMatch(entry -> entry.getKey().getName().equals("type")
                                            && entry.getValue().toString().equals("branch")))
                    .setValue(com.dannykim.dtvanillabackport.block.DynamicCactusFlowerBlock.PIPE, this.pipe);
            if (level.getBlockState(flowerPos).isAir() && flowerState.canSurvive(level, flowerPos)) {
                level.setBlock(flowerPos, flowerState, Block.UPDATE_ALL);
                placed = true;
            }
        }
        return placed;
    }
}
