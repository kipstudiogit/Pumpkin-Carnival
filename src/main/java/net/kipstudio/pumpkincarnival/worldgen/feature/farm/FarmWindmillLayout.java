// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.worldgen.feature.farm;

import net.kipstudio.pumpkincarnival.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Half;

public class FarmWindmillLayout implements FarmLayout {
    @Override
    public void build(FarmBuildContext ctx, BlockPos center) {
        buildWindmillTower(ctx, center.offset(3, 0, -5));
        buildWitchCottage(ctx, center.offset(-8, 0, -2));
        FarmYardProps.buildPumpkinMounds(ctx, center.offset(2, 0, 3));
        FarmYardProps.buildPerimeterAndGate(ctx, center);
    }

    private static void buildWindmillTower(FarmBuildContext ctx, BlockPos origin) {
        BlockState cobble = Blocks.COBBLESTONE.defaultBlockState();
        BlockState planks = ModBlocks.DEADWOOD_PLANKS.get().defaultBlockState();
        BlockState log = ModBlocks.DEADWOOD_LOG.get().defaultBlockState();

        int radius = 2;
        int height = 9;

        ctx.clearAir(origin.offset(-radius + 1, 1, -radius + 1), origin.offset(radius - 1, height, radius - 1));

        for (int y = 0; y <= 4; y++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (dx * dx + dz * dz > radius * radius + 1) {
                        continue;
                    }
                    BlockPos pos = origin.offset(dx, y, dz);
                    if (y == 0) {
                        ctx.fillFoundation(pos.below(), cobble);
                    }
                    if (Math.abs(dx) == radius || Math.abs(dz) == radius) {
                        ctx.setBlock(pos, cobble);
                    } else if (y == 0) {
                        ctx.setBlock(pos, planks);
                    }
                }
            }
        }

        for (int y = 5; y <= height; y++) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (Math.abs(dx) == 1 || Math.abs(dz) == 1) {
                        ctx.setBlock(origin.offset(dx, y, dz), planks);
                    }
                }
            }
        }

        BlockPos hub = origin.offset(0, height - 1, -2);
        ctx.setBlock(hub, log.setValue(RotatedPillarBlock.AXIS, Direction.Axis.Z));

        for (Direction dir : Direction.Plane.VERTICAL) {
            for (int s = 1; s <= 3; s++) {
                ctx.setFence(hub.relative(dir, s));
            }
        }
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            if (dir.getAxis() == Direction.Axis.X) {
                for (int s = 1; s <= 3; s++) {
                    ctx.setFence(hub.relative(dir, s));
                }
            }
        }

        ctx.setBlock(origin.offset(0, 1, 0), Blocks.GRINDSTONE.defaultBlockState());
        ctx.setBlock(origin.offset(1, 1, 0), Blocks.HAY_BLOCK.defaultBlockState());
    }

    private static void buildWitchCottage(FarmBuildContext ctx, BlockPos origin) {
        BlockState planks = ModBlocks.DEADWOOD_PLANKS.get().defaultBlockState();
        BlockState stairs = ModBlocks.DEADWOOD_STAIRS.get().defaultBlockState();
        BlockState slab = ModBlocks.DEADWOOD_SLAB.get().defaultBlockState();
        BlockState cobble = Blocks.COBBLESTONE.defaultBlockState();

        int width = 6;
        int depth = 6;
        int height = 4;

        ctx.clearAir(origin.offset(1, 1, 1), origin.offset(width - 2, height + 3, depth - 2));

        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                BlockPos floor = origin.offset(x, 0, z);
                ctx.fillFoundation(floor.below(), cobble);
                ctx.setBlock(floor, planks);
            }
        }

        for (int y = 1; y <= height; y++) {
            for (int x = 0; x < width; x++) {
                for (int z = 0; z < depth; z++) {
                    boolean isWall = x == 0 || x == width - 1 || z == 0 || z == depth - 1;
                    if (isWall && !(x == width - 1 && z == 3 && y <= 2)) {
                        ctx.setBlock(origin.offset(x, y, z), planks);
                    }
                }
            }
        }

        BlockPos doorPos = origin.offset(width - 1, 1, 3);
        ctx.setBlock(doorPos, ModBlocks.DEADWOOD_DOOR.get().defaultBlockState()
                .setValue(DoorBlock.FACING, Direction.EAST)
                .setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER)
                .setValue(DoorBlock.HINGE, DoorHingeSide.LEFT));
        ctx.setBlock(doorPos.above(), ModBlocks.DEADWOOD_DOOR.get().defaultBlockState()
                .setValue(DoorBlock.FACING, Direction.EAST)
                .setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER)
                .setValue(DoorBlock.HINGE, DoorHingeSide.LEFT));

        for (int z = -1; z <= depth; z++) {
            boolean isGable = z == 0 || z == depth - 1;
            for (int r = -1; r <= 3; r++) {
                BlockPos left = origin.offset(r, height + 1 + r, z);
                BlockPos right = origin.offset(width - 1 - r, height + 1 + r, z);
                if (r == 3) {
                    ctx.setBlock(left, slab);
                    ctx.setBlock(right, slab);
                } else {
                    ctx.setBlock(left, stairs.setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.BOTTOM));
                    ctx.setBlock(right, stairs.setValue(StairBlock.FACING, Direction.WEST).setValue(StairBlock.HALF, Half.BOTTOM));
                    if (isGable && r >= 1) {
                        for (int gx = r + 1; gx <= width - 2 - r; gx++) {
                            ctx.setBlock(origin.offset(gx, height + 1 + r, z), planks);
                        }
                    }
                }
            }
        }

        ctx.setBlock(origin.offset(2, 1, 2), Blocks.BREWING_STAND.defaultBlockState());
        ctx.setBlock(origin.offset(2, 1, 3), Blocks.CAULDRON.defaultBlockState());
        ctx.setBlock(origin.offset(1, 1, 1), Blocks.BOOKSHELF.defaultBlockState());
    }
}