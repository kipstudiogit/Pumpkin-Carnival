// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.worldgen.feature;

import com.mojang.serialization.Codec;
import net.kipstudio.pumpkincarnival.init.ModBlocks;
import net.kipstudio.pumpkincarnival.init.ModLootTables;
import net.kipstudio.pumpkincarnival.worldgen.util.WorldGenHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.ArrayList;
import java.util.List;

public class CarnivalRelicFeature extends Feature<NoneFeatureConfiguration> {
    public CarnivalRelicFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        if (!WorldGenHelper.isDrySurface(level, origin)) {
            return false;
        }

        BlockState groundState = level.getBlockState(origin.below());
        if (!groundState.is(Blocks.GRASS_BLOCK) && !groundState.is(Blocks.DIRT) && !groundState.is(Blocks.COARSE_DIRT)) {
            return false;
        }

        if (random.nextBoolean()) {
            generateAbandonedCart(level, origin, random);
        } else {
            generatePumpkinCairn(level, origin, random);
        }

        return true;
    }

    private static void generateAbandonedCart(WorldGenLevel level, BlockPos origin, RandomSource random) {
        Direction forward = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        Direction side = forward.getClockWise();

        BlockState planks = ModBlocks.DEADWOOD_PLANKS.get().defaultBlockState();
        BlockState fence = ModBlocks.DEADWOOD_FENCE.get().defaultBlockState();
        BlockState slab = ModBlocks.DEADWOOD_SLAB.get().defaultBlockState();
        BlockState coarseDirt = Blocks.COARSE_DIRT.defaultBlockState();

        List<BlockPos> wheels = new ArrayList<>();
        for (int l = 0; l < 3; l++) {
            for (int w = -1; w <= 1; w++) {
                BlockPos pos = origin.relative(forward, l).relative(side, w);
                if (!level.ensureCanWrite(pos) || !level.ensureCanWrite(pos.below())) {
                    continue;
                }
                level.setBlock(pos.below(), coarseDirt, 2);
                WorldGenHelper.fillFoundation(level, pos.below(2), coarseDirt, 3, level::ensureCanWrite);
                if (Math.abs(w) == 1 && (l == 0 || l == 2)) {
                    level.setBlock(pos, fence, 2);
                    wheels.add(pos);
                } else {
                    level.setBlock(pos, planks, 2);
                }
            }
        }

        for (BlockPos wheel : wheels) {
            WorldGenHelper.connectFence(level, wheel, level::ensureCanWrite);
        }

        BlockPos chestPos = origin.relative(forward, 1).above();
        if (WorldGenHelper.setBlockIfWritable(level, chestPos, Blocks.CHEST.defaultBlockState())) {
            WorldGenHelper.attachLootTable(level, chestPos, random, ModLootTables.CHEST_CARNIVAL_RELIC);
        }

        BlockPos roofPos = chestPos.above();
        if (WorldGenHelper.setBlockIfWritable(level, roofPos, slab)) {
            WorldGenHelper.setBlockIfWritable(level, roofPos.relative(forward), Blocks.COBWEB.defaultBlockState());
        }
    }

    private static void generatePumpkinCairn(WorldGenLevel level, BlockPos origin, RandomSource random) {
        BlockState coarseDirt = Blocks.COARSE_DIRT.defaultBlockState();
        BlockState mossy = Blocks.MOSSY_COBBLESTONE.defaultBlockState();

        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos perimeter = origin.relative(dir);
            if (level.ensureCanWrite(perimeter) && level.ensureCanWrite(perimeter.below())) {
                level.setBlock(perimeter.below(), coarseDirt, 2);
                WorldGenHelper.fillFoundation(level, perimeter.below(2), coarseDirt, 3, level::ensureCanWrite);
                level.setBlock(perimeter, mossy, 2);
            }
        }

        if (WorldGenHelper.setBlockIfWritable(level, origin, mossy)) {
            Direction facing = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            BlockState jack = Blocks.JACK_O_LANTERN.defaultBlockState().setValue(CarvedPumpkinBlock.FACING, facing);
            WorldGenHelper.setBlockIfWritable(level, origin.above(), jack);
        }
    }
}