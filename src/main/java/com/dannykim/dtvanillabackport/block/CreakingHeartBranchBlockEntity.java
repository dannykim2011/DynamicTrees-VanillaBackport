package com.dannykim.dtvanillabackport.block;

import com.blackgear.vanillabackport.common.level.blockentities.CreakingHeartBlockEntity;
import com.blackgear.vanillabackport.common.registries.ModBlocks;
import com.dannykim.dtvanillabackport.registry.DTVBRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

public class CreakingHeartBranchBlockEntity extends CreakingHeartBlockEntity {
    public CreakingHeartBranchBlockEntity(final BlockPos pos, final BlockState state) {
        super(pos, ModBlocks.CREAKING_HEART.get().defaultBlockState());
        ObfuscationReflectionHelper.setPrivateValue(
                BlockEntity.class, this, DTVBRegistries.CREAKING_HEART.get(), "f_58855_"
        );
        ObfuscationReflectionHelper.setPrivateValue(
                BlockEntity.class, this, state, "f_58856_"
        );
    }
}
