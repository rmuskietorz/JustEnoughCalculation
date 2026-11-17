package me.towdium.jecalculation.events;

import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientScreenInputEvent;
import dev.architectury.event.events.client.ClientTooltipEvent;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.utils.wrappers.Trio;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.DeltaTracker;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static dev.architectury.event.EventResult.interruptFalse;
import static dev.architectury.event.EventResult.pass;

@Environment(EnvType.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class GuiScreenEventHandler {

    protected GuiScreenOverlayHandler overlayHandler = null;
    protected JecaGui gui = null;
    protected InventorySummary cachedInventory;
    protected Trio<List<? extends ClientTooltipComponent>, Integer, Integer> cachedTooltipEvent;

    public GuiScreenEventHandler() {
        registerEvents();
    }

    private void registerEvents() {
        ClientGuiEvent.SET_SCREEN.register(this::onGuiOpen);
        ClientTooltipEvent.RENDER_PRE.register(this::onTooltip);
        ClientGuiEvent.RENDER_POST.register(this::onDrawForeground);
        ClientScreenInputEvent.MOUSE_SCROLLED_PRE.register(this::onMouseScroll);
        ClientScreenInputEvent.MOUSE_CLICKED_PRE.register(this::onMouseClicked);
        ClientScreenInputEvent.MOUSE_DRAGGED_PRE.register(this::onMouseDragged);
        ClientScreenInputEvent.MOUSE_RELEASED_PRE.register(this::onMouseReleased);
        ClientScreenInputEvent.KEY_PRESSED_PRE.register(this::onKeyPressed);
        ClientScreenInputEvent.KEY_RELEASED_PRE.register(this::onKeyReleased);
        ClientScreenInputEvent.CHAR_TYPED_PRE.register(this::onCharTyped);
    }

    public CompoundEventResult<Screen> onGuiOpen(Screen screen) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || !(screen instanceof AbstractContainerScreen)) {
            return CompoundEventResult.pass();
        }

        overlayHandler = new GuiScreenOverlayHandler(player.getInventory());
        gui = new JecaGui(null, false, overlayHandler, true);
        gui.init(Minecraft.getInstance(), screen.width, screen.height);
        overlayHandler.setGui(gui);
        return CompoundEventResult.pass();
    }

    protected boolean isScreenValidForOverlay(Screen screen) {
        return screen instanceof AbstractContainerScreen
                && !(screen instanceof JecaGui);
    }

    public void onDrawForeground(Screen screen, GuiGraphics graphics, int mouseX, int mouseY, DeltaTracker delta) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || overlayHandler == null || !isScreenValidForOverlay(screen)) {
            return;
        }

        Inventory inventory = player.getInventory();
        if (didInventoryChange(inventory)) {
            overlayHandler = new GuiScreenOverlayHandler(inventory);
            gui = new JecaGui(null, false, overlayHandler, true);
            gui.init(Minecraft.getInstance(), screen.width, screen.height);
            overlayHandler.setGui(gui);
        } else if (screen.width != gui.width || screen.height != gui.height) {
            gui.init(Minecraft.getInstance(), screen.width, screen.height);
        }

        gui.setGraphics(graphics);
        int absoluteMouseX = mouseX;
        int absoluteMouseY = mouseY;
        int relativeMouseX = absoluteMouseX - gui.getGuiLeft();
        int relativeMouseY = absoluteMouseY - gui.getGuiTop();

        var poseStack = graphics.pose();
        poseStack.pushPose();
        poseStack.translate(gui.getGuiLeft(), gui.getGuiTop(), 0);
        overlayHandler.onDraw(gui, relativeMouseX, relativeMouseY);
        poseStack.popPose();

        List<String> tooltip = new ArrayList<>();
        overlayHandler.onTooltip(gui, relativeMouseX, relativeMouseY, tooltip);
        gui.drawHoveringText(graphics, tooltip, absoluteMouseX, absoluteMouseY, minecraft.font);
        if (cachedTooltipEvent != null) {
            graphics.renderTooltipInternal(minecraft.font, (List<ClientTooltipComponent>) cachedTooltipEvent.one, cachedTooltipEvent.two, cachedTooltipEvent.three, DefaultTooltipPositioner.INSTANCE);
            cachedTooltipEvent = null;
        }
    }

    public EventResult onTooltip(GuiGraphics guiGraphics, List<? extends ClientTooltipComponent> components, int x, int y) {
        if (overlayHandler == null || cachedTooltipEvent != null || !overlayHandler.hasAnyWindow())
            return pass();

        boolean overlap = overlayHandler.onTooltip(gui, x - gui.getGuiLeft(), y - gui.getGuiTop(), new ArrayList<>());
        if (!overlap)
            cachedTooltipEvent = new Trio<>(components, x, y);
        return interruptFalse();
    }

    public EventResult onMouseScroll(Minecraft client, Screen screen, double mouseX, double mouseY, double amountX, double amountY) {
        if (overlayHandler == null || !isScreenValidForOverlay(screen))
            return pass();
        int relativeMouseX = toLocalMouseX(mouseX);
        int relativeMouseY = toLocalMouseY(mouseY);
        return amountY != 0 && overlayHandler.onMouseScroll(gui, relativeMouseX, relativeMouseY, (int) amountY) ? interruptFalse() : pass();
    }

    public EventResult onMouseClicked(Minecraft client, Screen screen, double mouseX, double mouseY, int button) {
        if (overlayHandler == null || !isScreenValidForOverlay(screen))
            return pass();
        int xMouse = toLocalMouseX(mouseX);
        int yMouse = toLocalMouseY(mouseY);
        overlayHandler.onMouseFocused(gui, xMouse, yMouse, button);
        return overlayHandler.onMouseClicked(gui, xMouse, yMouse, button) ? interruptFalse() : pass();
    }

    public EventResult onMouseReleased(Minecraft minecraft, Screen screen, double mouseX, double mouseY, int button) {
        if (overlayHandler == null || !isScreenValidForOverlay(screen))
            return pass();
        int xMouse = toLocalMouseX(mouseX);
        int yMouse = toLocalMouseY(mouseY);
        return overlayHandler.onMouseReleased(gui, xMouse, yMouse, button) ? interruptFalse() : pass();
    }

    public EventResult onMouseDragged(Minecraft client, Screen screen, double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (overlayHandler == null || !isScreenValidForOverlay(screen))
            return pass();
        int xMouse = toLocalMouseX(mouseX);
        int yMouse = toLocalMouseY(mouseY);
        return overlayHandler.onMouseDragged(gui, xMouse, yMouse, (int) deltaX, (int) deltaY) ? interruptFalse() : pass();
    }

    public EventResult onKeyPressed(Minecraft client, Screen screen, int keyCode, int scanCode, int modifiers) {
        if (overlayHandler == null || !isScreenValidForOverlay(screen))
            return pass();
        return overlayHandler.onKeyPressed(gui, keyCode, modifiers) ? interruptFalse() : pass();
    }

    public EventResult onKeyReleased(Minecraft client, Screen screen, int keyCode, int scanCode, int modifiers) {
        if (overlayHandler == null || !isScreenValidForOverlay(screen))
            return pass();
        return overlayHandler.onKeyReleased(gui, keyCode, modifiers) ? interruptFalse() : pass();
    }

    public EventResult onCharTyped(Minecraft minecraft, Screen screen, char character, int modifiers) {
        if (overlayHandler == null || !isScreenValidForOverlay(screen))
            return pass();
        return overlayHandler.onChar(gui, character, modifiers) ? interruptFalse() : pass();
    }

    private int toLocalMouseX(double mouseX) {
        return gui == null ? 0 : (int) (mouseX - gui.getGuiLeft());
    }

    private int toLocalMouseY(double mouseY) {
        return gui == null ? 0 : (int) (mouseY - gui.getGuiTop());
    }

    private boolean didInventoryChange(Inventory inventory) {
        if (cachedInventory == null) {
            cachedInventory = new InventorySummary(inventory);
            return false;
        }

        InventorySummary newSummery = new InventorySummary(inventory);
        if (newSummery.equals(cachedInventory)) {
            return false;
        }

        cachedInventory = newSummery;
        return true;
    }

    public List<Rect2i> getGuiAreas() {
        if (overlayHandler != null && gui != null && isScreenValidForOverlay(Minecraft.getInstance().screen)) {
            return overlayHandler.getGuiExtraAreas(gui.getGuiLeft(), gui.getGuiTop());
        }
        return Collections.emptyList();
    }
}
