package com.dannykim.dtvanillabackport.genfeature;

import com.blackgear.vanillabackport.common.level.block.states.CreakingHeartState;
import com.dtteam.dynamictrees.api.configuration.ConfigurationProperty;
import com.dtteam.dynamictrees.block.branch.BranchBlock;
import com.dtteam.dynamictrees.systems.genfeature.GenFeature;
import com.dtteam.dynamictrees.systems.genfeature.GenFeatureConfiguration;
import com.dtteam.dynamictrees.systems.genfeature.context.PostGenerationContext;
import com.dtteam.dynamictrees.systems.genfeature.context.PostGrowContext;
import com.dtteam.dynamictrees.systems.growthlogic.context.PositionalSpeciesContext;
import com.dtteam.dynamictrees.tree.TreeHelper;
import com.dannykim.dtvanillabackport.block.CreakingHeartBranchBlock;
import com.dannykim.dtvanillabackport.tree.CreakingHeartFamily;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class CreakingHeartGenFeature extends GenFeature {
    public static final ConfigurationProperty<Integer> MAX_RADIUS = ConfigurationProperty.integer("max_radius");
    public static final ConfigurationProperty<Integer> MIN_RADIUS = ConfigurationProperty.integer("min_radius");
    public static final ConfigurationProperty<Integer> MAX_HEIGHT = ConfigurationProperty.integer("max_height");

    public CreakingHeartGenFeature(final ResourceLocation registryName) {
        super(registryName);
    }

    @Override
    protected void registerProperties() {
        this.register(MAX_HEIGHT, MAX_RADIUS, MIN_RADIUS, PLACE_CHANCE, FRUITING_RADIUS);
    }

    @Override
    public GenFeatureConfiguration createDefaultConfiguration() {
        return super.createDefaultConfiguration()
                .with(MAX_HEIGHT, 8)
                .with(MAX_RADIUS, 24)
                .with(MIN_RADIUS, 6)
                .with(PLACE_CHANCE, 0.2F)
                .with(FRUITING_RADIUS, 14);
    }

    @Override
    protected boolean postGenerate(final GenFeatureConfiguration configuration, final PostGenerationContext context) {
        return this.tryPlaceHeart(configuration, context);
    }

    @Override
    protected boolean postGrow(final GenFeatureConfiguration configuration, final PostGrowContext context) {
        return false;
    }

    private boolean tryPlaceHeart(final GenFeatureConfiguration configuration, final PostGenerationContext context) {
        if (!(context.species().getFamily() instanceof CreakingHeartFamily heartFamily)
                || heartFamily.getHeartBranch().isEmpty()
                || context.random().nextFloat() > configuration.get(PLACE_CHANCE)) {
            return false;
        }

        final LevelAccessor level = context.level();
        if (TreeHelper.getRadius(level, context.pos().above()) < configuration.get(FRUITING_RADIUS)) {
            return false;
        }

        final BranchBlock heart = heartFamily.getHeartBranch().get();
        final int maxHeight = configuration.get(MAX_HEIGHT);
        final int lowestBranchHeight = context.species().getGrowthLogicKit().getLowestBranchHeight(
                new PositionalSpeciesContext(context.levelContext().level(), context.pos(), context.species())
        );

        for (int y = 1; y < maxHeight; y++) {
            if (lowestBranchHeight + y > maxHeight) {
                return false;
            }
            final BlockPos testPos = context.pos().above(lowestBranchHeight + y);
            final BlockState testState = level.getBlockState(testPos);
            if (!TreeHelper.isBranch(testState)) {
                return false;
            }
            if (this.isRadiusJustRight(level, testPos, configuration)
                    && TreeHelper.isBranch(level.getBlockState(testPos.above()))) {
                heart.setRadius(level, testPos, TreeHelper.getRadius(level, testPos), Direction.DOWN, 3);
                final BlockState placedState = level.getBlockState(testPos);
                if (placedState.getBlock() instanceof CreakingHeartBranchBlock) {
                    level.setBlock(testPos, placedState
                            .setValue(CreakingHeartBranchBlock.STATE, CreakingHeartState.DORMANT)
                            .setValue(CreakingHeartBranchBlock.HIDDEN, true), 3);
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    private boolean isRadiusJustRight(final LevelAccessor level, final BlockPos pos, final GenFeatureConfiguration configuration) {
        final int radius = TreeHelper.getRadius(level, pos);
        return radius >= configuration.get(MIN_RADIUS) && radius <= configuration.get(MAX_RADIUS);
    }
}
