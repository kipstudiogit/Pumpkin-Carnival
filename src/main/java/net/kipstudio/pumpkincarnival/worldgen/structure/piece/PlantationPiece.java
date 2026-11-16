// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.worldgen.structure.piece;

import net.kipstudio.pumpkincarnival.init.ModBlocks;
import net.kipstudio.pumpkincarnival.worldgen.util.WorldGenHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

import java.util.List;

public abstract class PlantationPiece extends StructurePiece {
    private static final int MAX_FOUNDATION_DEPTH = 14;
    protected final BlockPos origin;

    protected PlantationPiece(StructurePieceType type, int genDepth, BoundingBox boundingBox, BlockPos origin) {
        super(type, genDepth, boundingBox);
        this.origin = origin;
    }

    protected PlantationPiece(StructurePieceType type, CompoundTag tag) {
        super(type, tag);
        this.origin = new BlockPos(tag.getInt("OrigX"), tag.getInt("OrigY"), tag.getInt("OrigZ"));
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        tag.putInt("OrigX", origin.getX());
        tag.putInt("OrigY", origin.getY());
        tag.putInt("OrigZ", origin.getZ());
    }

    protected boolean setChunkSafeBlock(WorldGenLevel level, BlockPos pos, BlockState state, BoundingBox chunkBox) {
        if (!chunkBox.isInside(pos) || !level.ensureCanWrite(pos)) {
            return false;
        }
        level.setBlock(pos, state, 2);
        return true;
    }

    protected void setFence(WorldGenLevel level, BlockPos pos, BoundingBox chunkBox, List<BlockPos> fences) {
        if (setChunkSafeBlock(level, pos, ModBlocks.DEADWOOD_FENCE.get().defaultBlockState(), chunkBox)) {
            fences.add(pos);
        }
    }

    protected void setLootChest(WorldGenLevel level, BlockPos pos, RandomSource random, ResourceLocation lootTable, BoundingBox chunkBox) {
        if (setChunkSafeBlock(level, pos, Blocks.CHEST.defaultBlockState(), chunkBox)) {
            WorldGenHelper.attachLootTable(level, pos, random, lootTable);
        }
    }

    protected void clearInteriorAir(WorldGenLevel level, BlockPos min, BlockPos max, BoundingBox chunkBox) {
        BlockState air = Blocks.AIR.defaultBlockState();
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int y = min.getY(); y <= max.getY(); y++) {
                for (int z = min.getZ(); z <= max.getZ(); z++) {
                    setChunkSafeBlock(level, new BlockPos(x, y, z), air, chunkBox);
                }
            }
        }
    }

    protected void fillFoundation(WorldGenLevel level, BlockPos startPos, BlockState state, BoundingBox chunkBox) {
        WorldGenHelper.fillFoundation(level, startPos, state, MAX_FOUNDATION_DEPTH, chunkBox::isInside);
    }

    protected void connectAllFences(WorldGenLevel level, List<BlockPos> fences, BoundingBox chunkBox) {
        for (BlockPos pos : fences) {
            if (chunkBox.isInside(pos)) {
                WorldGenHelper.connectFence(level, pos, level::ensureCanWrite);
            }
        }
    }
}