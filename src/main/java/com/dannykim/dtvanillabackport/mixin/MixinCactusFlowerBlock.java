package com.dannykim.dtvanillabackport.mixin;

import com.blackgear.vanillabackport.common.level.blocks.CactusFlowerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CactusFlowerBlock.class)
public abstract class MixinCactusFlowerBlock {
    private static final ResourceLocation DYNAMIC_CACTUS_BRANCH =
            new ResourceLocation("dynamictreesplus", "cactus_branch");

    @Inject(method = "m_6266_", at = @At("HEAD"), cancellable = true, remap = false)
    private void dtvanillabackport$allowDynamicCactusBranch(
            final BlockState state,
            final BlockGetter level,
            final BlockPos pos,
            final CallbackInfoReturnable<Boolean> cir
    ) {
        if (DYNAMIC_CACTUS_BRANCH.equals(BuiltInRegistries.BLOCK.getKey(state.getBlock()))) {
            cir.setReturnValue(true);
        }
    }
}
