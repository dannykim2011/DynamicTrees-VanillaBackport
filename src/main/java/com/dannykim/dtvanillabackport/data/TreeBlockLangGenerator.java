package com.dannykim.dtvanillabackport.data;

import com.ferreusveritas.dynamictrees.api.data.Generator;
import com.ferreusveritas.dynamictrees.data.provider.DTLangProvider;

public final class TreeBlockLangGenerator implements Generator<DTLangProvider, String> {
    @Override
    public void generate(DTLangProvider provider, String input, Dependencies dependencies) {
        addTree(provider, "pale_oak", "Pale Oak");
        addTree(provider, "red_poplar", "Red Poplar");
        addTree(provider, "orange_poplar", "Orange Poplar");
        addTree(provider, "yellow_poplar", "Yellow Poplar");
    }

    private static void addTree(DTLangProvider provider, String species, String name) {
        String key = "block.dtvanillabackport." + species;
        provider.add(key + "_sapling", name + " Sapling");
        provider.add(key + "_leaves", name + " Leaves");
    }

    @Override
    public Dependencies gatherDependencies(String input) {
        return new Dependencies();
    }
}
