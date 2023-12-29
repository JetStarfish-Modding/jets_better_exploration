package dev.hugeblank.jbe.network;

import dev.hugeblank.jbe.MainInit;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class JbeStateChangeS2CPacket implements FabricPacket {
    public static final PacketType<JbeStateChangeS2CPacket> TYPE = PacketType.create(new Identifier(MainInit.ID, "state_change"), JbeStateChangeS2CPacket::new);
    public static final int ALLOW_ICE_BOAT_SPEED = 0;
    public static final int HORSE_STAMINA = 1;

    private final int reason;
    private final float value;

    public JbeStateChangeS2CPacket(int reason, float value) {
        this.reason = reason;
        this.value = value;
    }

    protected JbeStateChangeS2CPacket(PacketByteBuf buf) {
        this.reason = buf.readUnsignedByte();
        this.value = buf.readFloat();
    }

    public int getReason() {
        return reason;
    }

    public float getValue() {
        return value;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeByte(this.reason);
        buf.writeFloat(this.value);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
