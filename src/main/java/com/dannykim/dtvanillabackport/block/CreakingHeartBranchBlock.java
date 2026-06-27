package com.dannykim.dtvanillabackport.block;

import com.blackgear.vanillabackport.common.level.blockentities.CreakingHeartBlockEntity;
import com.blackgear.vanillabackport.common.level.blocks.CreakingHeartBlock;
import com.blackgear.vanillabackport.common.level.blocks.states.CreakingHeartState;
import com.ferreusveritas.dynamictrees.block.branch.BasicBranchBlock;
import com.ferreusveritas.dynamictrees.block.branch.BranchBlock;
import com.dannykim.dtvanillabackport.registry.DTVBRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CreakingHeartBranchBlock extends BasicBranchBlock implements EntityBlock {
    public static final EnumProperty<CreakingHeartState> STATE = CreakingHeartBlock.STATE;
    public static final BooleanProperty HIDDEN = BooleanProperty.create("hidden");

    public CreakingHeartBranchBlock(final ResourceLocation name, final Properties properties) {
        super(name, properties);
    }

    @Override
    public BlockState[] createBranchStates(final IntegerProperty radiusProperty, final int maxRadius) {
        this.registerDefaultState(this.defaultBlockState()
                .setValue(STATE, CreakingHeartState.DORMANT)
                .setValue(HIDDEN, true));
        return super.createBranchStates(radiusProperty, maxRadius);
    }

    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STATE, HIDDEN);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public int setRadius(final LevelAccessor level, final BlockPos pos, final int radius, final Direction originDir, final int flags) {
        final BlockState previousState = level.getBlockState(pos);
        final CreakingHeartState previousHeartState = previousState.hasProperty(STATE)
                ? previousState.getValue(STATE)
                : CreakingHeartState.DORMANT;
        final boolean previousHidden = !previousState.hasProperty(HIDDEN) || previousState.getValue(HIDDEN);
        final int result = super.setRadius(level, pos, radius, originDir, flags);
        final BlockState placedState = level.getBlockState(pos);
        if (placedState.getBlock() == this) {
            level.setBlock(pos, placedState
                    .setValue(STATE, previousHeartState == CreakingHeartState.UPROOTED
                            ? CreakingHeartState.DORMANT
                            : previousHeartState)
                    .setValue(HIDDEN, previousHidden), flags);
        }
        return result;
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