// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.worldgen.structure.piece;

import net.kipstudio.pumpkincarnival.init.ModBlocks;
import net.kipstudio.pumpkincarnival.init.ModStructurePieceTypes;
import net.kipstudio.pumpkincarnival.worldgen.util.ScarecrowBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;

import java.util.ArrayList;
import java.util.List;

public class PlantationFieldsPiece extends PlantationPiece {
    private static final int WIDTH = 16;
    private static final int LENGTH = 14;

    public PlantationFieldsPiece(BoundingBox box, BlockPos origin) {
        super(ModStructurePieceTypes.FIELDS.get(), 0, box, origin);
    }

    public PlantationFieldsPiece(StructurePieceSerializationContext context, CompoundTag tag) {
        super(ModStructurePieceTypes.FIELDS.get(), tag);
    }

    @Override
    public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator, RandomSource random, BoundingBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        BlockState farmland = Blocks.FARMLAND.defaultBlockState().setValue(FarmBlock.MOISTURE, 7);
        BlockState water = Blocks.WATER.defaultBlockState();
        BlockState trapdoor = ModBlocks.DEADWOOD_TRAPDOOR.get().defaultBlockState();
        BlockState cobble = Blocks.COBBLESTONE.defaultBlockState();
        BlockState mossy = Blocks.MOSSY_COBBLESTONE.defaultBlockState();

        List<BlockPos> fences = new ArrayList<>();

        for (int x = -1; x <= WIDTH; x++) {
            for (int z = -1; z <= LENGTH; z++) {
                boolean isBorder = x == -1 || x == WIDTH || z == -1 || z == LENGTH;
                BlockPos pos = origin.offset(x, 0, z);

                if (isBorder) {
                    BlockState borderStone = random.nextBoolean() ? cobble : mossy;
                    setChunkSafeBlock(level, pos, borderStone, chunkBox);
                    fillFoundation(level, pos.below(), borderStone, chunkBox);
                } else if (x == 5 || x == 11) {
                    setChunkSafeBlock(level, pos, water, chunkBox);
                    setChunkSafeBlock(level, pos.above(), trapdoor, chunkBox);
                    fillFoundation(level, pos.below(), Blocks.DIRT.defaultBlockState(), chunkBox);
                } else {
                    setChunkSafeBlock(level, pos, farmland, chunkBox);
                    fillFoundation(level, pos.below(), Blocks.DIRT.defaultBlockState(), chunkBox);

                    BlockPos plantPos = pos.above();
                    if (x == 8 && z == 7) {
                        ScarecrowBuilder.build(level, plantPos, Direction.EAST, Direction.NORTH, true, chunkBox::isInside);
                        fences.add(plantPos);
                        fences.add(plantPos.above());
                        fences.add(plantPos.above().relative(Direction.EAST));
                        fences.add(plantPos.above().relative(Direction.WEST));
                    } else if (random.nextInt(3) == 0) {
                        BlockState pumpkin = random.nextInt(3) == 0
                                ? Blocks.CARVED_PUMPKIN.defaultBlockState()
                                : Blocks.PUMPKIN.defaultBlockState();
                        setChunkSafeBlock(level, plantPos, pumpkin, chunkBox);
                    } else if (random.nextInt(4) != 0) {
                        setChunkSafeBlock(level, plantPos, Blocks.PUMPKIN_STEM.defaultBlockState(), chunkBox);
                    }
                }
            }
        }

        connectAllFences(level, fences, chunkBox);
    }
}