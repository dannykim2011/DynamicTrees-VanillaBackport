package com.dannykim.dtvanillabackport.block;

import com.blackgear.vanillabackport.common.level.blockentities.CreakingHeartBlockEntity;
import com.blackgear.vanillabackport.common.registries.ModBlocks;
import com.dannykim.dtvanillabackport.registry.DTVBRegistries;
import com.dannykim.dtvanillabackport.tree.CreakingHeartFamily;
import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.block.branch.BranchBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class CreakingHeartBranchBlockEntity extends CreakingHeartBlockEntity {
    public CreakingHeartBranchBlockEntity(final BlockPos pos, final BlockState state) {
        super(pos, ModBlocks.CREAKING_HEART.get().defaultBlockState());
        ObfuscationReflectionHelper.setPrivateValue(
                BlockEntity.class, this, DTVBRegistries.CREAKING_HEART.get(), "f_58855_"
        );
        ObfuscationReflectionHelper.setPrivateValue(
                BlockEntity.class, this, state, "f_58856_"
        );
    }

    public Optional<BlockPos> spreadResinOnBranches(final ServerLevel level) {
        final BlockState heartState = level.getBlockState(this.worldPosition);
        if (!(TreeHelper.getBranch(heartState) instanceof CreakingHeartBranchBlock heart)
                || !(heart.getFamily() instanceof CreakingHeartFamily family)
                || family.getBranch().isEmpty()
                || family.getResinBranch().isEmpty()) {
            return Optional.empty();
        }

        final BranchBlock standardBranch = family.getBranch().get();
        final BranchBlock resinBranch = family.getResinBranch().get();
        final ArrayDeque<SearchNode> queue = new ArrayDeque<>();
        final Set<BlockPos> visited = new HashSet<>();
        queue.add(new SearchNode(this.worldPosition, 0));
        visited.add(this.worldPosition);

        while (!queue.isEmpty() && visited.size() <= 64) {
            final SearchNode node = queue.removeFirst();
            final BlockPos pos = node.pos();
            final BlockState branchState = level.getBlockState(pos);
            if (TreeHelper.getBranch(branchState) == standardBranch) {
                resinBranch.setRadius(level, pos, TreeHelper.getRadius(level, pos), null, 3);
                if (!heartState.getValue(CreakingHeartBranchBlock.RESIN)) {
                    level.setBlock(this.worldPosition,
                            heartState.setValue(CreakingHeartBranchBlock.RESIN, true), 3);
                }
                return Optional.of(pos.immutable());
            }
            if (node.depth() >= 2) {
                continue;
            }
            for (final Direction direction : Direction.values()) {
                final BlockPos neighbour = pos.relative(direction);
                if (visited.add(neighbour) && TreeHelper.isBranch(level.getBlockState(neighbour))) {
                    queue.addLast(new SearchNode(neighbour, node.depth() + 1));
                }
            }
        }
        return Optional.empty();
    }

    private record SearchNode(BlockPos pos, int depth) {}
}
