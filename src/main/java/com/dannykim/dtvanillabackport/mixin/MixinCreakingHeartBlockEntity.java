package com.dannykim.dtvanillabackport.mixin;

import com.blackgear.vanillabackport.common.level.blockentities.CreakingHeartBlockEntity;
import com.blackgear.vanillabackport.common.registries.ModBlocks;
import com.blackgear.vanillabackport.core.data.tags.ModBlockTags;
import com.dannykim.dtvanillabackport.block.CreakingHeartBranchBlockEntity;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

@Mixin(CreakingHeartBlockEntity.class)
public abstract class MixinCreakingHeartBlockEntity {
    @Inject(method = "spreadResin", at = @At("RETURN"), cancellable = true, remap = false)
    private void dtvanillabackport$spreadResinOnDynamicBranches(
            final CallbackInfoReturnable<Optional<BlockPos>> cir
    ) {
        if (cir.getReturnValue().isPresent() || !((Object) this instanceof CreakingHeartBranchBlockEntity heart)) {
            return;
        }

        final Level level = heart.getLevel();
        if (level == null) {
            return;
        }

        final Queue<Node> queue = new ArrayDeque<>();
        final Set<BlockPos> visited = new HashSet<>();
        queue.add(new Node(heart.getBlockPos(), 0));

        while (!queue.isEmpty() && visited.size() < 64) {
            final Node node = queue.remove();
            if (!visited.add(node.pos()) || !level.getBlockState(node.pos()).is(ModBlockTags.CREAKING_HEART_HOLDERS)) {
                continue;
            }

            for (Direction direction : Util.shuffledCopy(Direction.values(), level.getRandom())) {
                final BlockPos targetPos = node.pos().relative(direction);
                final BlockState targetState = level.getBlockState(targetPos);
                BlockState resinState = null;

                if (targetState.isAir()) {
                    resinState = ModBlocks.RESIN_CLUMP.get().defaultBlockState();
                } else if (targetState.getFluidState().is(Fluids.WATER)
                        && targetState.getFluidState().isSource()) {
                    resinState = ModBlocks.RESIN_CLUMP.get().defaultBlockState()
                            .setValue(BlockStateProperties.WATERLOGGED, true);
                }

                if (resinState != null) {
                    level.setBlock(targetPos, resinState.setValue(
                            MultifaceBlock.getFaceProperty(direction.getOpposite()), true
                    ), 3);
                    cir.setReturnValue(Optional.of(targetPos));
                    return;
                }

                if (node.depth() < 2 && targetState.is(ModBlockTags.CREAKING_HEART_HOLDERS)) {
                    queue.add(new Node(targetPos, node.depth() + 1));
                }
            }
        }
    }

    private record Node(BlockPos pos, int depth) {
    }
}