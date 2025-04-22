package com.example.specialmodelrenderertes;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.function.BiConsumer;

public class SpecialModelRendererTestDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		final var pack = fabricDataGenerator.createPack();
		pack.addProvider(ModModelProvider::new);
	}

	public static class ModModelProvider extends FabricModelProvider {
		public ModModelProvider(FabricDataOutput output) {
			super(output);
		}

		@Override
		public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
		}

		@Override
		public void generateItemModels(ItemModelGenerators itemModelGenerator) {
			final var itemModelOutput = itemModelGenerator.itemModelOutput;
			final var modelOutput = itemModelGenerator.modelOutput;

			// Add example item model with special model renderer
			Item exampleItem = SpecialModelRendererTest.ModItems.EXAMPLE_ITEM;
			itemModelOutput.accept(exampleItem,
					ItemModelUtils.specialModel(createExampleItemModel(exampleItem, modelOutput), new ExampleItemSpecialRenderer.Unbaked()));
		}

		private ResourceLocation createExampleItemModel(Item item, BiConsumer<ResourceLocation, ModelInstance> modelOutput) {
			ResourceLocation location = ModelLocationUtils.getModelLocation(item, "");
			// Create a JSON object for example item model
			modelOutput.accept(location, () -> {
				var jsonObject = new JsonObject();
				jsonObject.addProperty("parent", "item/generated");
				return jsonObject;
			});
			return location;
		}
	}
}
