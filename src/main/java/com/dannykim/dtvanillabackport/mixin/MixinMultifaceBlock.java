package com.dannykim.dtvanillabackport.mixin;

import com.blackgear.vanillabackport.core.data.tags.ModBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultifaceBlock.class)
public abstract class MixinMultifaceBlock {
    @Inject(method = "m_153829_", at = @At("HEAD"), cancellable = true, remap = false)
    private static void dtvanillabackport$allowCreakingHeartHolders(
            final BlockGetter level,
            final Direction direction,
            final BlockPos pos,
            final BlockState state,
            final CallbackInfoReturnable<Boolean> cir
    ) {
        if (state.is(ModBlockTags.CREAKING_HEART_HOLDERS)) {
            cir.setReturnValue(true);
        }
    }
}