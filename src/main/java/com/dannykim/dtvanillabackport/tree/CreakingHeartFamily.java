package com.dannykim.dtvanillabackport.tree;

import com.ferreusveritas.dynamictrees.api.registry.TypedRegistry;
import com.ferreusveritas.dynamictrees.block.branch.BranchBlock;
import com.ferreusveritas.dynamictrees.tree.family.Family;
import com.ferreusveritas.dynamictrees.util.Optionals;
import com.dannykim.dtvanillabackport.registry.DTVBRegistries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Optional;
import java.util.function.Supplier;

public class CreakingHeartFamily extends Family {
    public static final TypedRegistry.EntryType<Family> TYPE = TypedRegistry.newType(CreakingHeartFamily::new);

    private Supplier<BranchBlock> heartBranch;
    private Supplier<BranchBlock> resinBranch;
    private Block primitiveHeart = Blocks.AIR;

    public CreakingHeartFamily(final ResourceLocation name) {
        super(name);
    }

    @Override
    public void setupBlocks() {
        super.setupBlocks();
        this.primitiveHeart = BuiltInRegistries.BLOCK.get(
                new ResourceLocation("minecraft", "creaking_heart")
        );
        this.heartBranch = DTVBRegistries.CREAKING_HEART_BRANCH;
        this.resinBranch = DTVBRegistries.RESIN_BRANCH;
    }

    public void bindHeartBranch() {
        final BranchBlock branch = this.heartBranch.get();
        branch.setFamily(this);
        branch.setCanBeStripped(true);
        this.addValidBranches(branch);

        final BranchBlock resin = this.resinBranch.get();
        resin.setFamily(this);
        resin.setCanBeStripped(true);
        this.addValidBranches(resin);
    }

    public Optional<BranchBlock> getHeartBranch() {
        return Optionals.ofBlock(this.heartBranch);
    }

    public Optional<BranchBlock> getResinBranch() {
        return Optionals.ofBlock(this.resinBranch);
    }

    public Optional<Block> getPrimitiveHeart() {
        return Optionals.ofBlock(this.primitiveHeart);
    }
}
