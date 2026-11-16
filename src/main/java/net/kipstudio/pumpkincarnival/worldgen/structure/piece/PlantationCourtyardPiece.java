// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.worldgen.structure.piece;

import net.kipstudio.pumpkincarnival.init.ModBlocks;
import net.kipstudio.pumpkincarnival.init.ModStructurePieceTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;

import java.util.ArrayList;
import java.util.List;

public class PlantationCourtyardPiece extends PlantationPiece {
    public PlantationCourtyardPiece(BoundingBox box, BlockPos origin) {
        super(ModStructurePieceTypes.COURTYARD.get(), 0, box, origin);
    }

    public PlantationCourtyardPiece(StructurePieceSerializationContext context, CompoundTag tag) {
        super(ModStructurePieceTypes.COURTYARD.get(), tag);
    }

    @Override
    public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator, RandomSource random, BoundingBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        BlockState planks = ModBlocks.DEADWOOD_PLANKS.get().defaultBlockState();
        BlockState slab = ModBlocks.DEADWOOD_SLAB.get().defaultBlockState();
        BlockState coarseDirt = Blocks.COARSE_DIRT.defaultBlockState();
        BlockState cobble = Blocks.COBBLESTONE.defaultBlockState();

        List<BlockPos> fences = new ArrayList<>();

        BlockPos cart = origin.offset(4, 0, 4);
        fillFoundation(level, cart.below(), coarseDirt, chunkBox);
        fillFoundation(level, cart.relative(Direction.EAST).below(), coarseDirt, chunkBox);
        fillFoundation(level, cart.relative(Direction.SOUTH).below(), coarseDirt, chunkBox);
        setChunkSafeBlock(level, cart, planks, chunkBox);
        setChunkSafeBlock(level, cart.relative(Direction.EAST), planks, chunkBox);
        setChunkSafeBlock(level, cart.relative(Direction.SOUTH), planks, chunkBox);

        setFence(level, cart.relative(Direction.NORTH), chunkBox, fences);
        setFence(level, cart.relative(Direction.SOUTH, 2), chunkBox, fences);
        setChunkSafeBlock(level, cart.above(), Blocks.HAY_BLOCK.defaultBlockState(), chunkBox);
        setChunkSafeBlock(level, cart.offset(1, 0, 1), Blocks.CARVED_PUMPKIN.defaultBlockState(), chunkBox);

        for (int g = 0; g < 4; g++) {
            BlockPos grave = origin.offset(10 + g * 2, 0, 3);
            fillFoundation(level, grave.below(), coarseDirt, chunkBox);
            setChunkSafeBlock(level, grave, coarseDirt, chunkBox);
            setChunkSafeBlock(level, grave.above(), Blocks.MOSSY_COBBLESTONE.defaultBlockState(), chunkBox);
            if (random.nextBoolean()) {
                setChunkSafeBlock(level, grave.offset(0, 1, 1), Blocks.WITHER_ROSE.defaultBlockState(), chunkBox);
            }
        }

        for (int p = 0; p < 12; p++) {
            BlockPos path = origin.offset(p, 0, 12);
            BlockState pathBlock = (p % 2 == 0) ? coarseDirt : Blocks.ROOTED_DIRT.defaultBlockState();
            fillFoundation(level, path.below(), coarseDirt, chunkBox);
            setChunkSafeBlock(level, path, pathBlock, chunkBox);
        }

        for (int z = 13; z <= 15; z++) {
            BlockPos approach = origin.offset(6, 0, z);
            fillFoundation(level, approach.below(), coarseDirt, chunkBox);
            setChunkSafeBlock(level, approach, (z % 2 == 0) ? coarseDirt : Blocks.ROOTED_DIRT.defaultBlockState(), chunkBox);
        }

        BlockPos gateCenter = origin.offset(6, 1, 16);
        BlockPos gateWest = gateCenter.relative(Direction.WEST);
        BlockPos gateEast = gateCenter.relative(Direction.EAST);

        setChunkSafeBlock(level, gateCenter.below(), cobble, chunkBox);
        setChunkSafeBlock(level, gateWest.below(), cobble, chunkBox);
        setChunkSafeBlock(level, gateEast.below(), cobble, chunkBox);
        fillFoundation(level, gateCenter.below(2), cobble, chunkBox);
        fillFoundation(level, gateWest.below(2), cobble, chunkBox);
        fillFoundation(level, gateEast.below(2), cobble, chunkBox);

        setChunkSafeBlock(level, gateCenter, ModBlocks.DEADWOOD_FENCE_GATE.get().defaultBlockState(), chunkBox);
        setFence(level, gateWest, chunkBox, fences);
        setFence(level, gateWest.above(), chunkBox, fences);
        setFence(level, gateEast, chunkBox, fences);
        setFence(level, gateEast.above(), chunkBox, fences);

        setChunkSafeBlock(level, gateCenter.above(2), slab, chunkBox);
        setChunkSafeBlock(level, gateWest.above(2), slab, chunkBox);
        setChunkSafeBlock(level, gateEast.above(2), slab, chunkBox);
        setChunkSafeBlock(level, gateCenter.above(3), ModBlocks.CARNIVAL_LANTERN.get().defaultBlockState().setValue(LanternBlock.HANGING, Boolean.TRUE), chunkBox);

        connectAllFences(level, fences, chunkBox);
    }
}