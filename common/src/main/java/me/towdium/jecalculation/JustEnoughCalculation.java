package me.towdium.jecalculation;

import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import me.towdium.jecalculation.data.Controller;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.data.label.labels.LPlaceholder;
import me.towdium.jecalculation.events.GuiScreenEventHandler;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.network.packets.PCalculator;
import me.towdium.jecalculation.network.packets.PEdit;
import me.towdium.jecalculation.network.packets.PRecord;
import me.towdium.jecalculation.utils.Utilities;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.ParametersAreNonnullByDefault;

import static me.towdium.jecalculation.gui.JecaGui.keyOpenGuiCraft;
import static me.towdium.jecalculation.gui.JecaGui.keyOpenGuiMath;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class JustEnoughCalculation {
    public static final String MODID = "jecalculation";
    public static final String MODNAME = "Just Enough Calculation";
    public static final NetworkHandler network = new NetworkHandler();
    public static Logger logger = LogManager.getLogger(MODID);

    @Environment(EnvType.CLIENT)
    public static class Client {
        @Environment(EnvType.CLIENT)
        public static GuiScreenEventHandler GUI_HANDLER =
                new GuiScreenEventHandler();
    }

    public JustEnoughCalculation() {
        JecaItem.register();
        registerEvents();
        if (Platform.getEnv() == EnvType.CLIENT)
            registerClientEvents();

        //noinspection ResultOfMethodCallIgnored
        Utilities.config().mkdirs();
    }

    private static void registerEvents() {
        LifecycleEvent.SETUP.register(JustEnoughCalculation::setupCommon);
        PlayerEvent.PLAYER_JOIN.register(Controller.Server::onJoin);
    }

    @Environment(EnvType.CLIENT)
    private static void registerClientEvents() {
        ClientLifecycleEvent.CLIENT_SETUP.register(JustEnoughCalculation::setupClient);
        JecaGui.registerEvents();
        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register(LPlaceholder::onLogOut);
        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register(Controller.Client::onLogOut);
    }

    public static void setupCommon() {
        Utilities.Greetings.send(logger, MODID);
        network.registerServer();
        ILabel.initServer();
    }

    public static void setupClient(Minecraft minecraft) {
        network.registerClient();
        ILabel.initClient();
        Controller.loadFromLocal();
        KeyMappingRegistry.register(keyOpenGuiCraft);
        KeyMappingRegistry.register(keyOpenGuiMath);
    }

    public static final class NetworkHandler {
        private volatile boolean serverRegistered;
        private volatile boolean clientRegistered;

        public void registerServer() {
            if (!serverRegistered) {
                registerServerReceivers();
            }
        }

        public void registerClient() {
            if (!clientRegistered) {
                registerClientReceivers();
            }
        }

        private synchronized void registerServerReceivers() {
            if (serverRegistered) {
                return;
            }
            NetworkManager.registerReceiver(NetworkManager.Side.C2S, PCalculator.TYPE, PCalculator.STREAM_CODEC,
                    (packet, context) -> packet.handle(context));
            NetworkManager.registerReceiver(NetworkManager.Side.C2S, PEdit.TYPE, PEdit.STREAM_CODEC,
                    (packet, context) -> packet.handle(context));
            serverRegistered = true;
        }

        @Environment(EnvType.CLIENT)
        private synchronized void registerClientReceivers() {
            if (clientRegistered) {
                return;
            }
            NetworkManager.registerReceiver(NetworkManager.Side.S2C, PRecord.TYPE, PRecord.STREAM_CODEC,
                    (packet, context) -> packet.handle(context));
            clientRegistered = true;
        }

        public void sendToServer(PCalculator packet) {
            NetworkManager.sendToServer(packet);
        }

        public void sendToServer(PEdit packet) {
            NetworkManager.sendToServer(packet);
        }

        public void sendToPlayer(ServerPlayer player, PRecord packet) {
            NetworkManager.sendToPlayer(player, packet);
        }
    }
}
