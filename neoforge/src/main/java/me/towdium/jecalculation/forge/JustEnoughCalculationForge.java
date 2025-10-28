package me.towdium.jecalculation.forge;

import dev.architectury.platform.Platform;
import me.towdium.jecalculation.JecaCommand;
import me.towdium.jecalculation.JustEnoughCalculation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

@Mod(JustEnoughCalculation.MODID)
@Mod.EventBusSubscriber(modid = JustEnoughCalculation.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class JustEnoughCalculationForge {
    public JustEnoughCalculationForge() {
        //noinspection InstantiationOfUtilityClass
        new JustEnoughCalculation();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, JecaConfig.common, Platform.getConfigFolder().resolve(JecaConfig.PATH).toString());
    }

    @SubscribeEvent
    public static void registerClientCommands(RegisterClientCommandsEvent event) {
        JecaCommand.register(event.getDispatcher());
    }
/*
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onFocus(ScreenEvent.MouseClickedEvent.Pre event) {
        if(JecaGui.onFocused(Minecraft.getInstance(), event.getScreen(), event.getMouseX(), event.getMouseY(), event.getButton()).isPresent())
            event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onMouseLow(ScreenEvent.MouseInputEvent event) {
        if (event instanceof ScreenEvent.MouseScrollEvent.Pre pre) {
            if (JecaGui.onMouseScroll(Minecraft.getInstance(), event.getScreen(), event.getMouseX(), event.getMouseY(), pre.getScrollDelta()).isPresent())
                event.setCanceled(true);
        }
        else if (event instanceof ScreenEvent.MouseClickedEvent.Pre pre) {
            if (JecaGui.onMouseClicked(Minecraft.getInstance(), event.getScreen(), event.getMouseX(), event.getMouseY(), pre.getButton()).isPresent())
                event.setCanceled(true);
        }
        else if (event instanceof ScreenEvent.MouseDragEvent.Pre pre) {
            if (JecaGui.onMouseDragged(Minecraft.getInstance(), event.getScreen(), event.getMouseX(), event.getMouseY(), pre.getMouseButton(), pre.getDragX(), pre.getDragY()).isPresent())
                event.setCanceled(true);
        }
        else if (event instanceof ScreenEvent.MouseReleasedEvent.Pre pre)
            if(JecaGui.onMouseReleased(Minecraft.getInstance(), event.getScreen(), event.getMouseX(), event.getMouseY(), pre.getButton()).isPresent())
                event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onMouseHigh(ScreenEvent.MouseInputEvent event) {
        if (event instanceof ScreenEvent.MouseScrollEvent.Pre pre) {
            if (JustEnoughCalculation.GUI_HANDLER.onMouseScroll(Minecraft.getInstance(), event.getScreen(), event.getMouseX(), event.getMouseY(), pre.getScrollDelta()).isPresent())
                event.setCanceled(true);
        }
        else if (event instanceof ScreenEvent.MouseClickedEvent.Pre pre) {
            if (JustEnoughCalculation.GUI_HANDLER.onMouseClicked(Minecraft.getInstance(), event.getScreen(), event.getMouseX(), event.getMouseY(), pre.getButton()).isPresent())
                event.setCanceled(true);
        }
        else if (event instanceof ScreenEvent.MouseDragEvent.Pre pre) {
            if (JustEnoughCalculation.GUI_HANDLER.onMouseDragged(Minecraft.getInstance(), event.getScreen(), event.getMouseX(), event.getMouseY(), pre.getMouseButton(), pre.getDragX(), pre.getDragY()).isPresent())
                event.setCanceled(true);
        }
        else if (event instanceof ScreenEvent.MouseReleasedEvent.Pre pre)
            if(JustEnoughCalculation.GUI_HANDLER.onMouseReleased(Minecraft.getInstance(), event.getScreen(), event.getMouseX(), event.getMouseY(), pre.getButton()).isPresent())
                event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onDrawForeground(ScreenEvent.DrawScreenEvent.Post event){
        JustEnoughCalculation.GUI_HANDLER.onDrawForeground(event.getScreen(), event.getPoseStack(), event.getMouseX(), event.getMouseY(), event.getPartialTicks());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onRenderTooltip(RenderTooltipEvent.Pre event){
        if(JustEnoughCalculation.GUI_HANDLER.onTooltip(event.getPoseStack(), event.getComponents(), event.getX(), event.getY()).isPresent())
            event.setCanceled(true);
    }

 */

}
