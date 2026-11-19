package com.dannykim.dtvanillabackport.genfeature;

import com.blackgear.vanillabackport.common.level.blocks.ShelfMushroomBlock;
import com.dannykim.dtvanillabackport.block.PoplarShelfMushroomBlock;
import com.dannykim.dtvanillabackport.registry.DTVBRegistries;
import com.ferreusveritas.dynamictrees.systems.genfeature.GenFeature;
import com.ferreusveritas.dynamictrees.systems.genfeature.GenFeatureConfiguration;
import com.ferreusveritas.dynamictrees.systems.genfeature.context.PostGenerationContext;
import com.ferreusveritas.dynamictrees.systems.genfeature.context.PostGrowContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;

public final class PoplarShelfMushroomGenFeature extends GenFeature {
    public PoplarShelfMushroomGenFeature(ResourceLocation name) {
        super(name);
    }

    @Override
    protected void registerProperties() {
        register(PLACE_CHANCE);
    }

    @Override
    public GenFeatureConfiguration createDefaultConfiguration() {
        return super.createDefaultConfiguration().with(PLACE_CHANCE, 0.4F);
    }

    @Override
    protected boolean postGenerate(GenFeatureConfiguration config, PostGenerationContext context) {
        return decorate(config, context.level(), context.pos(), true);
    }

    @Override
    protected boolean postGrow(GenFeatureConfiguration config, PostGrowContext context) {
        return decorate(config, context.level(), context.pos(), context.fertility() > 0);
    }

    private boolean decorate(GenFeatureConfiguration config, LevelAccessor level, BlockPos root, boolean allowPlacement) {
        // Keep the same eligible faces as the tree grows, so repeated pulses cannot cover every side.
        long seed = Mth.getSeed(root) ^ (level instanceof WorldGenLevel world ? world.getSeed() : 0L);
        RandomSource layout = RandomSource.create(seed);
        if (layout.nextFloat() >= config.get(PLACE_CHANCE)) return false;
        Direction first = Direction.Plane.HORIZONTAL.getRandomDirection(layout);
        boolean changed = false;
        for (int height = 2; height <= 5; height++) {
            BlockPos support = root.above(height);
            int radius = PoplarShelfMushroomBlock.supportRadius(level.getBlockState(support));
            boolean placed = hasShelf(level, support, first) || hasShelf(level, support, first.getClockWise());
            for (int side = 0; side < 2; side++) {
                Direction facing = side == 0 ? first : first.getClockWise();
                boolean candidate = layout.nextFloat() < 0.25F;
                int age = layout.nextInt(2);
                BlockPos pos = support.relative(facing);
                BlockState state = level.getBlockState(pos);
                if (state.is(DTVBRegistries.POPLAR_SHELF_MUSHROOM.get()) && state.getValue(ShelfMushroomBlock.FACING) == facing) {
                    // DT can thicken a branch with client-only update flags.
                    if (radius >= PoplarShelfMushroomBlock.MIN_RADIUS && radius != state.getValue(PoplarShelfMushroomBlock.SUPPORT_RADIUS)) {
                        changed |= level.setBlock(pos, state.setValue(PoplarShelfMushroomBlock.SUPPORT_RADIUS, radius), 2);
                    }
                    placed = true;
                } else if (candidate && !placed && allowPlacement && radius >= PoplarShelfMushroomBlock.MIN_RADIUS
                        && state.canBeReplaced() && !(level.getBlockState(pos.below()).getBlock() instanceof ShelfMushroomBlock)
                        && isDry(level, pos)) {
                    BlockState mushroom = DTVBRegistries.POPLAR_SHELF_MUSHROOM.get().defaultBlockState()
                            .setValue(ShelfMushroomBlock.FACING, facing).setValue(ShelfMushroomBlock.AGE, age)
                            .setValue(PoplarShelfMushroomBlock.SUPPORT_RADIUS, radius);
                    placed = level.setBlock(pos, mushroom, 3);
                    changed |= placed;
                }
            }
        }
        return changed;
    }

    private static boolean hasShelf(LevelAccessor level, BlockPos support, Direction facing) {
        BlockState state = level.getBlockState(support.relative(facing));
        return state.getBlock() instanceof ShelfMushroomBlock && state.getValue(ShelfMushroomBlock.FACING) == facing;
    }

    private static boolean isDry(LevelAccessor level, BlockPos pos) {
        if (!level.getFluidState(pos).isEmpty()) return false;
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (level.getFluidState(pos.relative(direction)).is(FluidTags.WATER)) return false;
        }
        return true;
    }
}
