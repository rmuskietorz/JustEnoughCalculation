# Projektplan: Update auf Minecraft 1.21.1 / NeoForge 21.1.203

## Ziel
- Mod "Just Enough Calculation" auf Minecraft 1.21.1 portieren und NeoForge 21.1.203 unterstützen.
- Plattformübergreifende Module (common, fabric, quilt) auf aktuelle Toolchain und Abhängigkeiten bringen.
- Sicherstellen, dass das NeoForge-Modul kompiliert und wesentliche Funktionen erhalten bleiben.

## Erledigt
- `gradle.properties`: Versionen auf Minecraft 1.21.1, NeoForge 21.1.203, neue Fabric-/Quilt-/JEI-/REI-Versionen und Architectury 18.0.4 aktualisiert.
- `build.gradle` (Root): Architectury-Plugin 3.4.162, Loom 1.11-SNAPSHOT, neue Parchment-Mappings (1.21.1:2024.11.17) und Java-Release 21 gesetzt.
- `settings.gradle`: Subprojekt `neoforge` eingebunden.
- Ordner `forge/` in `neoforge/` umbenannt, um künftige NeoForge-spezifische Anpassungen vorzubereiten.
- `neoforge/build.gradle`: Plattform auf `neoForge()` umgestellt, Entwicklungs-/Shadow-Konfigurationen angepasst und JEI/REI-Abhängigkeiten auf NeoForge-Artefakte aktualisiert.
- NeoForge-Quellcode: Imports auf `net.neoforged.*` migriert, Capability-Handling auf Attachments umgestellt und REI-/JEI-Hooks aktualisiert.
- NeoForge-Ressourcen: `META-INF/neoforge.mods.toml` angelegt und `pack.mcmeta` auf Minecraft 1.21.1 Pack-Formate (34/48) angepasst.
- Build-Tooling: Java 21 lokal installiert und über `org.gradle.java.home` sowie Gradle-Wrapper (≥8.5) eingebunden.
- Gradle-/Loom-Setup: Wrapper auf 8.10.2 angehoben, lokal gepatchte Architectury-Loom-Version (`1.11.454-patched`) mit ersetztem `ProjectDependency#getPath()` eingebunden (`local-maven/` + `settings.gradle`-Resolution) und `./gradlew help` als Smoke-Test erfolgreich durchlaufen.
- `common`: Forge-Erkennung auf `Platform.isForgeLike()`/`isNeoForge()` vereinheitlicht und die JEI-Fluidstack-Reflection auf NeoForge-Klassen aktualisiert.
- `common`: Netzwerkebene von `NetworkChannel` auf `CustomPacketPayload` + `StreamCodec` (ItemStack-Codec + `ByteBufCodecs`) umgestellt; Registrierung/Senden erfolgt nun über Architectury 1.21 APIs.

## Nächste Schritte
1. **Common-Port 1.21.1**  
   - Item/Fluid-Serialisierung vollständig auf Data Components (CustomData/ComponentPatch) und neue JEI-/Architectury-Hooks migrieren (`Utilities`, `LItemStack`, `LFluidStack`, JEI-Fluidhelper).  
   - Client-Ereignisse & Rendering (`JecaGui`, `GuiUtils`, Input-Events) auf die neuen Signaturen (GuiGraphics, ClientScreenInputEvent 1.21) anpassen.  
   - JEI-Port: Fluid-Helper erwartet nun `Holder<Fluid>`; Update der Einbindung sowie Prüfung alternativer APIs (EMI?).  
2. **Toolchain-Nacharbeit & Warnungen adressieren**  
   - Lokalen Loom-Patch dokumentieren und auf offizielles Update warten (lokales Repo bereinigen, falls Upstream-Fix eintrifft).  
   - Remap-Warnungen (ClientPacketListener, BucketItem, TagLoader etc.) analysieren – ggf. aktualisierte Dependencies/Mappings erforderlich.  
   - NeoForge: Neues Vorgehen für Access-Widener/Access-Transformer ableiten, da `convertAccessWideners` entfällt.

3. **NeoForge-Port komplettieren**  
   - `JustEnoughCalculationForge` (neoforge/.../JustEnoughCalculationForge.java:9) an neue Client-Events anpassen und die auskommentierten Screen-/Tooltip-Hooks mit NeoForge 21.1 APIs nachrüsten.  
   - Attachment-/Config-Verhalten im Spiel testen (Respawn, Todesfall, ClientMode-Schalter).  
   - Alternative für Item-Capabilities evaluieren (`UtilitiesImpl#getCap` liefert momentan `null`), ggf. Data Components oder dedizierte Attachments pro Item einführen.

4. **Ressourcen/Metadata**  
   - `fabric.mod.json`/`quilt.mod.json`: Minecraft-/Architectury-Minversionen auf 1.21.1/18.x anheben, Loader-IDs prüfen und Icons/Entrypoints gegentesten.  
   - Logo/Assets-Pfade verifizieren, Versionsplatzhalter und sonstige Metadaten (z. B. Packbeschreibung) final prüfen.  
   - `architectury.common.json` + Access Widener: Funktionstest mit Loom 1.11 (kein automatisches Forge-Conversion mehr).

5. **Common-/Client-Kompatibilität**  
   - Commoncode auf verbleibende Mapping-/API-Brüche prüfen (insb. Fabric-/Quilt-spezifische Hooks sowie neue Mojang-/Architectury-Hilfsklassen).  
   - Loader-spezifische Implementierungen (Fabric/Quilt) auf die neuen API-Versionen abstimmen und JEI/REI-Integration durchtesten.

6. **Build- und Testphase**  
   - Gradle Build für alle Module (`./gradlew build`) ausführen.  
   - Fehler auswerten, fehlende Anpassungen nachziehen, bis Build erfolgreich ist.

## Offene Fragen / Beobachtungen
- NeoForge-spezifische Capability-Registrierung abgleichen (mögliche API-Änderungen nach 1.20).  
- Prüfen, ob REI weiterhin benötigt wird oder ein Update auf EMI nötig ist (abhängig von Projektanforderungen).
- Item-Capabilities: `neoforge/.../UtilitiesImpl#getCap` gibt aktuell `null` zurück – saubere Lösung (Data Components/Attachments) evaluieren.  
- GUI-Events: Welche NeoForge-Ersatz-Events decken die bisherige Screen-/Tooltip-Verarbeitung ab?
- Build-Umgebung: Aktuell läuft ein lokaler Loom-Patch; offizielle Lösung beobachten und Übergang planen.
- Build-Output: Duplicate-Class-Warnung bei JEI-APIs und Remap-Fehler (ClientPacketListener, BucketItem etc.) nachvollziehen.

> Status aktualisieren, sobald ein Schritt abgeschlossen oder neue To-dos entdeckt wurden.
