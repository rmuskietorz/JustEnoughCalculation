package me.towdium.jecalculation.network.packets;

import dev.architectury.networking.NetworkManager;
import me.towdium.jecalculation.JecaItem;
import me.towdium.jecalculation.JustEnoughCalculation;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public record PCalculator(ItemStack stack, int slot) implements CustomPacketPayload {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(JustEnoughCalculation.MODID, "calculator");
    public static final Type<PCalculator> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, PCalculator> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC, PCalculator::stack,
            ByteBufCodecs.VAR_INT, PCalculator::slot,
            PCalculator::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(NetworkManager.PacketContext ctx) {
        ctx.queue(() -> {
            Inventory inventory = Objects.requireNonNull(ctx.getPlayer()).getInventory();
            ItemStack calculator = inventory.getItem(slot);
            if (!calculator.isEmpty() && calculator.getItem() instanceof JecaItem) {
                inventory.setItem(slot, stack);
                return;
            }
            calculator = inventory.offhand.get(0);
            if (!calculator.isEmpty() && calculator.getItem() instanceof JecaItem) {
                inventory.offhand.set(0, stack);
            }
        });
    }
}
