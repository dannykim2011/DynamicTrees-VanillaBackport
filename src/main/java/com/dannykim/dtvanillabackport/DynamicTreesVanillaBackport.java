package com.dannykim.dtvanillabackport;

import com.dtteam.dynamictrees.block.leaves.LeavesProperties;
import com.dtteam.dynamictrees.block.soil.SoilProperties;
import com.dtteam.dynamictrees.data.GatherDataHelper;
import com.dtteam.dynamictrees.registry.NeoForgeRegistryHandler;
import com.dtteam.dynamictrees.systems.genfeature.GenFeature;
import com.dtteam.dynamictrees.tree.family.Family;
import com.dtteam.dynamictrees.tree.species.Species;
import com.dtteam.dynamictrees.treepack.Resources;
import com.dannykim.dtvanillabackport.registry.DTVBRegistries;
import com.dannykim.dtvanillabackport.tree.CreakingHeartFamily;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@Mod(DynamicTreesVanillaBackport.MOD_ID)
public final class DynamicTreesVanillaBackport {
    public static final String MOD_ID = "dtvanillabackport";

    public DynamicTreesVanillaBackport(final IEventBus eventBus) {
        NeoForgeRegistryHandler.setup(MOD_ID, eventBus);
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
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
