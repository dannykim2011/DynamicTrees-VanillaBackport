package com.dannykim.dtvanillabackport.growthlogic;

import com.ferreusveritas.dynamictrees.growthlogic.GrowthLogicKit;
import com.ferreusveritas.dynamictrees.growthlogic.GrowthLogicKitConfiguration;
import com.ferreusveritas.dynamictrees.growthlogic.context.DirectionManipulationContext;
import com.ferreusveritas.dynamictrees.growthlogic.context.PositionalSpeciesContext;
import com.ferreusveritas.dynamictrees.growthlogic.context.DirectionSelectionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public final class PoplarGrowthLogic extends GrowthLogicKit {
    public PoplarGrowthLogic(ResourceLocation name) {
        super(name);
    }

    // Keep the original lower crown elevation and end the leader one block above it.
    private static int crownBaseHeight(BlockPos root) {
        return 3 + Math.floorMod(Mth.getSeed(root), 5);
    }

    // Each row contains every direction mask for the same number of arms.
    private static final int[][] ARM_MASKS = {
            {1, 2, 4, 8}, {3, 5, 6, 9, 10, 12}, {7, 11, 13, 14}, {15}
    };

    @Override
    public float getEnergy(GrowthLogicKitConfiguration config, PositionalSpeciesContext context) {
        // A signal spends its first step entering the trunk base.
        return crownBaseHeight(context.pos()) + 2;
    }

    @Override
    public Direction selectNewDirection(GrowthLogicKitConfiguration config, DirectionSelectionContext context) {
        Direction direction = super.selectNewDirection(config, context);
        if (context.signal().isInTrunk() && direction.getAxis().isHorizontal()) {
            // Each arm has its own short growth budget to support the broad, uneven crown.
            long armSeed = Mth.getSeed(context.signal().rootPos.relative(direction));
            context.signal().energy = 2 + Math.floorMod(armSeed, 4);
        }
        // DT defaults to UP when no direction has weight. End a blocked signal in
        // its incoming branch so this fallback cannot start a second leader.
        if (direction == Direction.UP && context.pos().getY() >=
                context.signal().rootPos.getY() + crownBaseHeight(context.signal().rootPos) + 1) {
            context.signal().energy = 0;
            return context.signal().dir.getOpposite();
        }
        return direction;
    }

    @Override
    public int[] populateDirectionProbabilityMap(GrowthLogicKitConfiguration config, DirectionManipulationContext context) {
        int[] weights = super.populateDirectionProbabilityMap(config, context);
        BlockPos root = context.signal().rootPos;
        BlockPos pos = context.pos();
        int crownBase = crownBaseHeight(root);
        int height = pos.getY() - root.getY();
        long seed = Mth.getSeed(root);
        int[] masks = ARM_MASKS[(int) (seed >>> 8) & 3];
        int crownSides = masks[Math.floorMod(seed >> 16, masks.length)];
        for (Direction side : Direction.Plane.HORIZONTAL) {
            if (context.signal().isInTrunk()) {
                if (height != crownBase || (crownSides & (1 << side.get2DDataValue())) == 0) {
                    weights[side.ordinal()] = 0;
                }
            } else {
                int dx = pos.getX() - root.getX();
                int dz = pos.getZ() - root.getZ();
                int reach = Math.abs(dx) + Math.abs(dz);
                int nextReach = Math.abs(dx + side.getStepX()) + Math.abs(dz + side.getStepZ());
                // Only grow away from the trunk, across the lower half of the crown.
                if (height > crownBase + 1 || nextReach <= reach || nextReach > 3) {
                    weights[side.ordinal()] = 0;
                }
            }
        }
        if (height >= crownBase + 1) weights[Direction.UP.ordinal()] = 0;
        weights[Direction.DOWN.ordinal()] = 0;
        return weights;
    }
}
