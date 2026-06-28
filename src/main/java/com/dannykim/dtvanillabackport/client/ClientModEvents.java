package com.dannykim.dtvanillabackport.client;

import com.mojang.serialization.Codec;
import com.dannykim.dtvanillabackport.DynamicTreesVanillaBackport;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Method;

public final class ClientModEvents {
    private ClientModEvents() {
    }

    public static void registerSpriteSource() {
        if (ThickBranchRingsSource.TYPE != null) {
            return;
        }
        try {
            final Method register = ObfuscationReflectionHelper.findMethod(
                    SpriteSources.class,
                    "m_260887_",
                    String.class,
                    Codec.class
            );
            ThickBranchRingsSource.TYPE = (SpriteSourceType) register.invoke(
                    null,
                    DynamicTreesVanillaBackport.MOD_ID + ":thick_branch_rings",
                    ThickBranchRingsSource.CODEC
            );
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Failed to register thick branch rings sprite source", exception);
        }
    }
}
