package com.dannykim.dtvanillabackport.tree;

import com.dtteam.dynamictrees.api.registry.TypedRegistry;
import com.dtteam.dynamictrees.block.branch.BranchBlock;
import com.dtteam.dynamictrees.tree.family.Family;
import com.dtteam.dynamictrees.utility.Optionals;
import com.dannykim.dtvanillabackport.registry.DTVBRegistries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Optional;
import java.util.function.Supplier;

public class CreakingHeartFamily extends Family {
    public static final TypedRegistry.EntryType<Family> TYPE = TypedRegistry.newType(CreakingHeartFamily::new);

    private Supplier<BranchBlock> heartBranch;
    private Block primitiveHeart = Blocks.AIR;

    public CreakingHeartFamily(final ResourceLocation name) {
        super(name);
    }

    @Override
    public void setupBlocks() {
        super.setupBlocks();
        this.primitiveHeart = BuiltInRegistries.BLOCK.get(
                ResourceLocation.fromNamespaceAndPath("minecraft", "creaking_heart")
        );
        this.heartBranch = DTVBRegistries.CREAKING_HEART_BRANCH;
    }

    public void bindHeartBranch() {
        final BranchBlock branch = this.heartBranch.get();
        branch.setFamily(this);
        branch.setPrimitiveLogDrops(new ItemStack(this.primitiveHeart));
        this.addValidBranches(branch);
    }

    public Optional<BranchBlock> getHeartBranch() {
        return Optionals.ofBlock(this.heartBranch);
    }

    public Optional<Block> getPrimitiveHeart() {
        return Optionals.ofBlock(this.primitiveHeart);
    }
}
