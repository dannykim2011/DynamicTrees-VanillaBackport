package com.dannykim.dtvanillabackport.client.model;

import com.ferreusveritas.dynamictrees.models.geometry.BranchBlockModelGeometry;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;

import java.util.function.Function;

public final class LayeredBranchModelGeometry
        implements IUnbakedGeometry<LayeredBranchModelGeometry> {
    private final BranchBlockModelGeometry base;
    private final BranchBlockModelGeometry overlay;

    public LayeredBranchModelGeometry(
            final ResourceLocation family,
            final ResourceLocation baseBark,
            final ResourceLocation baseRings,
            final ResourceLocation overlayBark,
            final ResourceLocation overlayRings
    ) {
        this.base = new BranchBlockModelGeometry(baseBark, baseRings, family, false);
        this.overlay = new BranchBlockModelGeometry(overlayBark, overlayRings, family, false);
    }

    @Override
    public BakedModel bake(
            final IGeometryBakingContext context,
            final ModelBaker baker,
            final Function<Material, TextureAtlasSprite> spriteGetter,
            final ModelState modelState,
            final ItemOverrides overrides,
            final ResourceLocation modelLocation
    ) {
        final BakedModel baseModel = this.base.bake(
                context, baker, spriteGetter, modelState, overrides, modelLocation
        );
        final BakedModel overlayModel = this.overlay.bake(
                context, baker, spriteGetter, modelState, overrides, modelLocation
        );
        return new LayeredBranchBakedModel(baseModel, overlayModel);
    }
}
