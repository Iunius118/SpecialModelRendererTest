package com.example.specialmodelrenderertes;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;

public class SpecialModelRendererTestClient implements ClientModInitializer {
	public static final Logger LOGGER = SpecialModelRendererTest.LOGGER;

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.

		// Register the special model renderer
		ExampleItemSpecialRenderer.register();
	}
}