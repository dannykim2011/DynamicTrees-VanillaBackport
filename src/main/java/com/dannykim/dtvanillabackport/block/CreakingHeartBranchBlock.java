package com.dannykim.dtvanillabackport.block;

import com.blackgear.vanillabackport.common.level.blockentities.CreakingHeartBlockEntity;
import com.blackgear.vanillabackport.common.level.blocks.CreakingHeartBlock;
import com.blackgear.vanillabackport.common.level.blocks.states.CreakingHeartState;
import com.ferreusveritas.dynamictrees.block.branch.BranchBlock;
import com.ferreusveritas.dynamictrees.block.branch.ThickBranchBlock;
import com.dannykim.dtvanillabackport.registry.DTVBRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CreakingHeartBranchBlock extends ThickBranchBlock implements EntityBlock {
    public static final EnumProperty<CreakingHeartState> STATE = CreakingHeartBlock.STATE;
    public static final BooleanProperty HIDDEN = BooleanProperty.create("hidden");
    public static final BooleanProperty RESIN = BooleanProperty.create("resin");

    public CreakingHeartBranchBlock(final ResourceLocation name, final Properties properties) {
        super(name, properties);
    }

    @Override
    public BlockState[] createBranchStates(final IntegerProperty radiusProperty, final int maxRadius) {
        this.registerDefaultState(this.defaultBlockState()
                .setValue(STATE, CreakingHeartState.DORMANT)
                .setValue(HIDDEN, true)
                .setValue(RESIN, false));
        return super.createBranchStates(radiusProperty, maxRadius);
    }

    @Override
    public void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STATE, HIDDEN, RESIN);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public int setRadius(final LevelAccessor level, final BlockPos pos, final int radius, final Direction originDir, final int flags) {
        final BlockState previousState = level.getBlockState(pos);
        final CreakingHeartState previousHeartState = previousState.hasProperty(STATE)
                ? previousState.getValue(STATE)
                : CreakingHeartState.DORMANT;
        final boolean previousHidden = !previousState.hasProperty(HIDDEN) || previousState.getValue(HIDDEN);
        final boolean previousResin = previousState.hasProperty(RESIN) && previousState.getValue(RESIN);
        final int result = super.setRadius(level, pos, radius, originDir, flags);
        final BlockState placedState = level.getBlockState(pos);
        if (placedState.getBlock() == this) {
            level.setBlock(pos, placedState
                    .setValue(STATE, previousHeartState == CreakingHeartState.UPROOTED
                            ? CreakingHeartState.DORMANT
                            : previousHeartState)
                    .setValue(HIDDEN, previousHidden)
                    .setValue(RESIN, previousResin), flags);
        }
        level.scheduleTick(pos, this, 1);
        return result;
    }

    @Override
    public BlockState updateShape(
            final BlockState state,
            final Direction direction,
            final BlockState neighbourState,
            final LevelAccessor level,
            final BlockPos pos,
            final BlockPos neighbourPos
    ) {
        level.scheduleTick(pos, this, 1);
        return super.updateShape(state, direction, neighbourState, level, pos, neighbourPos);
    }

    @Override
    public void tick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {
        final BlockState updatedState;
        if (!hasRequiredLogs(state, level, pos)) {
            if (level.getBlockEntity(pos) instanceof CreakingHeartBlockEntity heart) {
                heart.removeProtector(null);
            }
            updatedState = state.setValue(STATE, CreakingHeartState.UPROOTED);
        } else {
            updatedState = state.setValue(
                    STATE,
                    CreakingHeartBlock.isNaturalNight(level)
                            ? CreakingHeartState.AWAKE
                            : CreakingHeartState.DORMANT
            );
        }
        if (updatedState != state) {
            level.setBlock(pos, updatedState, 3);
        }
        level.scheduleTick(pos, this, 20);
    }
    public static boolean hasRequiredLogs(final BlockState state, final LevelReader level, final BlockPos pos) {
        int count = 0;
        for (Direction direction : Direction.values()) {
            if (level.getBlockState(pos.relative(direction)).getBlock() instanceof BranchBlock) {
                count++;
            }
            if (count >= 2) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canBeStripped(final BlockState state, final Level level, final BlockPos pos,
                                 final Player player, final ItemStack heldItem) {
        return state.getValue(HIDDEN)
                && super.canBeStripped(state, level, pos, player, heldItem);
    }

    @Override
    public void stripBranch(final BlockState state, final LevelAccessor level,
                            final BlockPos pos, final int radius) {
        level.setBlock(pos, state.setValue(HIDDEN, false), 3);
    }

    @Override
    public boolean onDestroyedByPlayer(final BlockState state, final Level level,
                                       final BlockPos pos, final Player player,
                                       final boolean willHarvest, final FluidState fluid) {
        if (state.getValue(HIDDEN)) {
            if (!level.isClientSide) {
                level.levelEvent(null, 2001, pos, Block.getId(state));
                level.setBlock(pos, state.setValue(HIDDEN, false), 3);
            }
            return false;
        }
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
        return new CreakingHeartBranchBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(
            final Level level,
            final BlockState state,
            final BlockEntityType<T> type
    ) {
        if (level.isClientSide || state.getValue(STATE) == CreakingHeartState.UPROOTED) {
            return null;
        }
        return createTickerHelper(type, DTVBRegistries.CREAKING_HEART.get(), CreakingHeartBlockEntity::serverTick);
    }

    @SuppressWarnings("unchecked")
    private static <E extends BlockEntity, A extends BlockEntity> @Nullable BlockEntityTicker<A> createTickerHelper(
            final BlockEntityType<A> actual,
            final BlockEntityType<E> expected,
            final BlockEntityTicker<? super E> ticker
    ) {
        return expected == actual ? (BlockEntityTicker<A>) ticker : null;
    }

    @Override
    public void futureBreak(final BlockState state, final Level level, final BlockPos pos, final LivingEntity entity) {
        if (entity instanceof Player player
                && level.getBlockEntity(pos) instanceof CreakingHeartBlockEntity heart) {
            heart.removeProtector(player.damageSources().playerAttack(player));
        }
        super.futureBreak(state, level, pos, entity);
    }

    @Override
    public Optional<Block> getPrimitiveLog() {
        return Optional.of(BuiltInRegistries.BLOCK.get(
                new ResourceLocation("minecraft", "creaking_heart")
        ));
    }
}
