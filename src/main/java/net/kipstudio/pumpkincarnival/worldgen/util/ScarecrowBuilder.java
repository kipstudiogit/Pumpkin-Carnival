// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.worldgen.util;

import net.kipstudio.pumpkincarnival.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Predicate;

public final class ScarecrowBuilder {
    private ScarecrowBuilder() {
    }

    public static void build(WorldGenLevel level, BlockPos base, Direction armDir, Direction headFacing, boolean litHead, Predicate<BlockPos> boundsCheck) {
        Direction oppDir = armDir.getOpposite();
        BlockPos post1 = base;
        BlockPos post2 = base.above();
        BlockPos arm1 = post2.relative(armDir);
        BlockPos arm2 = post2.relative(oppDir);
        BlockPos headPos = base.above(2);
        BlockPos hatPos = base.above(3);

        BlockState baseFence = ModBlocks.DEADWOOD_FENCE.get().defaultBlockState();
        BlockState chestState = baseFence
                .setValue(WorldGenHelper.getFenceDirectionProperty(armDir), Boolean.TRUE)
                .setValue(WorldGenHelper.getFenceDirectionProperty(oppDir), Boolean.TRUE);
        BlockState arm1State = baseFence.setValue(WorldGenHelper.getFenceDirectionProperty(oppDir), Boolean.TRUE);
        BlockState arm2State = baseFence.setValue(WorldGenHelper.getFenceDirectionProperty(armDir), Boolean.TRUE);

        setBounded(level, post1, baseFence, boundsCheck);
        setBounded(level, post2, chestState, boundsCheck);
        setBounded(level, arm1, arm1State, boundsCheck);
        setBounded(level, arm2, arm2State, boundsCheck);

        BlockPos lanternPos = arm1.below();
        if (boundsCheck.test(lanternPos) && level.ensureCanWrite(lanternPos) && level.isEmptyBlock(lanternPos)) {
            level.setBlock(lanternPos, ModBlocks.CARNIVAL_LANTERN.get().defaultBlockState().setValue(LanternBlock.HANGING, Boolean.TRUE), 2);
        }

        BlockPos cobwebPos = arm2.below();
        if (boundsCheck.test(cobwebPos) && level.ensureCanWrite(cobwebPos) && level.isEmptyBlock(cobwebPos)) {
            level.setBlock(cobwebPos, Blocks.COBWEB.defaultBlockState(), 2);
        }

        BlockState pumpkinHead = (litHead ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN)
                .defaultBlockState()
                .setValue(CarvedPumpkinBlock.FACING, headFacing);
        setBounded(level, headPos, pumpkinHead, boundsCheck);
        setBounded(level, hatPos, ModBlocks.DEADWOOD_SLAB.get().defaultBlockState(), boundsCheck);
    }

    private static void setBounded(WorldGenLevel level, BlockPos pos, BlockState state, Predicate<BlockPos> boundsCheck) {
        if (boundsCheck.test(pos) && level.ensureCanWrite(pos)) {
            level.setBlock(pos, state, 2);
        }
    }
}