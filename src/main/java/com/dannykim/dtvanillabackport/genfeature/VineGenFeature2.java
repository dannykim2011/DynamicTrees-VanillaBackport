package com.dannykim.dtvanillabackport.genfeature;

import com.ferreusveritas.dynamictrees.systems.genfeature.GenFeatureConfiguration;
import com.ferreusveritas.dynamictrees.systems.genfeature.VinesGenFeature;
import com.ferreusveritas.dynamictrees.tree.species.Species;
import com.ferreusveritas.dynamictrees.util.CoordUtils;
import com.ferreusveritas.dynamictrees.util.SafeChunkBounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/** Backport of the DT 26.1 vertical-vine tip handling for Forge 1.20.1. */
public class VineGenFeature2 extends VinesGenFeature {
    public VineGenFeature2(final ResourceLocation registryName) {
        super(registryName);
    }

    @Override
    protected void addVerticalVines(
            final GenFeatureConfiguration configuration,
            final LevelAccessor level,
            final Species species,
            final BlockPos rootPos,
            final BlockPos branchPos,
            final SafeChunkBounds safeBounds,
            final boolean worldGen
    ) {
        BlockPos vinePos = CoordUtils.getRayTraceFruitPos(level, species, rootPos, branchPos, safeBounds);
        if (configuration.get(VINE_TYPE) == VineType.FLOOR) {
            vinePos = this.findGround(level, vinePos);
        }
        if (vinePos == BlockPos.ZERO) {
            return;
        }

        this.placeVines(
                level,
                vinePos,
                getPlantState(configuration),
                configuration.get(MAX_LENGTH),
                getTipState(configuration, worldGen),
                configuration.get(VINE_TYPE),
                worldGen
        );
    }

    private static BlockState getPlantState(final GenFeatureConfiguration configuration) {
        return setTip(configuration.get(BLOCK).defaultBlockState(), false);
    }

    @Nullable
    private static BlockState getTipState(final GenFeatureConfiguration configuration, final boolean worldGen) {
        final Optional<Block> tipBlock = configuration.getAsOptional(TIP_BLOCK);
        if (tipBlock.isPresent()) {
            BlockState state = tipBlock.get().defaultBlockState();
            if (state.hasProperty(BlockStateProperties.AGE_25)) {
                state = state.setValue(BlockStateProperties.AGE_25, worldGen ? 25 : 0);
            }
            return state;
        }

        final BlockState plantState = getPlantState(configuration);
        return findTipProperty(plantState)
                .map(property -> plantState.setValue(property, true))
                .orElse(null);
    }

    private static BlockState setTip(final BlockState state, final boolean tip) {
        return findTipProperty(state).map(property -> state.setValue(property, tip)).orElse(state);
    }

    private static Optional<BooleanProperty> findTipProperty(final BlockState state) {
        return state.getProperties().stream()
                .filter(BooleanProperty.class::isInstance)
                .map(BooleanProperty.class::cast)
                .filter(property -> property.getName().equals("tip"))
                .findFirst();
    }

    private BlockPos findGround(final LevelAccessor level, final BlockPos startPos) {
        final BlockPos.MutableBlockPos pos = startPos.mutable();
        while (pos.getY() > level.getMinBuildHeight()) {
            pos.move(Direction.DOWN);
            if (!level.isEmptyBlock(pos)) {
                return pos.above().immutable();
            }
        }
        return BlockPos.ZERO;
    }
}
