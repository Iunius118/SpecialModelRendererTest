package com.example.specialmodelrenderertes;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpecialModelRendererTest implements ModInitializer {
	public static final String MOD_ID = "specialmodelrenderertest";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");

		// Register the mod's items
		ModItems.register();
		ModCreativeModeTabs.register();
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}

	public static class ModItems {
		public static final Item EXAMPLE_ITEM = registerItem("example_item");

		private static ResourceKey<Item> modItemId(String name) {
			return ResourceKey.create(Registries.ITEM, id(name));
		}

		private static Item registerItem(String name) {
			return Items.registerItem(modItemId(name), Item::new, new Item.Properties());
		}

		public static void register() {
			LOGGER.debug("Register mod items");
		}
	}

	public static class ModCreativeModeTabs {
		public static final CreativeModeTab EXAMPLE_TAB = FabricItemGroup.builder().title(Component.literal("Example Tab"))
				.icon(ModItems.EXAMPLE_ITEM::getDefaultInstance)
				.displayItems((featureFlagSet, output) -> output.accept(ModItems.EXAMPLE_ITEM))
				.build();

		public static void register() {
			ResourceKey<CreativeModeTab> key = ResourceKey.create(Registries.CREATIVE_MODE_TAB, id("example_tab"));
			Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, key, EXAMPLE_TAB);
		}
	}
}