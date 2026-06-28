package com.dannykim.dtvanillabackport;

import com.ferreusveritas.dynamictrees.block.leaves.LeavesProperties;
import com.ferreusveritas.dynamictrees.block.rooty.SoilProperties;
import com.ferreusveritas.dynamictrees.api.GatherDataHelper;
import com.ferreusveritas.dynamictrees.api.registry.RegistryHandler;
import com.ferreusveritas.dynamictrees.systems.genfeature.GenFeature;
import com.ferreusveritas.dynamictrees.tree.family.Family;
import com.ferreusveritas.dynamictrees.tree.species.Species;
import com.ferreusveritas.dynamictrees.resources.Resources;
import com.dannykim.dtvanillabackport.registry.DTVBRegistries;
import com.dannykim.dtvanillabackport.client.ClientModEvents;
import com.dannykim.dtvanillabackport.tree.CreakingHeartFamily;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.data.event.GatherDataEvent;

@Mod(DynamicTreesVanillaBackport.MOD_ID)
public final class DynamicTreesVanillaBackport {
    public static final String MOD_ID = "dtvanillabackport";

    public DynamicTreesVanillaBackport() {
        final IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientModEvents::registerSpriteSource);
        RegistryHandler.setup(MOD_ID);
        DTVBRegistries.setup();
        DTVBRegistries.BLOCK_ENTITY_TYPES.register(eventBus);
        eventBus.addListener(this::commonSetup);
        eventBus.addListener(this::gatherData);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> Family.REGISTRY
                .getOptional(location("pale_oak"))
                .filter(CreakingHeartFamily.class::isInstance)
                .map(CreakingHeartFamily.class::cast)
                .ifPresent(CreakingHeartFamily::bindHeartBranch));
    }

    private void gatherData(final GatherDataEvent event) {
        Resources.MANAGER.gatherData();
        GatherDataHelper.gatherAllData(
                MOD_ID,
                event,
                SoilProperties.REGISTRY,
                Family.REGISTRY,
                Species.REGISTRY,
                LeavesProperties.REGISTRY,
                GenFeature.REGISTRY
        );
    }

    public static ResourceLocation location(final String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
