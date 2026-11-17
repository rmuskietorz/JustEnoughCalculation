package me.towdium.jecalculation.forge;

import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.data.label.labels.LPlaceholder;
import me.towdium.jecalculation.data.structure.RecordPlayer;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;

/**
 * NeoForge-specific storage for player records backed by attachments.
 */
public final class JecaCapability {
    private static final ResourceLocation RECORD_ID = ResourceLocation.fromNamespaceAndPath(JustEnoughCalculation.MODID, "player_record");

    private static final AttachmentType<Container> PLAYER_RECORD = AttachmentType.serializable(Container::new)
            .copyOnDeath()
            .build();

    private JecaCapability() {
    }

    public static void registerAttachments(RegisterEvent event) {
        event.register(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, helper -> helper.register(RECORD_ID, PLAYER_RECORD));
    }

    public static RecordPlayer getRecord(Player player) {
        return player.getData(PLAYER_RECORD).getRecord();
    }

    public static final class Container implements INBTSerializable<CompoundTag> {
        private RecordPlayer record;

        private RecordPlayer ensureRecord() {
            if (record == null) {
                record = new RecordPlayer();
            }
            return record;
        }

        public RecordPlayer getRecord() {
            return ensureRecord();
        }

        public void setRecord(RecordPlayer record) {
            this.record = record;
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.Provider provider) {
            return ensureRecord().serialize();
        }

        @Override
        public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
            boolean state = LPlaceholder.state;
            LPlaceholder.state = false;
            this.record = new RecordPlayer(nbt);
            LPlaceholder.state = state;
        }
    }
}
