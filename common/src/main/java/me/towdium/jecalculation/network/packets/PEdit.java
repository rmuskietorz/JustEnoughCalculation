package me.towdium.jecalculation.network.packets;

import dev.architectury.networking.NetworkManager;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.data.structure.Recipe;
import me.towdium.jecalculation.utils.Utilities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Objects;

public record PEdit(String neu, @Nullable String old, int index, @Nullable Recipe recipe) implements CustomPacketPayload {
    private static final String KEY_OLD = "old";
    private static final String KEY_NEW = "new";
    private static final String KEY_INDEX = "index";
    private static final String KEY_RECIPE = "recipe";

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(JustEnoughCalculation.MODID, "edit");
    public static final Type<PEdit> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, PEdit> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> {
                CompoundTag tag = new CompoundTag();
                if (payload.old != null) {
                    tag.putString(KEY_OLD, payload.old);
                }
                tag.putString(KEY_NEW, payload.neu);
                tag.putInt(KEY_INDEX, payload.index);
                if (payload.recipe != null) {
                    tag.put(KEY_RECIPE, payload.recipe.serialize());
                }
                buf.writeNbt(tag);
            },
            buf -> {
                CompoundTag tag = buf.readNbt();
                if (tag == null) {
                    tag = new CompoundTag();
                }
                String old = tag.contains(KEY_OLD) ? tag.getString(KEY_OLD) : null;
                String neu = tag.getString(KEY_NEW);
                int index = tag.getInt(KEY_INDEX);
                Recipe recipe = tag.contains(KEY_RECIPE) ? new Recipe(tag.getCompound(KEY_RECIPE)) : null;
                return new PEdit(neu, old, index, recipe);
            }
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(NetworkManager.PacketContext ctx) {
        ctx.queue(() -> {
            Utilities.getRecord(Objects.requireNonNull(ctx.getPlayer())).recipes
                    .modify(neu, old, index, recipe);
            Utilities.getRecord(Objects.requireNonNull(ctx.getPlayer())).last = old;
        });
    }
}
