package me.towdium.jecalculation.utils.forge;

import dev.architectury.fluid.FluidStack;
import me.towdium.jecalculation.data.structure.RecordPlayer;
import me.towdium.jecalculation.forge.JecaCapability;
import me.towdium.jecalculation.forge.JecaConfig;
import net.minecraft.world.entity.player.Player;

public class UtilitiesImpl {
    public static RecordPlayer getRecord(Player player) {
        return JecaCapability.getRecord(player);
    }

    public static boolean isClientMode() {
        return JecaConfig.clientMode.get();
    }

    public static FluidStack createFluidStackFromJeiIngredient(Object object) {
        if (object instanceof net.neoforged.neoforge.fluids.FluidStack fluidStack) {
            return FluidStack.create(fluidStack.getFluidHolder(), fluidStack.getAmount(), fluidStack.getComponentsPatch());
        }
        return null;
    }
}
