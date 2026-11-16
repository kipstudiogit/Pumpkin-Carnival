// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.worldgen.structure;

import com.mojang.serialization.Codec;
import net.kipstudio.pumpkincarnival.init.ModStructureTypes;
import net.kipstudio.pumpkincarnival.worldgen.structure.piece.PlantationCourtyardPiece;
import net.kipstudio.pumpkincarnival.worldgen.structure.piece.PlantationFieldsPiece;
import net.kipstudio.pumpkincarnival.worldgen.structure.piece.PlantationManorPiece;
import net.kipstudio.pumpkincarnival.worldgen.structure.piece.PlantationMillPiece;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import java.util.Optional;

public class PumpkinPlantationStructure extends Structure {
    public static final Codec<PumpkinPlantationStructure> CODEC = simpleCodec(PumpkinPlantationStructure::new);
    private static final int MAX_SECTOR_ELEVATION_SPREAD = 12;

    public PumpkinPlantationStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        ChunkPos chunkPos = context.chunkPos();
        int originX = chunkPos.getMiddleBlockX();
        int originZ = chunkPos.getMiddleBlockZ();
        int seaLevel = context.chunkGenerator().getSeaLevel();

        int centerSurfaceY = sampleSurfaceHeight(context, originX, originZ);
        if (centerSurfaceY <= seaLevel) {
            return Optional.empty();
        }

        int manorX = originX - 20;
        int manorZ = originZ - 10;
        int manorY = sampleSurfaceHeight(context, manorX + 6, manorZ + 5);

        int millX = originX + 10;
        int millZ = originZ - 18;
        int millY = sampleSurfaceHeight(context, millX, millZ);

        int fieldsX = originX + 6;
        int fieldsZ = originZ + 6;
        int fieldsY = sampleSurfaceHeight(context, fieldsX + 8, fieldsZ + 7);

        int courtX = originX - 18;
        int courtZ = originZ + 8;
        int courtY = sampleSurfaceHeight(context, courtX + 8, courtZ + 8);

        int minY = Math.min(Math.min(manorY, millY), Math.min(fieldsY, courtY));
        int maxY = Math.max(Math.max(manorY, millY), Math.max(fieldsY, courtY));
        if (minY <= seaLevel || maxY - minY > MAX_SECTOR_ELEVATION_SPREAD) {
            return Optional.empty();
        }

        BlockPos center = new BlockPos(originX, centerSurfaceY, originZ);
        return Optional.of(new GenerationStub(center, builder ->
                assembleEstate(builder, manorX, manorY, manorZ, millX, millY, millZ, fieldsX, fieldsY, fieldsZ, courtX, courtY, courtZ)
        ));
    }

    private static void assembleEstate(StructurePiecesBuilder builder,
                                       int mx, int my, int mz,
                                       int lx, int ly, int lz,
                                       int fx, int fy, int fz,
                                       int cx, int cy, int cz) {
        BoundingBox manorBox = new BoundingBox(mx - 1, my - 18, mz - 1, mx + 12, my + 15, mz + 11);
        builder.addPiece(new PlantationManorPiece(manorBox, new BlockPos(mx, my, mz)));

        BoundingBox millBox = new BoundingBox(lx - 4, ly - 14, lz - 4, lx + 4, ly + 15, lz + 3);
        builder.addPiece(new PlantationMillPiece(millBox, new BlockPos(lx, ly, lz)));

        BoundingBox fieldsBox = new BoundingBox(fx - 1, fy - 14, fz - 1, fx + 16, fy + 5, fz + 14);
        builder.addPiece(new PlantationFieldsPiece(fieldsBox, new BlockPos(fx, fy, fz)));

        BoundingBox courtBox = new BoundingBox(cx, cy - 14, cz, cx + 16, cy + 5, cz + 16);
        builder.addPiece(new PlantationCourtyardPiece(courtBox, new BlockPos(cx, cy, cz)));
    }

    private static int sampleSurfaceHeight(GenerationContext context, int x, int z) {
        return context.chunkGenerator().getFirstOccupiedHeight(
                x,
                z,
                Heightmap.Types.WORLD_SURFACE_WG,
                context.heightAccessor(),
                context.randomState()
        );
    }

    @Override
    public StructureType<?> type() {
        return ModStructureTypes.PUMPKIN_PLANTATION.get();
    }
}