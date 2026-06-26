package com.dannykim.dtvanillabackport.block;

import com.blackgear.vanillabackport.common.level.blockentities.CreakingHeartBlockEntity;
import com.blackgear.vanillabackport.common.registries.ModBlocks;
import com.dannykim.dtvanillabackport.registry.DTVBRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class CreakingHeartBranchBlockEntity extends CreakingHeartBlockEntity {
    public CreakingHeartBranchBlockEntity(final BlockPos pos, final BlockState state) {
        super(pos, ModBlocks.CREAKING_HEART.get().defaultBlockState());
        this.type = DTVBRegistries.CREAKING_HEART.get();
        this.blockState = state;
    }
}
