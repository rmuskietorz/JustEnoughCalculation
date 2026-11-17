# Projektplan: Update auf Minecraft 1.21.1 / NeoForge 21.1.203

## Ziel
- Mod "Just Enough Calculation" auf Minecraft 1.21.1 portieren und NeoForge 21.1.203 unterstützen.
- Plattformübergreifende Module (common, fabric, quilt) auf aktuelle Toolchain und Abhängigkeiten bringen.
- Sicherstellen, dass das NeoForge-Modul kompiliert und wesentliche Funktionen erhalten bleiben.

## Erledigt
- `gradle.properties`: Versionen auf Minecraft 1.21.1, NeoForge 21.1.203, neue Fabric-/Quilt-/JEI-/REI-Versionen und Architectury 14.0.3 aktualisiert.
- `build.gradle` (Root): Architectury-Plugin 3.4.162, Loom 1.11-SNAPSHOT, neue Parchment-Mappings (1.21.1:2024.11.17) und Java-Release 21 gesetzt.
- `build.gradle` (Root): Globale `resolutionStrategy` zwingt jetzt alle `dev.architectury:architectury-*` Artefakte auf die in `gradle.properties` definierte Version, damit keine älteren Varianten via Transitiveinschlüssen auftauchen.
- `settings.gradle`: Subprojekt `neoforge` eingebunden.
- Ordner `forge/` in `neoforge/` umbenannt, um künftige NeoForge-spezifische Anpassungen vorzubereiten.
- `neoforge/build.gradle`: Plattform auf `neoForge()` umgestellt, Entwicklungs-/Shadow-Konfigurationen angepasst und JEI/REI-Abhängigkeiten auf NeoForge-Artefakte aktualisiert.
- NeoForge-Quellcode: Imports auf `net.neoforged.*` migriert, Capability-Handling auf Attachments umgestellt und REI-/JEI-Hooks aktualisiert.
- NeoForge-Ressourcen: `META-INF/neoforge.mods.toml` angelegt und `pack.mcmeta` auf Minecraft 1.21.1 Pack-Formate (34/48) angepasst.
- `neoforge`: `JecaConfig` wird wieder über `ModLoadingContext` registriert, damit der `clientMode`-Schalter persistiert und Attachments auf NeoForge auch außerhalb des Dev-Clients funktionieren.
- Build-Tooling: Java 21-Anforderung dokumentiert, Gradle-Toolchain nutzt Wrapper ≥8.5 und benötigt keinen projektspezifischen `org.gradle.java.home` mehr.
- Gradle-/Loom-Setup: Wrapper auf 8.10.2 angehoben, lokal gepatchte Architectury-Loom-Version (`1.11.454-patched`) mit ersetztem `ProjectDependency#getPath()` eingebunden (`local-maven/` + `settings.gradle`-Resolution) und `./gradlew help` als Smoke-Test erfolgreich durchlaufen.
- `gradle.properties` + Module-Buildskripte (`fabric/`, `quilt/`, `neoforge/`) & `README.md`: Neuer `include_rei_runtime`-Parameter sorgt dafür, dass REI standardmäßig nur compileOnly gezogen wird und bei Bedarf via `-Pinclude_rei_runtime=true` in Dev-Clients landet (Workaround für NeoForge-21.1-Instabilitäten).
- Toolchain-Hygiene: Hardcodierter `org.gradle.java.home` entfernt, README beschreibt den benötigten JDK-21-Setup und `.gitignore` ignoriert nun lokale `.jdk/`-Installationen.
- `common`: Forge-Erkennung auf `Platform.isForgeLike()`/`isNeoForge()` vereinheitlicht und die JEI-Fluidstack-Reflection auf NeoForge-Klassen aktualisiert.
- `common`: Netzwerkebene von `NetworkChannel` auf `CustomPacketPayload` + `StreamCodec` (ItemStack-Codec + `ByteBufCodecs`) umgestellt; Registrierung/Senden erfolgt nun über Architectury 1.21 APIs.
- `common`: JEI-Plugin aktualisiert (`JecaJEIPlugin`) – Fluidstacks werden jetzt über `Holder<Fluid>` erstellt, `ResourceLocation.fromNamespaceAndPath` genutzt und die NeoForge-FluidStack-Klasse (`net.neoforged.neoforge.fluids.FluidStack`) referenziert.
- `common`: ItemStack-Labels serialisieren jetzt vollständige Data Components (statt ForgeCaps), neue Utilities-Helfer übernehmen das Lesen/Schreiben der `DataComponentPatch`-Infos.
- `common`: Fluid-Repräsentationen nutzen jetzt ebenfalls Data Components – `Utilities` stellt Serialisierungshelfer bereit, `LFluidStack` speichert die Component-Patches und JEI-Bridge extrahiert Flüssigkeiten inklusive Komponenten aus Fabric-/NeoForge-APIs.
- `common`: Client-Ereignisse & Input wurden auf die 1.21-`ClientScreenInputEvent`-Signaturen umgestellt – der Reflection-Helfer entfällt, GUI-/Overlay-Handler nutzen nun die vom Event gelieferten Mauskoordinaten/GiuGraphics-Kontexte.
- `fabric.mod.json` & `quilt.mod.json`: Mindestanforderungen auf Minecraft 1.21.1 sowie Architectury 14.0.3 angehoben, so dass alle Loader konsistent denselben Versionsstand deklarieren.
- `common`: `WLabel` zeigt standardisierte Mengen-Strings (`ILabel#getAmountString`) direkt an und erweitert Tooltips automatisch um lokalisierte Mengendetails; das alte TODO entfällt.

## Nächste Schritte
1. **Common-Port 1.21.1**  
   - Overlay-/Tooltip-Handling konsolidieren: `common/src/main/java/me/towdium/jecalculation/events/GuiScreenEventHandler.java` verwendet weiterhin die alten `ClientScreenInputEvent.*_PRE`-Hooks und cached Tooltips manuell – prüfen, ob 1.21 jetzt die `Before/After`-Callbacks oder `HandledScreenEvents` voraussetzt und wie sich das Overlay besser in den neuen Render-Zyklus integrieren lässt.  
   - Item/Fluid-Repräsentationen weiter harmonisieren: Data-Component-Patches sind nun vorhanden, aber wir müssen definieren, welche Component-Typen cross-loader relevant sind (Potion, PotionContents, FluidAttributes etc.) und wie alte `nbt`/`cap`-Saves migriert/aufgeräumt werden.  
2. **Toolchain-Nacharbeit & Warnungen adressieren**  
   - Lokalen Loom-Patch dokumentieren und auf offizielles Update warten (lokales Repo bereinigen, falls Upstream-Fix eintrifft).  
   - Remap-Warnungen (ClientPacketListener, BucketItem, TagLoader etc.) analysieren – ggf. aktualisierte Dependencies/Mappings erforderlich.  
   - NeoForge: Neues Vorgehen für Access-Widener/Access-Transformer ableiten, da `convertAccessWideners` entfällt.

3. **NeoForge-Port komplettieren**  
   - `JustEnoughCalculationForge` (neoforge/.../JustEnoughCalculationForge.java:9) an neue Client-Events anpassen und die auskommentierten Screen-/Tooltip-Hooks mit NeoForge 21.1 APIs nachrüsten.  
   - Attachment-/Config-Verhalten im Spiel testen (Respawn, Todesfall, ClientMode-Schalter).

4. **Ressourcen/Metadata**  
   - Logo/Assets-Pfade verifizieren, Versionsplatzhalter und sonstige Metadaten (z. B. Packbeschreibung) final prüfen.  
   - `architectury.common.json` + Access Widener: Funktionstest mit Loom 1.11 (kein automatisches Forge-Conversion mehr).

5. **Common-/Client-Kompatibilität**  
   - Commoncode auf verbleibende Mapping-/API-Brüche prüfen (insb. Fabric-/Quilt-spezifische Hooks sowie neue Mojang-/Architectury-Hilfsklassen).  
   - Loader-spezifische Implementierungen (Fabric/Quilt) auf die neuen API-Versionen abstimmen und JEI/REI-Integration durchtesten.
   - JEI/REI-Bridge speziell mit Fluid-Komponenten abgleichen (Fabric: `IJeiFluidIngredient` ↔ Data Components, NeoForge: `FluidStack#getComponentsPatch`), um sicherzustellen, dass Data-Component-Payloads nicht unterwegs verloren gehen.

6. **Build- und Testphase**  
   - Gradle Build für alle Module (`./gradlew build`) ausführen.  
   - Fehler auswerten, fehlende Anpassungen nachziehen, bis Build erfolgreich ist.

7. **Repo-Hygiene**  
   - Zahlreiche Dateien weisen geänderte Ausführungsrechte (100755 ↔ 100644) auf – vor dem Merge klären, welche Rechte gelten sollen, damit der Commit nicht nur Modus-Änderungen enthält.

## Offene Fragen / Beobachtungen
- Prüfen, ob REI weiterhin benötigt wird oder ein Update auf EMI nötig ist (abhängig von Projektanforderungen).
- Data Components: Neuer Item-/Fluid-Flow (Utilities/LItemStack/LFluidStack) auf allen Loadern gegentesten und fehlende Attachments ergänzen.  
- GUI-Events: Welche NeoForge-Ersatz-Events decken die bisherige Screen-/Tooltip-Verarbeitung ab?
- Overlay-Integration: Kann `common/src/main/java/me/towdium/jecalculation/events/GuiScreenEventHandler.java` auf den neuen Architectury-`HandledScreenEvents`-Wrapper wechseln, ohne dass JEI / REI Tooltips kollidieren?
- Build-Umgebung: Aktuell läuft ein lokaler Loom-Patch; offizielle Lösung beobachten und Übergang planen.
- Build-Output: Duplicate-Class-Warnung bei JEI-APIs und Remap-Fehler (ClientPacketListener, BucketItem etc.) nachvollziehen.
- Repo-Hygiene: Wie gehen wir mit den Modusänderungen an Skripten (`gradlew`, `*.sh` etc.) um, damit der Branch clean bleibt?

> Status aktualisieren, sobald ein Schritt abgeschlossen oder neue To-dos entdeckt wurden.
