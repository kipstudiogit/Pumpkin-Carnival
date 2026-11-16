// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.init;

import net.kipstudio.pumpkincarnival.PumpkinCarnival;
import net.kipstudio.pumpkincarnival.worldgen.structure.piece.PlantationCourtyardPiece;
import net.kipstudio.pumpkincarnival.worldgen.structure.piece.PlantationFieldsPiece;
import net.kipstudio.pumpkincarnival.worldgen.structure.piece.PlantationManorPiece;
import net.kipstudio.pumpkincarnival.worldgen.structure.piece.PlantationMillPiece;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModStructurePieceTypes {
    public static final DeferredRegister<StructurePieceType> PIECE_TYPES =
            DeferredRegister.create(Registries.STRUCTURE_PIECE, PumpkinCarnival.MOD_ID);

    public static final RegistryObject<StructurePieceType> MANOR =
            PIECE_TYPES.register("plantation_manor", () -> PlantationManorPiece::new);

    public static final RegistryObject<StructurePieceType> MILL =
            PIECE_TYPES.register("plantation_mill", () -> PlantationMillPiece::new);

    public static final RegistryObject<StructurePieceType> FIELDS =
            PIECE_TYPES.register("plantation_fields", () -> PlantationFieldsPiece::new);

    public static final RegistryObject<StructurePieceType> COURTYARD =
            PIECE_TYPES.register("plantation_courtyard", () -> PlantationCourtyardPiece::new);

    private ModStructurePieceTypes() {
    }

    public static void register(IEventBus eventBus) {
        PIECE_TYPES.register(eventBus);
    }
}