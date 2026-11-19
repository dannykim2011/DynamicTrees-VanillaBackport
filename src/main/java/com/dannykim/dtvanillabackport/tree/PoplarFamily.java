package com.dannykim.dtvanillabackport.tree;

import com.ferreusveritas.dynamictrees.api.registry.TypedRegistry;
import com.ferreusveritas.dynamictrees.block.branch.BranchBlock;
import com.ferreusveritas.dynamictrees.tree.family.Family;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public final class PoplarFamily extends Family {
    public static final TypedRegistry.EntryType<Family> TYPE = TypedRegistry.newType(PoplarFamily::new);

    public PoplarFamily(ResourceLocation name) {
        super(name);
    }

    @Override
    public int getRadiusForCellKit(BlockGetter level, BlockPos pos, BlockState state, Direction dir, BranchBlock branch) {
        // A straight section below the crown should not sustain a separate collar of leaves.
        if (level.getBlockState(pos.above()).is(branch)) {
            for (Direction side : Direction.Plane.HORIZONTAL) {
                if (level.getBlockState(pos.relative(side)).is(branch)) {
                    return super.getRadiusForCellKit(level, pos, state, dir, branch);
                }
            }
            return 0;
        }
        return super.getRadiusForCellKit(level, pos, state, dir, branch);
    }
}
