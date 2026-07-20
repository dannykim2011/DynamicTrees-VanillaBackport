package com.dannykim.dtvanillabackport.mixin;

import com.blackgear.vanillabackport.common.level.block.CreakingHeartBlock;
import com.dannykim.dtvanillabackport.block.CreakingHeartBranchBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CreakingHeartBlock.class)
public class MixinCreakingHeartBlock {
    @Inject(method = "hasRequiredLogs", at = @At("HEAD"), cancellable = true, remap = false)
    private static void dtvanillabackport$hasRequiredLogs(final BlockState state, final LevelReader level, final BlockPos pos, final CallbackInfoReturnable<Boolean> cir) {
        if (state.getBlock() instanceof CreakingHeartBranchBlock) {
            cir.setReturnValue(CreakingHeartBranchBlock.hasRequiredLogs(state, level, pos));
        }
    }
}