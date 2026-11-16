// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.worldgen.feature.farm;

import net.kipstudio.pumpkincarnival.init.ModBlocks;
import net.kipstudio.pumpkincarnival.init.ModLootTables;
import net.kipstudio.pumpkincarnival.worldgen.util.ScarecrowBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Half;

public class FarmManorLayout implements FarmLayout {
    @Override
    public void build(FarmBuildContext ctx, BlockPos center) {
        BlockPos houseOrigin = center.offset(-9, 0, -4);
        buildManorHouse(ctx, houseOrigin);
        buildTerracedPlantation(ctx, center.offset(2, 0, 1));
        FarmYardProps.buildCemetery(ctx, houseOrigin.offset(1, 0, -4));
        FarmYardProps.buildPerimeterAndGate(ctx, center);
    }

    private static void buildManorHouse(FarmBuildContext ctx, BlockPos origin) {
        RandomSource random = ctx.random();
        BlockState cobble = Blocks.COBBLESTONE.defaultBlockState();
        BlockState mossyCobble = Blocks.MOSSY_COBBLESTONE.defaultBlockState();
        BlockState logY = ModBlocks.DEADWOOD_LOG.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, Direction.Axis.Y);
        BlockState logX = ModBlocks.DEADWOOD_LOG.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, Direction.Axis.X);
        BlockState planks = ModBlocks.DEADWOOD_PLANKS.get().defaultBlockState();
        BlockState woodStairs = ModBlocks.DEADWOOD_STAIRS.get().defaultBlockState();
        BlockState stoneStairs = Blocks.MOSSY_COBBLESTONE_STAIRS.defaultBlockState();
        BlockState slab = ModBlocks.DEADWOOD_SLAB.get().defaultBlockState();

        int width = 8;
        int depth = 7;
        int wallHeight = 5;

        ctx.clearAir(origin.offset(1, 1, 1), origin.offset(width - 2, wallHeight + 4, depth - 2));

        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                BlockPos floor = origin.offset(x, 0, z);
                ctx.fillFoundation(floor.below(), random.nextBoolean() ? cobble : mossyCobble);
                ctx.setBlock(floor, random.nextBoolean() ? cobble : mossyCobble);
            }
        }

        for (int y = 1; y <= wallHeight; y++) {
            for (int x = 0; x < width; x++) {
                for (int z = 0; z < depth; z++) {
                    boolean isCorner = (x == 0 || x == width - 1) && (z == 0 || z == depth - 1);
                    boolean isWall = x == 0 || x == width - 1 || z == 0 || z == depth - 1;
                    if (!isWall) {
                        continue;
                    }

                    BlockPos pos = origin.offset(x, y, z);
                    if (isCorner) {
                        ctx.setBlock(pos, logY);
                    } else if (y == 2 && ((x == 4 && (z == 0 || z == depth - 1)) || (z == 3 && x == 0))) {
                        ctx.setFence(pos);
                    } else if (!(x == width - 1 && z == 3 && y <= 2)) {
                        BlockState wallBlock = random.nextInt(10) == 0
                                ? ModBlocks.STRIPPED_DEADWOOD_LOG.get().defaultBlockState()
                                : planks;
                        ctx.setBlock(pos, wallBlock);
                    }
                }
            }
        }

        for (int x = 0; x < width; x++) {
            ctx.setBlock(origin.offset(x, 3, 0), logX);
            ctx.setBlock(origin.offset(x, 3, depth - 1), logX);
        }

        buildPorch(ctx, origin, width, planks, cobble);

        BlockPos doorBottom = origin.offset(width - 1, 1, 3);
        ctx.setBlock(doorBottom, ModBlocks.DEADWOOD_DOOR.get().defaultBlockState()
                .setValue(DoorBlock.FACING, Direction.EAST)
                .setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER)
                .setValue(DoorBlock.HINGE, DoorHingeSide.LEFT));
        ctx.setBlock(doorBottom.above(), ModBlocks.DEADWOOD_DOOR.get().defaultBlockState()
                .setValue(DoorBlock.FACING, Direction.EAST)
                .setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER)
                .setValue(DoorBlock.HINGE, DoorHingeSide.LEFT));

        BlockPos chimney = origin.offset(0, 1, 2);
        for (int cy = 0; cy <= wallHeight + 2; cy++) {
            ctx.setBlock(chimney.above(cy), random.nextBoolean() ? cobble : mossyCobble);
        }
        ctx.setBlock(chimney.above(wallHeight + 3), Blocks.CAMPFIRE.defaultBlockState().setValue(CampfireBlock.LIT, Boolean.FALSE));

        buildRoof(ctx, origin, width, depth, wallHeight, planks, woodStairs, stoneStairs, slab);

        ctx.setLootChest(origin.offset(2, 1, depth - 2), ModLootTables.CHEST_PUMPKIN_FARM);
        ctx.setBlock(origin.offset(1, 1, 2), Blocks.CAULDRON.defaultBlockState());
        ctx.setBlock(origin.offset(4, wallHeight - 1, 3), ModBlocks.CARNIVAL_LANTERN.get().defaultBlockState().setValue(LanternBlock.HANGING, Boolean.TRUE));
        ctx.setBlock(origin.offset(2, wallHeight - 1, 2), Blocks.COBWEB.defaultBlockState());
    }

    private static void buildPorch(FarmBuildContext ctx, BlockPos origin, int width, BlockState planks, BlockState cobble) {
        for (int pz = 1; pz <= 5; pz++) {
            BlockPos porchFloor = origin.offset(width, 0, pz);
            ctx.setBlock(porchFloor, planks);
            ctx.fillFoundation(porchFloor.below(), cobble);

            if (pz == 1 || pz == 5) {
                ctx.setFence(porchFloor.above());
                ctx.setFence(porchFloor.above(2));
                ctx.setBlock(porchFloor.above(3), ModBlocks.CARNIVAL_LANTERN.get().defaultBlockState().setValue(LanternBlock.HANGING, Boolean.TRUE));
            }
        }
    }

    private static void buildRoof(FarmBuildContext ctx, BlockPos origin, int width, int depth, int wallHeight,
                                  BlockState planks, BlockState woodStairs, BlockState stoneStairs, BlockState slab) {
        for (int z = -1; z <= depth; z++) {
            boolean isFascia = z == -1 || z == depth;
            boolean isGable = z == 0 || z == depth - 1;

            for (int r = -1; r <= 4; r++) {
                if (z == 2 && r >= 2) {
                    continue;
                }

                BlockPos leftRoof = origin.offset(r, wallHeight + 1 + r, z);
                BlockPos rightRoof = origin.offset(width - 1 - r, wallHeight + 1 + r, z);

                if (r == 4) {
                    ctx.setBlock(leftRoof, slab);
                    ctx.setBlock(rightRoof, slab);
                } else {
                    BlockState leftStair = (isFascia ? stoneStairs : woodStairs)
                            .setValue(StairBlock.FACING, Direction.EAST)
                            .setValue(StairBlock.HALF, Half.BOTTOM);
                    BlockState rightStair = (isFascia ? stoneStairs : woodStairs)
                            .setValue(StairBlock.FACING, Direction.WEST)
                            .setValue(StairBlock.HALF, Half.BOTTOM);

                    ctx.setBlock(leftRoof, leftStair);
                    ctx.setBlock(rightRoof, rightStair);

                    if (r == -1) {
                        ctx.setBlock(leftRoof.below(), woodStairs.setValue(StairBlock.FACING, Direction.WEST).setValue(StairBlock.HALF, Half.TOP));
                        ctx.setBlock(rightRoof.below(), woodStairs.setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.TOP));
                    } else if (isGable && r >= 1) {
                        for (int gx = r + 1; gx <= width - 2 - r; gx++) {
                            ctx.setBlock(origin.offset(gx, wallHeight + 1 + r, z), planks);
                        }
                    }
                }
            }
        }
    }

    private static void buildTerracedPlantation(FarmBuildContext ctx, BlockPos origin) {
        RandomSource random = ctx.random();
        int width = 7;
        int length = 9;

        BlockState farmland = Blocks.FARMLAND.defaultBlockState().setValue(FarmBlock.MOISTURE, 7);
        BlockState water = Blocks.WATER.defaultBlockState();
        BlockState trapdoor = ModBlocks.DEADWOOD_TRAPDOOR.get().defaultBlockState();
        BlockState cobble = Blocks.COBBLESTONE.defaultBlockState();

        for (int x = -1; x <= width; x++) {
            for (int z = -1; z <= length; z++) {
                boolean isEdge = x == -1 || x == width || z == -1 || z == length;
                BlockPos pos = origin.offset(x, 0, z);
                if (isEdge) {
                    ctx.setBlock(pos, cobble);
                    ctx.fillFoundation(pos.below(), cobble);
                } else if (x == 3) {
                    ctx.setBlock(pos, water);
                    ctx.setBlock(pos.above(), trapdoor);
                    ctx.fillFoundation(pos.below(), Blocks.DIRT.defaultBlockState());
                } else {
                    ctx.setBlock(pos, farmland);
                    ctx.fillFoundation(pos.below(), Blocks.DIRT.defaultBlockState());

                    BlockPos plantPos = pos.above();
                    if (x == 4 && z == 5) {
                        ScarecrowBuilder.build(ctx.level(), plantPos, Direction.EAST, Direction.NORTH, true, ctx::isInChunkRegion);
                        ctx.registerFence(plantPos);
                        ctx.registerFence(plantPos.above());
                        ctx.registerFence(plantPos.above().relative(Direction.EAST));
                        ctx.registerFence(plantPos.above().relative(Direction.WEST));
                    } else if (random.nextInt(3) == 0) {
                        BlockState pumpkin = random.nextBoolean() ? Blocks.CARVED_PUMPKIN.defaultBlockState() : Blocks.PUMPKIN.defaultBlockState();
                        ctx.setBlock(plantPos, pumpkin);
                    } else if (random.nextInt(4) != 0) {
                        ctx.setBlock(plantPos, Blocks.PUMPKIN_STEM.defaultBlockState());
                    }
                }
            }
        }
    }
}