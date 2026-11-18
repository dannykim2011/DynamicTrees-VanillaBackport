package com.dannykim.dtvanillabackport.data;

import com.dtteam.dynamictrees.data.DTDataProvider;
import com.dtteam.dynamictrees.data.Generator;
import com.dtteam.dynamictrees.data.provider.DTLangProvider;

public final class TreeBlockLangGenerator implements Generator<DTDataProvider.Language, String> {
    @Override
    public void generate(DTDataProvider.Language language, String input, Dependencies dependencies) {
        if (!(language instanceof DTLangProvider provider)) return;
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
