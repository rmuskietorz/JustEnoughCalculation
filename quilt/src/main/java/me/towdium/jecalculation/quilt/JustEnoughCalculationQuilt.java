package me.towdium.jecalculation.quilt;

import me.towdium.jecalculation.fabric_like.JustEnoughCalculationFabricLike;
import net.fabricmc.api.ModInitializer;

public class JustEnoughCalculationQuilt implements ModInitializer {
    @Override
    public void onInitialize() {
        new JustEnoughCalculationFabricLike();
    }
}
