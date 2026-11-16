// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.worldgen.feature.farm;

import net.kipstudio.pumpkincarnival.init.ModBlocks;
import net.kipstudio.pumpkincarnival.worldgen.util.WorldGenHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class FarmBuildContext {
    private static final int MAX_FOUNDATION_DEPTH = 8;

    private final WorldGenLevel level;
    private final ChunkPos centerChunk;
    private final RandomSource random;
    private final List<BlockPos> fences = new ArrayList<>();

    public FarmBuildContext(WorldGenLevel level, ChunkPos centerChunk, RandomSource random) {
        this.level = level;
        this.centerChunk = centerChunk;
        this.random = random;
    }

    public WorldGenLevel level() {
        return level;
    }

    public RandomSource random() {
        return random;
    }

    public boolean isInChunkRegion(BlockPos pos) {
        int chunkX = pos.getX() >> 4;
        int chunkZ = pos.getZ() >> 4;
        return Math.abs(chunkX - centerChunk.x) <= 1 && Math.abs(chunkZ - centerChunk.z) <= 1;
    }

    public boolean setBlock(BlockPos pos, BlockState state) {
        if (!isInChunkRegion(pos) || !level.ensureCanWrite(pos)) {
            return false;
        }
        level.setBlock(pos, state, 2);
        return true;
    }

    public void clearAir(BlockPos min, BlockPos max) {
        BlockState air = Blocks.AIR.defaultBlockState();
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int y = min.getY(); y <= max.getY(); y++) {
                for (int z = min.getZ(); z <= max.getZ(); z++) {
                    setBlock(new BlockPos(x, y, z), air);
                }
            }
        }
    }

    public void setFence(BlockPos pos) {
        if (setBlock(pos, ModBlocks.DEADWOOD_FENCE.get().defaultBlockState())) {
            fences.add(pos);
        }
    }

    public void registerFence(BlockPos pos) {
        fences.add(pos);
    }

    public void fillFoundation(BlockPos startPos, BlockState state) {
        WorldGenHelper.fillFoundation(level, startPos, state, MAX_FOUNDATION_DEPTH, this::isInChunkRegion);
    }

    public void setLootChest(BlockPos pos, ResourceLocation lootTable) {
        if (setBlock(pos, Blocks.CHEST.defaultBlockState())) {
            WorldGenHelper.attachLootTable(level, pos, random, lootTable);
        }
    }

    public void connectAllFences() {
        for (BlockPos pos : fences) {
            WorldGenHelper.connectFence(level, pos, this::isInChunkRegion);
        }
    }
}