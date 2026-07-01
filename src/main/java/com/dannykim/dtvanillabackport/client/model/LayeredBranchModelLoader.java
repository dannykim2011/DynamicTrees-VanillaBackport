package com.dannykim.dtvanillabackport.client.model;

import com.ferreusveritas.dynamictrees.api.TreeRegistry;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public final class LayeredBranchModelLoader implements IGeometryLoader<LayeredBranchModelGeometry> {
    @Override
    public LayeredBranchModelGeometry read(final JsonObject model,
                                           final JsonDeserializationContext context) {
        final ResourceLocation family = TreeRegistry.processResLoc(
                new ResourceLocation(model.get("family").getAsString())
        );
        final JsonObject base = model.getAsJsonObject("base");
        final JsonObject overlay = model.getAsJsonObject("overlay");
        return new LayeredBranchModelGeometry(
                family,
                texture(base, "bark"),
                texture(base, "rings"),
                texture(overlay, "bark"),
                texture(overlay, "rings")
        );
    }

    private static ResourceLocation texture(final JsonObject object, final String name) {
        return new ResourceLocation(object.get(name).getAsString());
    }
}
