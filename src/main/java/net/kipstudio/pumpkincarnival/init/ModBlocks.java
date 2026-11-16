// Copyright (C) 2026 Kettle Ignition Power Studio (K. I. P. Studio)
// SPDX-License-Identifier: GPL-3.0-only

package net.kipstudio.pumpkincarnival.init;

import net.kipstudio.pumpkincarnival.PumpkinCarnival;
import net.kipstudio.pumpkincarnival.block.CarnivalLanternBlock;
import net.kipstudio.pumpkincarnival.block.FlammableBlock;
import net.kipstudio.pumpkincarnival.block.FlammableRotatedPillarBlock;
import net.kipstudio.pumpkincarnival.worldgen.tree.DeadTreeGrower;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, PumpkinCarnival.MOD_ID);

    public static final RegistryObject<Block> CARNIVAL_LANTERN = registerBlock("carnival_lantern",
            () -> new CarnivalLanternBlock(BlockBehaviour.Properties.of(Material.METAL)
                    .strength(3.5F)
                    .sound(SoundType.LANTERN)
                    .lightLevel(state -> 15)
                    .noOcclusion()));

    public static final RegistryObject<Block> DEADWOOD_LOG = registerBlock("deadwood_log",
            () -> new FlammableRotatedPillarBlock(BlockBehaviour.Properties.of(Material.WOOD)
                    .strength(2.0F)
                    .sound(SoundType.WOOD), 5, 5));

    public static final RegistryObject<Block> STRIPPED_DEADWOOD_LOG = registerBlock("stripped_deadwood_log",
            () -> new FlammableRotatedPillarBlock(BlockBehaviour.Properties.of(Material.WOOD)
                    .strength(2.0F)
                    .sound(SoundType.WOOD), 5, 5));

    public static final RegistryObject<Block> DEADWOOD_WOOD = registerBlock("deadwood_wood",
            () -> new FlammableRotatedPillarBlock(BlockBehaviour.Properties.of(Material.WOOD)
                    .strength(2.0F)
                    .sound(SoundType.WOOD), 5, 5));

    public static final RegistryObject<Block> STRIPPED_DEADWOOD_WOOD = registerBlock("stripped_deadwood_wood",
            () -> new FlammableRotatedPillarBlock(BlockBehaviour.Properties.of(Material.WOOD)
                    .strength(2.0F)
                    .sound(SoundType.WOOD), 5, 5));

    public static final RegistryObject<Block> DEADWOOD_PLANKS = registerBlock("deadwood_planks",
            () -> new FlammableBlock(BlockBehaviour.Properties.of(Material.WOOD)
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.WOOD), 20, 5));

    public static final RegistryObject<Block> DEADWOOD_STAIRS = registerBlock("deadwood_stairs",
            () -> new StairBlock(() -> DEADWOOD_PLANKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(DEADWOOD_PLANKS.get())));

    public static final RegistryObject<Block> DEADWOOD_SLAB = registerBlock("deadwood_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(DEADWOOD_PLANKS.get())));

    public static final RegistryObject<Block> DEADWOOD_FENCE = registerBlock("deadwood_fence",
            () -> new FenceBlock(BlockBehaviour.Properties.copy(DEADWOOD_PLANKS.get())));

    public static final RegistryObject<Block> DEADWOOD_FENCE_GATE = registerBlock("deadwood_fence_gate",
            () -> new FenceGateBlock(BlockBehaviour.Properties.copy(DEADWOOD_PLANKS.get()), WoodType.DARK_OAK));

    public static final RegistryObject<Block> DEADWOOD_DOOR = registerBlock("deadwood_door",
            () -> new DoorBlock(BlockBehaviour.Properties.copy(DEADWOOD_PLANKS.get()).noOcclusion(), BlockSetType.DARK_OAK));

    public static final RegistryObject<Block> DEADWOOD_TRAPDOOR = registerBlock("deadwood_trapdoor",
            () -> new TrapDoorBlock(BlockBehaviour.Properties.copy(DEADWOOD_PLANKS.get()).noOcclusion(), BlockSetType.DARK_OAK));

    public static final RegistryObject<Block> DEADWOOD_PRESSURE_PLATE = registerBlock("deadwood_pressure_plate",
            () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING,
                    BlockBehaviour.Properties.of(Material.WOOD).noCollission().strength(0.5F).sound(SoundType.WOOD),
                    BlockSetType.DARK_OAK));

    public static final RegistryObject<Block> DEADWOOD_BUTTON = registerBlock("deadwood_button",
            () -> new ButtonBlock(BlockBehaviour.Properties.of(Material.WOOD).noCollission().strength(0.5F).sound(SoundType.WOOD),
                    BlockSetType.DARK_OAK, 30, true));

    public static final RegistryObject<Block> DEADWOOD_SAPLING = registerBlock("deadwood_sapling",
            () -> new SaplingBlock(new DeadTreeGrower(),
                    BlockBehaviour.Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS)));

    private ModBlocks() {
    }

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> blockSupplier) {
        RegistryObject<T> block = BLOCKS.register(name, blockSupplier);
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}