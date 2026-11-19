package com.dannykim.dtvanillabackport.block;

import com.blackgear.vanillabackport.common.level.blocks.ShelfMushroomBlock;
import com.blackgear.vanillabackport.common.registries.blocks.ModBlocks;
import com.dannykim.dtvanillabackport.DynamicTreesVanillaBackport;
import com.ferreusveritas.dynamictrees.block.branch.BranchBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class PoplarShelfMushroomBlock extends ShelfMushroomBlock {
    public static final int MIN_RADIUS = 3;
    public static final IntegerProperty SUPPORT_RADIUS = IntegerProperty.create("support_radius", MIN_RADIUS, 8);
    private static final ResourceLocation POPLAR = DynamicTreesVanillaBackport.location("poplar");

    public PoplarShelfMushroomBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(SUPPORT_RADIUS, 8));
    }

    public static int supportRadius(BlockState support) {
        if (support.getBlock() instanceof BranchBlock branch && POPLAR.equals(branch.getFamily().getRegistryName())) {
            return Math.min(8, branch.getRadius(support));
        }
        return 0;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(SUPPORT_RADIUS);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return supportRadius(level.getBlockState(pos.relative(state.getValue(FACING).getOpposite()))) >= MIN_RADIUS;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbour, LevelAccessor level,
                                  BlockPos pos, BlockPos neighbourPos) {
        if (direction == state.getValue(FACING).getOpposite()) {
            int radius = supportRadius(neighbour);
            return radius < MIN_RADIUS ? Blocks.AIR.defaultBlockState() : state.setValue(SUPPORT_RADIUS, radius);
        }
        return super.updateShape(state, direction, neighbour, level, pos, neighbourPos);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape original = super.getShape(state, level, pos, context);
        int radius = state.getValue(SUPPORT_RADIUS);
        Direction facing = state.getValue(FACING);
        // Scale about the attachment face, then move it in to the branch surface.
        double scale = (12.0 + radius) / 20.0;
        // The two VB caps are 10 and 14 pixels wide. Match either cap to the trunk diameter.
        double widthScale = 2.0 * radius / (10 + 4 * state.getValue(AGE));
        double scaleX = facing.getStepX() == 0 ? widthScale : scale;
        double scaleZ = facing.getStepZ() == 0 ? widthScale : scale;
        double inset = (8 - radius) / 16.0;
        double x = (1 - facing.getStepX()) * 0.5 * (1 - scaleX) - facing.getStepX() * inset;
        double y = 0.5 * (1 - scale);
        double z = (1 - facing.getStepZ()) * 0.5 * (1 - scaleZ) - facing.getStepZ() * inset;
        VoxelShape shape = Shapes.empty();
        for (AABB box : original.toAabbs()) {
            shape = Shapes.or(shape, Shapes.box(box.minX * scaleX + x, box.minY * scale + y, box.minZ * scaleZ + z,
                    box.maxX * scaleX + x, box.maxY * scale + y, box.maxZ * scaleZ + z));
        }
        return shape;
    }

    @Override
    public String getDescriptionId() {
        return ModBlocks.SHELF_MUSHROOM.get().getDescriptionId();
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        return new ItemStack(ModBlocks.SHELF_MUSHROOM.get());
    }
}
