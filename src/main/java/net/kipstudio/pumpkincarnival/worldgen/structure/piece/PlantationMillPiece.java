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
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;

import java.util.ArrayList;
import java.util.List;

public class PlantationMillPiece extends PlantationPiece {
    private static final int RADIUS = 3;
    private static final int HEIGHT = 13;

    public PlantationMillPiece(BoundingBox box, BlockPos origin) {
        super(ModStructurePieceTypes.MILL.get(), 0, box, origin);
    }

    public PlantationMillPiece(StructurePieceSerializationContext context, CompoundTag tag) {
        super(ModStructurePieceTypes.MILL.get(), tag);
    }

    @Override
    public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator, RandomSource random, BoundingBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        BlockState cobble = Blocks.COBBLESTONE.defaultBlockState();
        BlockState mossy = Blocks.MOSSY_COBBLESTONE.defaultBlockState();
        BlockState stoneBricks = Blocks.STONE_BRICKS.defaultBlockState();
        BlockState planks = ModBlocks.DEADWOOD_PLANKS.get().defaultBlockState();
        BlockState log = ModBlocks.DEADWOOD_LOG.get().defaultBlockState();

        List<BlockPos> fences = new ArrayList<>();

        clearInteriorAir(level, origin.offset(-RADIUS + 1, 1, -RADIUS + 1), origin.offset(RADIUS - 1, HEIGHT, RADIUS - 1), chunkBox);

        for (int y = 0; y <= 5; y++) {
            for (int dx = -RADIUS; dx <= RADIUS; dx++) {
                for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                    int distSq = dx * dx + dz * dz;
                    if (distSq > RADIUS * RADIUS + 1) {
                        continue;
                    }
                    BlockPos pos = origin.offset(dx, y, dz);
                    if (y == 0) {
                        fillFoundation(level, pos.below(), cobble, chunkBox);
                        setChunkSafeBlock(level, pos, planks, chunkBox);
                    }
                    boolean isOuter = Math.abs(dx) == RADIUS || Math.abs(dz) == RADIUS || distSq >= RADIUS * RADIUS - 1;
                    if (isOuter) {
                        setChunkSafeBlock(level, pos, (y % 2 == 0) ? mossy : stoneBricks, chunkBox);
                    }
                }
            }
        }

        for (int y = 6; y <= HEIGHT; y++) {
            int r = (y >= 11) ? 1 : 2;
            for (int dx = -r; dx <= r; dx++) {
                for (int dz = -r; dz <= r; dz++) {
                    if (Math.abs(dx) == r || Math.abs(dz) == r) {
                        BlockState timber = random.nextInt(6) == 0
                                ? ModBlocks.STRIPPED_DEADWOOD_LOG.get().defaultBlockState()
                                : planks;
                        setChunkSafeBlock(level, origin.offset(dx, y, dz), timber, chunkBox);
                    }
                }
            }
        }

        BlockPos hub = origin.offset(0, HEIGHT - 2, -RADIUS - 1);
        setChunkSafeBlock(level, hub, log.setValue(RotatedPillarBlock.AXIS, Direction.Axis.Z), chunkBox);

        for (Direction dir : Direction.Plane.VERTICAL) {
            for (int s = 1; s <= 4; s++) {
                setFence(level, hub.relative(dir, s), chunkBox, fences);
            }
        }
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            if (dir.getAxis() == Direction.Axis.X) {
                for (int s = 1; s <= 4; s++) {
                    setFence(level, hub.relative(dir, s), chunkBox, fences);
                }
            }
        }

        setChunkSafeBlock(level, hub.below(4), ModBlocks.CARNIVAL_LANTERN.get().defaultBlockState().setValue(LanternBlock.HANGING, Boolean.TRUE), chunkBox);

        setChunkSafeBlock(level, origin.offset(0, 1, 0), Blocks.GRINDSTONE.defaultBlockState(), chunkBox);
        setChunkSafeBlock(level, origin.offset(1, 1, 0), Blocks.HAY_BLOCK.defaultBlockState(), chunkBox);
        setChunkSafeBlock(level, origin.offset(-1, 1, 0), Blocks.BARREL.defaultBlockState(), chunkBox);
        setChunkSafeBlock(level, origin.offset(0, 1, 1), Blocks.CARVED_PUMPKIN.defaultBlockState(), chunkBox);

        connectAllFences(level, fences, chunkBox);
    }
}