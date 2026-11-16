// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.worldgen.structure.piece;

import net.kipstudio.pumpkincarnival.init.ModBlocks;
import net.kipstudio.pumpkincarnival.init.ModLootTables;
import net.kipstudio.pumpkincarnival.init.ModStructurePieceTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;

import java.util.ArrayList;
import java.util.List;

public class PlantationManorPiece extends PlantationPiece {
    private static final int WIDTH = 12;
    private static final int DEPTH = 11;
    private static final int WALL_HEIGHT = 7;

    public PlantationManorPiece(BoundingBox box, BlockPos origin) {
        super(ModStructurePieceTypes.MANOR.get(), 0, box, origin);
    }

    public PlantationManorPiece(StructurePieceSerializationContext context, CompoundTag tag) {
        super(ModStructurePieceTypes.MANOR.get(), tag);
    }

    @Override
    public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator, RandomSource random, BoundingBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        BlockState cobble = Blocks.COBBLESTONE.defaultBlockState();
        BlockState mossyCobble = Blocks.MOSSY_COBBLESTONE.defaultBlockState();
        BlockState stoneBricks = Blocks.STONE_BRICKS.defaultBlockState();
        BlockState crackedBricks = Blocks.CRACKED_STONE_BRICKS.defaultBlockState();
        BlockState logY = ModBlocks.DEADWOOD_LOG.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, Direction.Axis.Y);
        BlockState logX = ModBlocks.DEADWOOD_LOG.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, Direction.Axis.X);
        BlockState planks = ModBlocks.DEADWOOD_PLANKS.get().defaultBlockState();
        BlockState woodStairs = ModBlocks.DEADWOOD_STAIRS.get().defaultBlockState();
        BlockState stoneStairs = Blocks.MOSSY_COBBLESTONE_STAIRS.defaultBlockState();
        BlockState slab = ModBlocks.DEADWOOD_SLAB.get().defaultBlockState();

        List<BlockPos> fences = new ArrayList<>();

        clearInteriorAir(level, origin.offset(1, -2, 1), origin.offset(WIDTH - 2, WALL_HEIGHT + 5, DEPTH - 2), chunkBox);

        buildCellar(level, random, chunkBox, cobble, mossyCobble, stoneBricks, crackedBricks);
        buildGroundAndUpperFloors(level, random, chunkBox, planks, logX, woodStairs);
        buildWalls(level, random, chunkBox, logY, planks, fences);
        buildPorticoAndBalcony(level, chunkBox, cobble, planks, fences);
        buildDoorsAndChimney(level, random, chunkBox, cobble, mossyCobble);
        buildRoof(level, chunkBox, planks, woodStairs, stoneStairs, slab);
        populateInterior(level, random, chunkBox);

        connectAllFences(level, fences, chunkBox);
    }

    private void buildCellar(WorldGenLevel level, RandomSource random, BoundingBox chunkBox,
                             BlockState cobble, BlockState mossyCobble, BlockState stoneBricks, BlockState crackedBricks) {
        for (int x = 0; x < WIDTH; x++) {
            for (int z = 0; z < DEPTH; z++) {
                BlockPos cellarFloor = origin.offset(x, -3, z);
                fillFoundation(level, cellarFloor.below(), cobble, chunkBox);
                setChunkSafeBlock(level, cellarFloor, random.nextBoolean() ? cobble : mossyCobble, chunkBox);

                if (x == 0 || x == WIDTH - 1 || z == 0 || z == DEPTH - 1) {
                    setChunkSafeBlock(level, origin.offset(x, -2, z), stoneBricks, chunkBox);
                    setChunkSafeBlock(level, origin.offset(x, -1, z), stoneBricks, chunkBox);
                    setChunkSafeBlock(level, origin.offset(x, 0, z), crackedBricks, chunkBox);
                }
            }
        }

        BlockState ladder = Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, Direction.SOUTH);
        setChunkSafeBlock(level, origin.offset(1, -2, 1), ladder, chunkBox);
        setChunkSafeBlock(level, origin.offset(1, -1, 1), ladder, chunkBox);

        setChunkSafeBlock(level, origin.offset(2, -2, 2), Blocks.BARREL.defaultBlockState(), chunkBox);
        setChunkSafeBlock(level, origin.offset(2, -2, 3), Blocks.BARREL.defaultBlockState(), chunkBox);
        setChunkSafeBlock(level, origin.offset(WIDTH - 3, -2, 2), Blocks.IRON_BARS.defaultBlockState(), chunkBox);
        setChunkSafeBlock(level, origin.offset(WIDTH - 3, -1, 2), Blocks.IRON_BARS.defaultBlockState(), chunkBox);
    }

    private void buildGroundAndUpperFloors(WorldGenLevel level, RandomSource random, BoundingBox chunkBox,
                                           BlockState planks, BlockState logX, BlockState woodStairs) {
        for (int x = 1; x < WIDTH - 1; x++) {
            for (int z = 1; z < DEPTH - 1; z++) {
                BlockPos floor1 = origin.offset(x, 0, z);
                if (x == 1 && z == 1) {
                    setChunkSafeBlock(level, floor1, ModBlocks.DEADWOOD_TRAPDOOR.get().defaultBlockState(), chunkBox);
                } else {
                    BlockState floorBlock = random.nextInt(8) == 0
                            ? ModBlocks.STRIPPED_DEADWOOD_LOG.get().defaultBlockState()
                            : planks;
                    setChunkSafeBlock(level, floor1, floorBlock, chunkBox);
                }

                boolean isCathedralVoid = x >= 4 && x <= 7 && z >= 3 && z <= 6;
                boolean isStairwell = x >= 5 && x <= 8 && z == 1;
                BlockPos floor2 = origin.offset(x, 4, z);
                if (!isCathedralVoid && !isStairwell) {
                    setChunkSafeBlock(level, floor2, planks, chunkBox);
                } else if (isCathedralVoid && (x == 4 || z == 3)) {
                    setChunkSafeBlock(level, floor2, logX, chunkBox);
                }
            }
        }

        for (int step = 0; step < 4; step++) {
            BlockPos stairPos = origin.offset(5 + step, 1 + step, 1);
            setChunkSafeBlock(level, stairPos, woodStairs.setValue(StairBlock.FACING, Direction.EAST), chunkBox);
        }
    }

    private void buildWalls(WorldGenLevel level, RandomSource random, BoundingBox chunkBox,
                            BlockState logY, BlockState planks, List<BlockPos> fences) {
        for (int y = 1; y <= WALL_HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                for (int z = 0; z < DEPTH; z++) {
                    boolean isWall = x == 0 || x == WIDTH - 1 || z == 0 || z == DEPTH - 1;
                    if (!isWall) {
                        continue;
                    }

                    boolean isCorner = (x == 0 || x == WIDTH - 1) && (z == 0 || z == DEPTH - 1);
                    boolean isPost = isCorner || (y <= 4 && x == 6 && (z == 0 || z == DEPTH - 1));
                    boolean isDoorway = x == WIDTH - 1 && z == 5 && (y <= 2 || y == 5 || y == 6);
                    boolean isWindow = (y == 2 || y == 5) && (x == 3 || x == 8) && (z == 0 || z == DEPTH - 1);

                    BlockPos pos = origin.offset(x, y, z);
                    if (isPost) {
                        setChunkSafeBlock(level, pos, logY, chunkBox);
                    } else if (isWindow) {
                        setFence(level, pos, chunkBox, fences);
                    } else if (!isDoorway) {
                        BlockState wallBlock = random.nextInt(7) == 0
                                ? ModBlocks.STRIPPED_DEADWOOD_LOG.get().defaultBlockState()
                                : planks;
                        setChunkSafeBlock(level, pos, wallBlock, chunkBox);
                    }
                }
            }
        }
    }

    private void buildPorticoAndBalcony(WorldGenLevel level, BoundingBox chunkBox,
                                        BlockState cobble, BlockState planks, List<BlockPos> fences) {
        BlockState hangingLantern = ModBlocks.CARNIVAL_LANTERN.get().defaultBlockState().setValue(LanternBlock.HANGING, Boolean.TRUE);
        for (int pz = 3; pz <= 7; pz++) {
            BlockPos porchBase = origin.offset(WIDTH, 0, pz);
            setChunkSafeBlock(level, porchBase, planks, chunkBox);
            fillFoundation(level, porchBase.below(), cobble, chunkBox);

            if (pz == 3 || pz == 7) {
                for (int py = 1; py <= 3; py++) {
                    setFence(level, porchBase.above(py), chunkBox, fences);
                }
            } else if (pz == 4 || pz == 6) {
                setChunkSafeBlock(level, porchBase.above(3), hangingLantern, chunkBox);
            }

            setChunkSafeBlock(level, porchBase.above(4), planks, chunkBox);
            if (pz != 5) {
                setFence(level, porchBase.above(5), chunkBox, fences);
            }
        }
    }

    private void buildDoorsAndChimney(WorldGenLevel level, RandomSource random, BoundingBox chunkBox,
                                      BlockState cobble, BlockState mossyCobble) {
        placeDoor(level, origin.offset(WIDTH - 1, 1, 5), chunkBox);
        placeDoor(level, origin.offset(WIDTH - 1, 5, 5), chunkBox);

        BlockPos chimneyBase = origin.offset(0, 1, 3);
        for (int cy = 0; cy <= WALL_HEIGHT + 4; cy++) {
            for (int cz = 0; cz <= 2; cz++) {
                setChunkSafeBlock(level, chimneyBase.offset(0, cy, cz), random.nextBoolean() ? cobble : mossyCobble, chunkBox);
            }
        }
        setChunkSafeBlock(level, chimneyBase.offset(1, 0, 1), Blocks.CAMPFIRE.defaultBlockState().setValue(CampfireBlock.LIT, Boolean.FALSE), chunkBox);
        setChunkSafeBlock(level, chimneyBase.offset(0, WALL_HEIGHT + 5, 1), Blocks.CAMPFIRE.defaultBlockState().setValue(CampfireBlock.LIT, Boolean.FALSE), chunkBox);
    }

    private void placeDoor(WorldGenLevel level, BlockPos bottomPos, BoundingBox chunkBox) {
        setChunkSafeBlock(level, bottomPos, ModBlocks.DEADWOOD_DOOR.get().defaultBlockState()
                .setValue(DoorBlock.FACING, Direction.EAST)
                .setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER)
                .setValue(DoorBlock.HINGE, DoorHingeSide.LEFT), chunkBox);
        setChunkSafeBlock(level, bottomPos.above(), ModBlocks.DEADWOOD_DOOR.get().defaultBlockState()
                .setValue(DoorBlock.FACING, Direction.EAST)
                .setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER)
                .setValue(DoorBlock.HINGE, DoorHingeSide.LEFT), chunkBox);
    }

    private void buildRoof(WorldGenLevel level, BoundingBox chunkBox, BlockState planks,
                           BlockState woodStairs, BlockState stoneStairs, BlockState slab) {
        for (int z = -1; z <= DEPTH; z++) {
            boolean isFascia = z == -1 || z == DEPTH;
            boolean isGable = z == 0 || z == DEPTH - 1;

            for (int r = -1; r <= 6; r++) {
                if (z >= 3 && z <= 5 && r >= 3) {
                    continue;
                }

                BlockPos leftRoof = origin.offset(r, WALL_HEIGHT + 1 + r, z);
                BlockPos rightRoof = origin.offset(WIDTH - 1 - r, WALL_HEIGHT + 1 + r, z);

                if (r == 6) {
                    setChunkSafeBlock(level, leftRoof, slab, chunkBox);
                    setChunkSafeBlock(level, rightRoof, slab, chunkBox);
                } else {
                    BlockState leftStair = (isFascia ? stoneStairs : woodStairs).setValue(StairBlock.FACING, Direction.EAST);
                    BlockState rightStair = (isFascia ? stoneStairs : woodStairs).setValue(StairBlock.FACING, Direction.WEST);

                    setChunkSafeBlock(level, leftRoof, leftStair, chunkBox);
                    setChunkSafeBlock(level, rightRoof, rightStair, chunkBox);

                    if (r == -1) {
                        setChunkSafeBlock(level, leftRoof.below(), woodStairs.setValue(StairBlock.FACING, Direction.WEST).setValue(StairBlock.HALF, Half.TOP), chunkBox);
                        setChunkSafeBlock(level, rightRoof.below(), woodStairs.setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.TOP), chunkBox);
                    } else if (isGable) {
                        for (int gx = r + 1; gx <= WIDTH - 2 - r; gx++) {
                            setChunkSafeBlock(level, origin.offset(gx, WALL_HEIGHT + 1 + r, z), planks, chunkBox);
                        }
                    }
                }
            }
        }
    }

    private void populateInterior(WorldGenLevel level, RandomSource random, BoundingBox chunkBox) {
        setLootChest(level, origin.offset(2, 5, DEPTH - 3), random, ModLootTables.CHEST_PLANTATION_MANOR, chunkBox);
        setChunkSafeBlock(level, origin.offset(2, 5, 2), Blocks.BOOKSHELF.defaultBlockState(), chunkBox);
        setChunkSafeBlock(level, origin.offset(3, 5, 2), Blocks.LECTERN.defaultBlockState(), chunkBox);
        setChunkSafeBlock(level, origin.offset(6, WALL_HEIGHT - 1, 5), ModBlocks.CARNIVAL_LANTERN.get().defaultBlockState().setValue(LanternBlock.HANGING, Boolean.TRUE), chunkBox);
    }
}