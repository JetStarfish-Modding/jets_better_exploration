package dev.hugeblank.jbe;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class ClientInit implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ModelPredicateProviderRegistry.register(MainInit.SCULK_VIAL, new Identifier("experience"), (itemStack, clientWorld, livingEntity, seed) -> {
			NbtCompound nbt = itemStack.getNbt();
			return nbt == null || !nbt.contains("experience") ? 0 : (float) nbt.getInt("experience") /30;
		});
	}
}