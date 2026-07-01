package com.dannykim.dtvanillabackport.client;

import com.mojang.serialization.Codec;
import com.dannykim.dtvanillabackport.DynamicTreesVanillaBackport;
import com.dannykim.dtvanillabackport.client.model.LayeredBranchModelLoader;
import com.dannykim.dtvanillabackport.registry.DTVBRegistries;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Method;

public final class ClientModEvents {
    private ClientModEvents() {
    }

    public static void register(final IEventBus eventBus) {
        registerSpriteSource();
        eventBus.addListener(ClientModEvents::registerGeometryLoaders);
        eventBus.addListener(ClientModEvents::clientSetup);
    }

    private static void registerGeometryLoaders(final ModelEvent.RegisterGeometryLoaders event) {
        event.register("layered_branch", new LayeredBranchModelLoader());
    }

    private static void registerSpriteSource() {
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

    private static void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(DTVBRegistries.RESIN_BRANCH.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(DTVBRegistries.CREAKING_HEART_BRANCH.get(), RenderType.cutout());
        });
    }
}
