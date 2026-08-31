package com.dannykim.dtvanillabackport.loot.function;

import com.dtteam.dynamictrees.loot.DTLootContextParams;
import com.dannykim.dtvanillabackport.registry.DTVBRegistries;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;

public final class MultiplyByTotalVolume extends LootItemConditionalFunction {
    public static final MapCodec<MultiplyByTotalVolume> CODEC = RecordCodecBuilder.mapCodec(instance ->
            commonFields(instance).apply(instance, MultiplyByTotalVolume::new));
    private static final float VOXELS_PER_LOG = 4096.0F;

    public MultiplyByTotalVolume(final List<LootItemCondition> conditions) {
        super(conditions);
    }

    @Override
    public LootItemFunctionType<? extends LootItemConditionalFunction> getType() {
        return DTVBRegistries.MULTIPLY_TOTAL_VOLUME.get();
    }

    @Override
    protected ItemStack run(final ItemStack stack, final LootContext context) {
        final Integer volume = context.getParamOrNull(DTLootContextParams.VOLUME);
        if (volume != null) {
            stack.setCount(Math.round(stack.getCount() * (volume / VOXELS_PER_LOG)));
        }
        return stack;
    }

    public static LootItemFunction.Builder multiplyByTotalVolume() {
        return () -> new MultiplyByTotalVolume(List.of());
    }
}
