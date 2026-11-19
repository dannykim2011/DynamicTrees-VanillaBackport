package com.dannykim.dtvanillabackport.cell;

import com.dtteam.dynamictrees.api.cell.Cell;
import com.dtteam.dynamictrees.api.cell.CellKit;
import com.dtteam.dynamictrees.api.cell.CellNull;
import com.dtteam.dynamictrees.api.cell.CellSolver;
import com.dtteam.dynamictrees.systems.cell.NormalCell;
import com.dtteam.dynamictrees.systems.cell.CellKits;
import com.dtteam.dynamictrees.api.voxmap.SimpleVoxmap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public final class PoplarCellKit extends CellKit {
    private static final int TWIG_REACH = 3;
    private static final int VERTICAL_REACH = 1;
    // Wood supports the layer immediately above and below it. Leaves fill that layer sideways.
    private final Cell[] leaves = new Cell[TWIG_REACH + 1];
    private final Cell twig = new NormalCell(TWIG_REACH + 1);
    private final Cell smallBranch = new NormalCell(3);
    private final CellSolver solver = new CellKits.BasicSolver(new short[]{0x0413, 0x0312, 0x0211});
    private final SimpleVoxmap cluster;

    public PoplarCellKit(ResourceLocation id) {
        super(id);
        leaves[0] = CellNull.NULL_CELL;
        for (int i = 1; i <= TWIG_REACH; i++) {
            leaves[i] = new NormalCell(i) {
                @Override
                public int getValueFromSide(Direction side) {
                    return side.getAxis().isVertical() ? 0 : getValue();
                }
            };
        }
        int width = 2 * TWIG_REACH + 1;
        cluster = new SimpleVoxmap(width, 2 * VERTICAL_REACH + 1, width)
                .setCenter(new BlockPos(TWIG_REACH, VERTICAL_REACH, TWIG_REACH));
        // The worldgen stamp uses the same vertical reach as growing foliage.
        for (int y = -VERTICAL_REACH; y <= VERTICAL_REACH; y++) {
            for (int x = -TWIG_REACH; x <= TWIG_REACH; x++) {
                for (int z = -TWIG_REACH; z <= TWIG_REACH; z++) {
                    int distance = Math.abs(x) + Math.abs(y) + Math.abs(z);
                    if (distance > 0 && distance <= TWIG_REACH) {
                        cluster.setVoxel(x, y, z, (byte) (TWIG_REACH + 1 - distance));
                    }
                }
            }
        }
    }

    @Override
    public Cell getCellForLeaves(int hydro) {
        return leaves[Math.min(hydro, TWIG_REACH)];
    }

    @Override
    public Cell getCellForBranch(int radius, int meta) {
        // Thin wood inside the crown keeps the leaves between terminal clusters alive.
        return switch (radius) {
            case 1 -> twig;
            case 2 -> smallBranch;
            default -> CellNull.NULL_CELL;
        };
    }

    @Override
    public CellSolver getCellSolver() {
        return solver;
    }

    @Override
    public SimpleVoxmap getLeafCluster() {
        return cluster;
    }

    @Override
    public int getDefaultHydration() {
        return TWIG_REACH;
    }
}
