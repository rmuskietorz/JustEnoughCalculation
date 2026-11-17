package me.towdium.jecalculation.utils.fabric;

import dev.architectury.fluid.FluidStack;
import me.towdium.jecalculation.data.structure.RecordPlayer;
import me.towdium.jecalculation.fabric_like.JecaConfig;
import me.towdium.jecalculation.fabric_like.JecaPlayerRecordAccessor;
import mezz.jei.api.fabric.ingredients.fluids.IJeiFluidIngredient;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.world.entity.player.Player;

public class UtilitiesImpl {
    public static RecordPlayer getRecord(Player player) {
        return player instanceof JecaPlayerRecordAccessor accessor ? accessor.Jeca_getRecord() : null;
    }

    public static boolean isClientMode() {
        return JecaConfig.clientMode;
    }

    public static FluidStack createFluidStackFromJeiIngredient(Object object) {
        if (object instanceof IJeiFluidIngredient fluid) {
            FluidVariant variant = fluid.getFluidVariant();
            return FluidStack.create(variant.getRegistryEntry(), fluid.getAmount(), variant.getComponents());
        }
        return null;
    }
}
