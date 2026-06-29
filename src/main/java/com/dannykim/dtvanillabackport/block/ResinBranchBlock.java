package com.dannykim.dtvanillabackport.block;

import com.dannykim.dtvanillabackport.tree.CreakingHeartFamily;
import com.ferreusveritas.dynamictrees.block.branch.ThickBranchBlock;
import com.ferreusveritas.dynamictrees.api.TreeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Optional;

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
                    new ResourceLocation("minecraft", "resin_clump")
            );
            player.addItem(new ItemStack(resin.asItem(), Math.max(1, radius / 4)));
        }
        level.playSound(null, pos, SoundEvents.WOOD_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    @Override
    public InteractionResult use(final BlockState state, final Level level, final BlockPos pos,
                                 final Player player, final InteractionHand hand,
                                 final BlockHitResult hitResult) {
        if (player.getItemInHand(hand).isEmpty()) {
            removeResin(state, level, pos, player);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.use(state, level, pos, player, hand, hitResult);
    }

    @Override
    public Optional<Block> getPrimitiveLog() {
        return getFamily() instanceof CreakingHeartFamily family
                ? family.getPrimitiveLog()
                : super.getPrimitiveLog();
    }
}
