package dev.hugeblank.jbe;

import dev.hugeblank.jbe.network.JbeStateChangeS2CPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
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

		ClientPlayNetworking.registerGlobalReceiver(JbeStateChangeS2CPacket.TYPE, (packet, player, responseSender) -> {
            if (packet.getReason() == JbeStateChangeS2CPacket.ALLOW_ICE_BOAT_SPEED) {
                player.getWorld().getGameRules().get(MainInit.ALLOW_ICE_BOAT_SPEED).set(packet.getValue() == 1.0F, null);
            }
		});
	}
}