package com.dannykim.dtvanillabackport.block;

import com.blackgear.vanillabackport.common.level.blocks.CactusFlowerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class DynamicCactusFlowerBlock extends CactusFlowerBlock {
    public static final BooleanProperty PILLAR = BooleanProperty.create("pillar");
    public static final BooleanProperty PIPE = BooleanProperty.create("pipe");
    private static final ResourceLocation DYNAMIC_CACTUS_BRANCH =
            new ResourceLocation("dynamictreesplus", "cactus_branch");
    private static final ResourceLocation CACTUS_FLOWER =
            new ResourceLocation("minecraft", "cactus_flower");
    private static final VoxelShape DEFAULT_SHAPE = Block.box(1.0D, -4.0D, 1.0D, 15.0D, 8.0D, 15.0D);
    private static final VoxelShape PILLAR_SHAPE = Block.box(1.0D, -3.0D, 1.0D, 15.0D, 9.0D, 15.0D);
    private static final VoxelShape PIPE_SHAPE = Block.box(1.0D, -4.0D, 1.0D, 15.0D, 8.0D, 15.0D);

    public DynamicCactusFlowerBlock(final BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(PILLAR, false).setValue(PIPE, false));
    }

    @Override
    protected boolean mayPlaceOn(final BlockState state, final BlockGetter level, final BlockPos pos) {
        return DYNAMIC_CACTUS_BRANCH.equals(BuiltInRegistries.BLOCK.getKey(state.getBlock()))
                || super.mayPlaceOn(state, level, pos);
    }

    @Override
    public VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos,
                               final CollisionContext context) {
        return state.getValue(PIPE) ? PIPE_SHAPE : state.getValue(PILLAR) ? PILLAR_SHAPE : DEFAULT_SHAPE;
    }

    @Override
    public ItemStack getCloneItemStack(final BlockGetter level, final BlockPos pos, final BlockState state) {
        return new ItemStack(BuiltInRegistries.ITEM.get(CACTUS_FLOWER));
    }

    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PILLAR, PIPE);
    }
}
