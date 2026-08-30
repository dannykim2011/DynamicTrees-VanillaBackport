package com.dannykim.dtvanillabackport.block;

import com.dannykim.dtvanillabackport.tree.CreakingHeartFamily;
import com.blackgear.vanillabackport.common.registries.ModBlocks;
import com.ferreusveritas.dynamictrees.block.branch.ThickBranchBlock;
import com.ferreusveritas.dynamictrees.api.TreeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ResinBranchBlock extends ThickBranchBlock {
    public ResinBranchBlock(final ResourceLocation name, final Properties properties) {
        super(name, properties);
    }

    public void removeResin(final BlockState state, final Level level, final BlockPos pos,
                            final @Nullable Player player) {
        if (level.isClientSide) {
            return;
        }
        if (!(getFamily() instanceof CreakingHeartFamily family)
                || family.getBranch().isEmpty()) {
            return;
        }

        final int radius = TreeHelper.getRadius(level, pos);
        family.getBranch().get().setRadius(level, pos, radius, null, 3);

        final Block resin = ModBlocks.RESIN_CLUMP.get();
        final ItemStack resinStack = getResinStack(level.getRandom(), resin, radius);
        if (player == null || !player.isCreative()) {
            popResource(level, pos, resinStack);
        }
        level.playSound(null, pos, SoundEvents.WOOD_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    private static ItemStack getResinStack(final RandomSource random, final Block resin, final int radius) {
        final int count = Math.max(1, Math.round(random.nextIntBetweenInclusive(2, 3) * (radius / 8.0F)));
        return new ItemStack(resin.asItem(), count);
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
    public void stripBranch(final BlockState state, final LevelAccessor level,
                            final BlockPos pos, final int radius) {
        if (level instanceof Level realLevel) {
            removeResin(state, realLevel, pos, null);
        }
        super.stripBranch(state, level, pos, radius);
    }

    @Override
    public boolean onDestroyedByPlayer(final BlockState state, final Level level,
                                       final BlockPos pos, final Player player,
                                       final boolean willHarvest, final FluidState fluid) {
        removeResin(state, level, pos, player);
        return false;
    }

    @Override
    public Optional<Block> getPrimitiveLog() {
        return getFamily() instanceof CreakingHeartFamily family
                ? family.getPrimitiveLog()
                : super.getPrimitiveLog();
    }
}
