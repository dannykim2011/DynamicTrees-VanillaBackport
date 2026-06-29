package com.dannykim.dtvanillabackport.block;

import com.dtteam.dynamictrees.block.branch.ThickBranchBlock;
import com.dtteam.dynamictrees.tree.TreeHelper;
import com.dannykim.dtvanillabackport.tree.CreakingHeartFamily;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Optional;

/**
 * A normal Dynamic Trees branch with a resin texture layer.  Keeping resin in
 * the branch block makes the overlay follow every branch radius instead of
 * sitting on the outer face of a full-sized vanilla resin-clump block.
 */
public class ResinBranchBlock extends ThickBranchBlock {
    public ResinBranchBlock(final ResourceLocation name, final Properties properties) {
        super(name, properties);
    }

    public void removeResin(final BlockState state, final Level level, final BlockPos pos,
                            final Player player) {
        if (!(getFamily() instanceof CreakingHeartFamily family)
                || family.getBranch().isEmpty()) {
            return;
        }

        final int radius = TreeHelper.getRadius(level, pos);
        family.getBranch().get().setRadius(level, pos, radius, null, 3);

        if (!player.isCreative()) {
            final Block resin = BuiltInRegistries.BLOCK.get(
                    ResourceLocation.fromNamespaceAndPath("minecraft", "resin_clump")
            );
            player.addItem(new ItemStack(resin.asItem(), Math.max(1, radius / 4)));
        }
        level.playSound(null, pos, SoundEvents.WOOD_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    @Override
    protected InteractionResult useWithoutItem(final BlockState state, final Level level,
                                                final BlockPos pos, final Player player,
                                                final BlockHitResult hitResult) {
        removeResin(state, level, pos, player);
        return InteractionResult.SUCCESS;
    }

    @Override
    protected ItemInteractionResult useItemOn(final ItemStack stack, final BlockState state,
                                              final Level level, final BlockPos pos,
                                              final Player player, final InteractionHand hand,
                                              final BlockHitResult hitResult) {
        if (stack.isEmpty()) {
            removeResin(state, level, pos, player);
            return ItemInteractionResult.SUCCESS;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public Optional<Block> getPrimitiveLog() {
        return getFamily() instanceof CreakingHeartFamily family
                ? family.getPrimitiveLog()
                : super.getPrimitiveLog();
    }
}
