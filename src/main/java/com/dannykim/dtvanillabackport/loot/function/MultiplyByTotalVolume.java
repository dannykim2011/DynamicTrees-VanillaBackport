package com.dannykim.dtvanillabackport.loot.function;

import com.ferreusveritas.dynamictrees.loot.DTLootContextParams;
import com.dannykim.dtvanillabackport.registry.DTVBRegistries;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public final class MultiplyByTotalVolume extends LootItemConditionalFunction {
    private static final float VOXELS_PER_LOG = 4096.0F;

    public MultiplyByTotalVolume(final LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    public LootItemFunctionType getType() {
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
        return simpleBuilder(MultiplyByTotalVolume::new);
    }

    public static final class Serializer extends LootItemConditionalFunction.Serializer<MultiplyByTotalVolume> {
        @Override
        public MultiplyByTotalVolume deserialize(
                final JsonObject object,
                final JsonDeserializationContext context,
                final LootItemCondition[] conditions
        ) {
            return new MultiplyByTotalVolume(conditions);
        }
    }
}
