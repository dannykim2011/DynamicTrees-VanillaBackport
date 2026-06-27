package com.dannykim.dtvanillabackport.registry;

import com.dtteam.dynamictrees.event.RegistryEvent;
import com.dtteam.dynamictrees.event.TypeRegistryEvent;
import com.dtteam.dynamictrees.api.registry.RegistryHandler;
import com.dtteam.dynamictrees.block.branch.BranchBlock;
import com.dtteam.dynamictrees.systems.genfeature.GenFeature;
import com.dtteam.dynamictrees.tree.family.Family;
import com.dannykim.dtvanillabackport.DynamicTreesVanillaBackport;
import com.dannykim.dtvanillabackport.block.CreakingHeartBranchBlock;
import com.dannykim.dtvanillabackport.block.CreakingHeartBranchBlockEntity;
import com.dannykim.dtvanillabackport.genfeature.CreakingHeartGenFeature;
import com.dannykim.dtvanillabackport.genfeature.VineGenFeature2;
import com.dannykim.dtvanillabackport.tree.CreakingHeartFamily;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@EventBusSubscriber(modid = DynamicTreesVanillaBackport.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class DTVBRegistries {
    public static Supplier<BranchBlock> CREAKING_HEART_BRANCH;

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, DynamicTreesVanillaBackport.MOD_ID);

    public static final Supplier<BlockEntityType<CreakingHeartBranchBlockEntity>> CREAKING_HEART =
            BLOCK_ENTITY_TYPES.register("creaking_heart", () ->
                    BlockEntityType.Builder.of(
                            CreakingHeartBranchBlockEntity::new,
                            CREAKING_HEART_BRANCH.get()
                    ).build(null));

    public static final GenFeature CREAKING_HEART_GEN_FEATURE =
            new CreakingHeartGenFeature(DynamicTreesVanillaBackport.location("creaking_heart"));

    public static final GenFeature VINE_GEN_FEATURE_2 =
            new VineGenFeature2(DynamicTreesVanillaBackport.location("vine_gen_feature_2"));
    private DTVBRegistries() {
    }

    public static void setup() {
        CREAKING_HEART_BRANCH = RegistryHandler.addBlock(
                DynamicTreesVanillaBackport.location("pale_oak_creaking_heart_branch"),
                () -> new CreakingHeartBranchBlock(
                        DynamicTreesVanillaBackport.location("pale_oak_creaking_heart"),
                        BlockBehaviour.Properties.of()
                                .mapColor(MapColor.WOOD)
                                .strength(2.0F)
                                .sound(SoundType.WOOD)
                                .ignitedByLava()
                )
        );
    }

    @SubscribeEvent
    public static void registerFamilyTypes(final TypeRegistryEvent<Family> event) {
        if (event.isEntryOfType(Family.class)) {
            event.registerType(DynamicTreesVanillaBackport.location("creaking_heart"), CreakingHeartFamily.TYPE);
        }
    }

    @SubscribeEvent
    public static void registerGenFeatures(final RegistryEvent<GenFeature> event) {
        if (event.isEntryOfType(GenFeature.class)) {
            event.getRegistry().register(CREAKING_HEART_GEN_FEATURE);
            event.getRegistry().register(VINE_GEN_FEATURE_2);
        }
    }
}
