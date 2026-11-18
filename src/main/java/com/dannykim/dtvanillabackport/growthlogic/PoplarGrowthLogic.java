package com.dannykim.dtvanillabackport.growthlogic;

import com.dtteam.dynamictrees.systems.growthlogic.GrowthLogicKit;
import com.dtteam.dynamictrees.systems.growthlogic.GrowthLogicKitConfiguration;
import com.dtteam.dynamictrees.systems.growthlogic.context.DirectionManipulationContext;
import com.dtteam.dynamictrees.systems.growthlogic.context.PositionalSpeciesContext;
import com.dtteam.dynamictrees.systems.growthlogic.context.DirectionSelectionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public final class PoplarGrowthLogic extends GrowthLogicKit {
    public PoplarGrowthLogic(ResourceLocation name) {
        super(name);
    }

    // VB places its lateral logs at one level, four blocks below the trunk tip.
    private static int trunkHeight(BlockPos root) {
        return 7 + Math.floorMod(Mth.getSeed(root), 5);
    }

    @Override
    public float getEnergy(GrowthLogicKitConfiguration config, PositionalSpeciesContext context) {
        // A signal spends its first step entering the trunk base.
        return trunkHeight(context.pos()) + 1;
    }

    @Override
    public Direction selectNewDirection(GrowthLogicKitConfiguration config, DirectionSelectionContext context) {
        Direction direction = super.selectNewDirection(config, context);
        if (context.signal().isInTrunk() && direction.getAxis().isHorizontal()) {
            // Each arm has its own short growth budget to support the broad, uneven crown.
            long armSeed = Mth.getSeed(context.signal().rootPos.relative(direction));
            context.signal().energy = Math.min(context.signal().energy, 1 + Math.floorMod(armSeed, 5));
        }
        return direction;
    }

    @Override
    public int[] populateDirectionProbabilityMap(GrowthLogicKitConfiguration config, DirectionManipulationContext context) {
        int[] weights = super.populateDirectionProbabilityMap(config, context);
        BlockPos root = context.signal().rootPos;
        BlockPos pos = context.pos();
        int crownBase = trunkHeight(root) - 4;
        int height = pos.getY() - root.getY();
        int crownSides = 1 + Math.floorMod(Mth.getSeed(root) >> 8, 15);
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
        weights[Direction.DOWN.ordinal()] = 0;
        return weights;
    }
}
