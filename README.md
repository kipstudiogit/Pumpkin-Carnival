# Pumpkin Carnival

[![License: GPL v3](https://img.shields.io/badge/Code-GPL--3.0--only-blue.svg)](./LICENSE)
[![Assets: CC BY-NC-ND 4.0](https://img.shields.io/badge/Assets-CC%20BY--NC--ND%204.0-lightgrey.svg)](./LICENSE-ASSETS)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.19.4%20(Forge)-orange.svg)](https://files.minecraftforge.net/)

An atmospheric autumn-horror Minecraft Forge mod developed by **Kettle Ignition Power Studio (K. I. P. Studio)** (*MouShi_*, *_DemSb_*).  
Step into the fog-shrouded **Misty Grounds**, uncover abandoned **Pumpkin Plantations**, befriend observant **Crows**, and survive the **Pumpkin Carnival** beneath the Jack-o'-Lantern Full Moon.

---

## Requirements

- **Minecraft:** `1.19.4`
- **Mod Loader:** Minecraft Forge
- **Required Dependencies:**
  - [GeckoLib 4](https://www.curseforge.com/minecraft/mc-mods/geckolib) (`>= 4.2`)
  - [TerraBlender](https://www.curseforge.com/minecraft/mc-mods/terrablender) (`>= 2.2.0.0`)

---

## Key Features

### Misty Grounds Biome
- Custom slate/charcoal fog transitions across day, night, and Full Moon nights.
- Haunting ambient soundscape (`wind_howl`, `crow_caw`), subtle wandering soul embers, and ash particles.
- Unique terrain features: **Ancient Oak**, **Lightning Snag**, and **Weeping Cypress** dead trees, fallen deadwood, misty pools, pumpkin effigies, abandoned carnival wagons, and ritual cairns.
- Full vanilla underground cave, ore, geode, and spring generation preserved.

### Full Moon Event — *Pumpkin Carnival*
- Triggers when a player is inside **Misty Grounds** during a Full Moon night.
- Replaces the lunar disc with a glowing **Jack-o'-Lantern Moon**, thickens spectral lunar mist, and rings the Carnival Bell.

### Pumpkin Plantation Structure
- Multi-sector estate generated across Misty Grounds:
  - **Manor House:** vaulted two-block-tall wine & pumpkin cellar with ladder access, ground & second floor, balcony, gothic roof, and hidden manor loot chest.
  - **Windmill Tower:** stone base and timber upper gallery with broken sail blades and mill machinery.
  - **Terraced Pumpkin Fields:** irrigated farmland flanked by a Titan Scarecrow.
  - **Courtyard & Cemetery:** twin-tower gatehouse arch, gravel roadway, broken harvest wagon, and family graves.

### Intelligent Crows (`pumpkincarnival:crow`)
- Natural flying & perching AI preferring Deadwood logs, fences, pumpkins, lanterns, and leaves.
- **Taming & Shoulder Riding:** tame crows with **Pumpkin Seeds** (1/3 chance) or **Soul Candy** (100% chance). Tamed crows follow you, sit on command, and perch on your shoulder.
- **Bartering & Beak Loot:** wild and tamed crows pick up shiny items & seeds (nuggets, emeralds, diamonds, amethyst shards, tickets, candy, seeds). Offer seeds or Soul Candy to trade for whatever a crow holds in its beak.
- **Gift Foraging:** feed a tamed crow **Soul Candy** to send it foraging for a gift (higher chance for **Carnival Tickets** and **Emeralds** on Carnival nights).
- **Scarecrow Alert:** spots nearby monsters, puffs up, tilts its head toward the threat, and emits a sharp warning caw.

### Items & Building Blocks
- **Carnival Ticket:**
  - *Unstamped -> Attuned (3 Stamps):* right-click in air to attune and reveal a soul-flame trail guiding toward **Misty Grounds** or the nearest **Pumpkin Plantation**.
  - *Lantern Ward:* right-click a **Carnival Lantern** or **Jack o'Lantern** to reveal nearby monsters (`Glowing`) and grant protective buffs (`Shift + Right-Click` on a Carnival Lantern inside Misty Grounds with 3/3 stamps for the **Grand Boon**).
  - *Crow Sight & Dispatch:* `Shift + Right-Click` in air to call crows and reveal threats; right-click a crow with a ticket for royal taming & high-tier treasure.
  - *Soul Toll:* holding an attuned ticket inside Misty Grounds or during a Carnival night spares you once from lethal damage.
- **Soul Candy:** restorative treat granting `Night Vision` and `Speed II`, also used for crow taming & gift foraging.
- **Carnival Lantern** & full **Deadwood** woodset (logs, stripped logs, wood, planks, stairs, slabs, fences, fence gates, doors, trapdoors, pressure plates, buttons, saplings).

---

## Installation

1. Install **Minecraft Forge 1.19.4**.
2. Download **GeckoLib 4** (`>= 4.2`) and **TerraBlender** (`>= 2.2.0.0`).
3. Download the latest `Pumpkin Carnival` `.jar` from [GitHub Releases](https://github.com/kipstudiogit/Pumpkin-Carnival/releases).
4. Place all `.jar` files into your `.minecraft/mods/` folder and launch the game.

---

## Building from Source

Requires **JDK 17**.

```bash
git clone https://github.com/kipstudiogit/Pumpkin-Carnival.git
cd Pumpkin-Carnival
./gradlew build
```

Built `.jar` artifacts will be output to `build/libs/`.

---

## Automated Releases

This repository uses the **Universal Mod Release** GitHub Actions workflow (`.github/workflows/release.yml`):
- Push any tag matching `v*` (for example `v1.0.0` or `v1.0.0-beta.1`) to automatically build with Gradle and create a GitHub Release with attached `.jar` artifacts.
- Or trigger manually from the **Actions** tab via `workflow_dispatch`.

---

## License

This project is dual-licensed:
- **Code & Data (`GPL-3.0-only`):** see [`LICENSE`](./LICENSE)
- **Assets (`CC BY-NC-ND 4.0`):** see [`LICENSE-ASSETS`](./LICENSE-ASSETS)

For a full path-by-path breakdown, see [`LICENSING.md`](./LICENSING.md).

---

## Links & Issues

- Repository: https://github.com/kipstudiogit/Pumpkin-Carnival
- Bug Reports & Suggestions: https://github.com/kipstudiogit/Pumpkin-Carnival/issues