# ⚔ Armor FD Tracker

A lightweight Fabric client mod for **Hypixel SkyBlock** that displays a real-time HUD showing the kill count and tier progress for each piece of your **Final Destination** armor.

---

## Features

- **Per-piece kill tracker** — shows kills for Helmet, Chestplate, Leggings and Boots separately (top-to-bottom order)
- **Defense bonus display** — shows your current ❈ defense bonus per piece based on your kill tier
- **Next tier progress** — shows how many kills until the next Enderman Bulwark tier upgrade
- **Auto-hide** — HUD only appears when you have at least one FD piece equipped
- **Toggle keybind** — press `H` (rebindable in Options → Controls) to show/hide the HUD
- **Draggable HUD** — run `/fdtracker gui` to drag the HUD anywhere on screen
- **Resizable HUD** — scroll up/down in the GUI to scale the HUD between 50% and 200%
- **Chat-friendly** — HUD stays visible when the chat is open
- **Hypixel-only** — HUD is hidden on any other server

---

## HUD Preview

```
⚔ Final Destination
Helmet       14,018  ❈+310
  → 25,000 (❈+335)
Chestplate   14,018  ❈+310
  → 25,000 (❈+335)
Leggings     14,018  ❈+310
  → 25,000 (❈+335)
Boots        14,018  ❈+310
  → 25,000 (❈+335)
```

---

## Tier Table

| Tier | Total Kills | ❈ Defense |
|------|------------|-----------|
| 1    | 100        | +20       |
| 2    | 200        | +40       |
| 3    | 300        | +60       |
| 4    | 500        | +90       |
| 5    | 800        | +120      |
| 6    | 1,200      | +150      |
| 7    | 1,750      | +180      |
| 8    | 2,500      | +210      |
| 9    | 3,500      | +240      |
| 10   | 5,000      | +270      |
| 11   | 10,000     | +310      |
| 12   | 25,000     | +335      |
| 13   | 50,000     | +355      |
| 14   | 75,000     | +370      |
| 15   | 100,000    | +380      |
| 16   | 125,000    | +390      |
| 17   | 150,000    | +395      |
| 18   | 200,000    | +400      |

---

## Commands

| Command | Description |
|---------|-------------|
| `/fdtracker gui` | Open the HUD position/scale editor |
| `/fdtracker kills` | Print kill counts in chat |
| `/fdtracker debug` | Dump raw NBT of the item in your hand |

---

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) and [Fabric API](https://modrinth.com/mod/fabric-api)
2. Drop the `.jar` into your `.minecraft/mods/` folder
3. Join Hypixel and equip your Final Destination armor

---

## Requirements

- Minecraft 1.21.11
- Fabric Loader ≥ 0.18.5
- Fabric API
