// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.worldgen.feature.farm;

import net.kipstudio.pumpkincarnival.init.ModBlocks;
import net.kipstudio.pumpkincarnival.worldgen.util.WorldGenHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

public final class FarmYardProps {
    private FarmYardProps() {
    }

    public static void buildCemetery(FarmBuildContext ctx, BlockPos origin) {
        RandomSource random = ctx.random();
        BlockState coarseDirt = Blocks.COARSE_DIRT.defaultBlockState();

        for (int g = 0; g < 3; g++) {
            BlockPos grave = origin.offset(g * 2, 0, 0);
            ctx.fillFoundation(grave.below(), coarseDirt);
            ctx.setBlock(grave, coarseDirt);
            ctx.setBlock(grave.offset(0, 0, 1), coarseDirt);
            ctx.setBlock(grave.above(), Blocks.MOSSY_COBBLESTONE.defaultBlockState());
            if (random.nextBoolean()) {
                ctx.setBlock(grave.offset(0, 1, 1), Blocks.WITHER_ROSE.defaultBlockState());
            }
        }
    }

    public static void buildPumpkinMounds(FarmBuildContext ctx, BlockPos origin) {
        RandomSource random = ctx.random();
        for (int i = 0; i < 5; i++) {
            BlockPos mound = origin.offset(random.nextInt(5) - 2, 0, random.nextInt(5) - 2);
            ctx.fillFoundation(mound.below(), Blocks.COARSE_DIRT.defaultBlockState());
            ctx.setBlock(mound, Blocks.HAY_BLOCK.defaultBlockState());
            BlockState pumpkin = random.nextBoolean()
                    ? Blocks.CARVED_PUMPKIN.defaultBlockState()
                    : Blocks.PUMPKIN.defaultBlockState();
            ctx.setBlock(mound.above(), pumpkin);
        }
    }

    public static void buildBrokenHarvestCart(FarmBuildContext ctx, BlockPos origin) {
        BlockState planks = ModBlocks.DEADWOOD_PLANKS.get().defaultBlockState();
        ctx.fillFoundation(origin.below(), Blocks.COARSE_DIRT.defaultBlockState());
        ctx.fillFoundation(origin.relative(Direction.EAST).below(), Blocks.COARSE_DIRT.defaultBlockState());
        ctx.setBlock(origin, planks);
        ctx.setBlock(origin.relative(Direction.EAST), planks);

        ctx.setFence(origin.relative(Direction.NORTH));
        ctx.setFence(origin.relative(Direction.SOUTH));

        ctx.setBlock(origin.above(), Blocks.HAY_BLOCK.defaultBlockState());
        ctx.setBlock(origin.offset(1, 0, 1), Blocks.CARVED_PUMPKIN.defaultBlockState());
        ctx.setBlock(origin.offset(-1, 0, 0), Blocks.PUMPKIN.defaultBlockState());
    }

    public static void buildPerimeterAndGate(FarmBuildContext ctx, BlockPos center) {
        WorldGenLevel level = ctx.level();
        RandomSource random = ctx.random();
        int rx = 11;
        int rz = 10;

        for (int x = -rx; x <= rx; x++) {
            for (int z = -rz; z <= rz; z++) {
                if (Math.abs(x) != rx && Math.abs(z) != rz) {
                    continue;
                }

                BlockPos fencePos = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, center.offset(x, 0, z));
                if (!ctx.isInChunkRegion(fencePos) || !level.ensureCanWrite(fencePos) || !level.ensureCanWrite(fencePos.below())) {
                    continue;
                }

                if (!level.getFluidState(fencePos.below()).isEmpty() || !WorldGenHelper.isNaturalSoil(level.getBlockState(fencePos.below()))) {
                    continue;
                }

                if (x == 0 && z == rz) {
                    ctx.setBlock(fencePos, ModBlocks.DEADWOOD_FENCE_GATE.get().defaultBlockState());
                    ctx.setBlock(fencePos.below(), Blocks.COARSE_DIRT.defaultBlockState());
                } else if (random.nextInt(7) != 0) {
                    ctx.setFence(fencePos);
                    ctx.fillFoundation(fencePos.below(), Blocks.COARSE_DIRT.defaultBlockState());
                }
            }
        }
    }
}