package com.dannykim.dtvanillabackport.client.model;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.IDynamicBakedModel;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class LayeredBranchBakedModel extends BakedModelWrapper<BakedModel>
        implements IDynamicBakedModel {
    private final BakedModel overlayModel;

    public LayeredBranchBakedModel(final BakedModel baseModel, final BakedModel overlayModel) {
        super(baseModel);
        this.overlayModel = overlayModel;
    }

    @Override
    public List<BakedQuad> getQuads(
            final @Nullable BlockState state,
            final @Nullable Direction side,
            final RandomSource random,
            final ModelData modelData,
            final @Nullable RenderType renderType
    ) {
        final List<BakedQuad> baseQuads =
                this.originalModel.getQuads(state, side, random, modelData, renderType);
        final List<BakedQuad> overlayQuads =
                this.overlayModel.getQuads(state, side, random, modelData, renderType);
        if (baseQuads.isEmpty()) {
            return overlayQuads;
        }
        if (overlayQuads.isEmpty()) {
            return baseQuads;
        }
        final List<BakedQuad> combined =
                new ArrayList<>(baseQuads.size() + overlayQuads.size());
        combined.addAll(baseQuads);
        combined.addAll(overlayQuads);
        return combined;
    }

    @Override
    public ModelData getModelData(final BlockAndTintGetter level, final BlockPos pos,
                                  final BlockState state, final ModelData modelData) {
        return this.originalModel.getModelData(level, pos, state, modelData);
    }

    @Override
    public ChunkRenderTypeSet getRenderTypes(final BlockState state,
                                             final RandomSource random,
                                             final ModelData modelData) {
        return ChunkRenderTypeSet.of(RenderType.cutout());
    }
}
