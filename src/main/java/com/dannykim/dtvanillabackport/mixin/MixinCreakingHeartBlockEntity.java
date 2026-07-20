package com.dannykim.dtvanillabackport.mixin;

import com.blackgear.vanillabackport.common.level.block_entity.CreakingHeartBlockEntity;
import com.dannykim.dtvanillabackport.block.CreakingHeartBranchBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(CreakingHeartBlockEntity.class)
public abstract class MixinCreakingHeartBlockEntity {
    @Inject(method = "spreadResin", at = @At("HEAD"), cancellable = true)
    private void dtvanillabackport$spreadResinOnDynamicBranches(
            final CallbackInfoReturnable<Optional<BlockPos>> cir
    ) {
        if ((Object) this instanceof CreakingHeartBranchBlockEntity heart
                && heart.getLevel() instanceof ServerLevel level) {
            cir.setReturnValue(heart.spreadResinOnBranches(level));
        }
    }
}
