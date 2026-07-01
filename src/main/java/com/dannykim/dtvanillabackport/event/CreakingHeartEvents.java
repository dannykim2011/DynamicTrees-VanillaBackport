package com.dannykim.dtvanillabackport.event;

import com.dannykim.dtvanillabackport.DynamicTreesVanillaBackport;
import com.dannykim.dtvanillabackport.block.CreakingHeartBranchBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = DynamicTreesVanillaBackport.MOD_ID)
public final class CreakingHeartEvents {
    private CreakingHeartEvents() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void onLeftClickBlock(final PlayerInteractEvent.LeftClickBlock event) {
        final Level level = event.getLevel();
        final BlockPos pos = findHiddenHeart(level, event.getPos());
        if (pos == null) {
            return;
        }
        final BlockState state = level.getBlockState(pos);
        if (level.isClientSide) {
            level.setBlock(pos, state.setValue(CreakingHeartBranchBlock.HIDDEN, false), 3);
            event.setCanceled(false);
            return;
        }
        event.setCanceled(true);
        level.levelEvent(null, 2001, pos, Block.getId(state));
        level.setBlock(pos, state.setValue(CreakingHeartBranchBlock.HIDDEN, false), 3);
    }

    private static BlockPos findHiddenHeart(final Level level, final BlockPos clickedPos) {
        final BlockState clickedState = level.getBlockState(clickedPos);
        if (isHiddenHeart(clickedState)) {
            return clickedPos;
        }
        for (final Direction direction : Direction.values()) {
            final BlockPos adjacentPos = clickedPos.relative(direction);
            if (isHiddenHeart(level.getBlockState(adjacentPos))) {
                return adjacentPos;
            }
        }
        return null;
    }

    private static boolean isHiddenHeart(final BlockState state) {
        return state.getBlock() instanceof CreakingHeartBranchBlock
                && state.getValue(CreakingHeartBranchBlock.HIDDEN);
    }
}
