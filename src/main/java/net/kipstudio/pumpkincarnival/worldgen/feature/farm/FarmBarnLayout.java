// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.worldgen.feature.farm;

import net.kipstudio.pumpkincarnival.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

public class FarmBarnLayout implements FarmLayout {
    @Override
    public void build(FarmBuildContext ctx, BlockPos center) {
        buildGreatBarn(ctx, center.offset(-8, 0, -5));
        buildSunkenCellar(ctx, center.offset(3, 0, -4));
        FarmYardProps.buildBrokenHarvestCart(ctx, center.offset(2, 0, 4));
        FarmYardProps.buildPerimeterAndGate(ctx, center);
    }

    private static void buildGreatBarn(FarmBuildContext ctx, BlockPos origin) {
        RandomSource random = ctx.random();
        BlockState logY = ModBlocks.DEADWOOD_LOG.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, Direction.Axis.Y);
        BlockState planks = ModBlocks.DEADWOOD_PLANKS.get().defaultBlockState();
        BlockState slab = ModBlocks.DEADWOOD_SLAB.get().defaultBlockState();

        int width = 8;
        int depth = 7;
        int height = 5;

        ctx.clearAir(origin.offset(0, 1, 0), origin.offset(width - 1, height, depth - 1));

        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                BlockPos floor = origin.offset(x, 0, z);
                ctx.fillFoundation(floor.below(), Blocks.COARSE_DIRT.defaultBlockState());
                ctx.setBlock(floor, random.nextInt(3) == 0 ? Blocks.DIRT.defaultBlockState() : planks);
            }
        }

        for (int y = 1; y <= height; y++) {
            for (int x = 0; x < width; x += 3) {
                for (int z = 0; z < depth; z += 6) {
                    ctx.setBlock(origin.offset(x, y, z), logY);
                }
            }
        }

        for (int x = -1; x <= width; x++) {
            for (int z = -1; z <= depth; z++) {
                if (random.nextInt(10) != 0) {
                    ctx.setBlock(origin.offset(x, height + 1, z), slab);
                }
            }
        }

        for (int i = 0; i < 3; i++) {
            ctx.setBlock(origin.offset(2 + i, 1, 3), Blocks.HAY_BLOCK.defaultBlockState());
            ctx.setBlock(origin.offset(2 + i, 1, 4), Blocks.PUMPKIN.defaultBlockState());
        }
    }

    private static void buildSunkenCellar(FarmBuildContext ctx, BlockPos origin) {
        RandomSource random = ctx.random();
        BlockState mossy = Blocks.MOSSY_COBBLESTONE.defaultBlockState();
        BlockState trapdoor = ModBlocks.DEADWOOD_TRAPDOOR.get().defaultBlockState();

        int size = 5;
        for (int x = 0; x < size; x++) {
            for (int z = 0; z < size; z++) {
                BlockPos pit = origin.offset(x, -1, z);
                ctx.fillFoundation(pit.below(), Blocks.DIRT.defaultBlockState());
                ctx.setBlock(pit, Blocks.DIRT.defaultBlockState());

                if (x == 0 || x == size - 1 || z == 0 || z == size - 1) {
                    ctx.setBlock(origin.offset(x, 0, z), mossy);
                } else {
                    ctx.setBlock(origin.offset(x, 1, z), trapdoor);
                    BlockState pumpkin = random.nextBoolean() ? Blocks.CARVED_PUMPKIN.defaultBlockState() : Blocks.PUMPKIN.defaultBlockState();
                    ctx.setBlock(pit.above(), pumpkin);
                }
            }
        }
    }
}