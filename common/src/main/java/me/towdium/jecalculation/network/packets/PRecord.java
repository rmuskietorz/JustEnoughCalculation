package me.towdium.jecalculation.network.packets;

import dev.architectury.networking.NetworkManager;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.data.Controller;
import me.towdium.jecalculation.data.label.labels.LPlaceholder;
import me.towdium.jecalculation.data.structure.RecordPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public record PRecord(RecordPlayer record) implements CustomPacketPayload {
    public static final String KEY_RECIPES = "recipes";
    public static final String KEY_LAST = "last";

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(JustEnoughCalculation.MODID, "record");
    public static final Type<PRecord> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, PRecord> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> buf.writeNbt(payload.record.serialize()),
            buf -> {
                CompoundTag tag = Objects.requireNonNull(buf.readNbt());
                LPlaceholder.state = false;
                return new PRecord(new RecordPlayer(tag));
            }
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(NetworkManager.PacketContext ctx) {
        ctx.queue(() -> Controller.setRecordsServer(record));
    }
}
