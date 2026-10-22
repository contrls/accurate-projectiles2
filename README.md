# Accurate Projectiles

A small Fabric mod for **Minecraft 1.21.1** that removes the random spread (inaccuracy) from **ender pearls** and **wind charges**. Both then fly along your exact look direction, so a pearl and a wind charge thrown at the same aim point start on exactly the same line every time. It is meant for Mace PvP.

Only the random part of the launch is removed. Gravity, drag, speed and the shooter's own movement being added to the projectile stay exactly as in vanilla. Arrows, snowballs, tridents, potions and every other projectile are not touched.

## Requirements

- Minecraft 1.21.1 and Java 21
- Fabric Loader 0.16.0 or newer
- Fabric API for 1.21.1 (this mod does not call Fabric API itself, but it is the standard baseline and is listed as a recommended dependency)

## Installation

1. Install Fabric Loader and Fabric API for Minecraft 1.21.1 as usual.
2. Put `accurate-projectiles-1.0.0.jar` in the right `mods` folder:
   - **Singleplayer:** the `mods` folder of your Minecraft client (`.minecraft/mods`). Singleplayer runs an integrated server, and that is where projectiles are launched.
   - **Dedicated server:** the `mods` folder of the server. Players do not need the mod.
3. Start the game or server once. The config file is created automatically.

> **A client-only install on a multiplayer server does nothing.**
> The server decides where a projectile really flies. If the server does not have this mod, installing it only on your own client will NOT change the real trajectories.

## Configuration

`config/accurate-projectiles.json` is created on first start. Edit it and restart the game or server.

| Option | Default | What it does |
| --- | --- | --- |
| `removePearlSpread` | `true` | Ender pearls fly with no random deviation. |
| `removeWindChargeSpread` | `true` | Player-thrown wind charges fly with no random deviation. |
| `affectDispensers` | `true` | Dispenser-fired pearls and wind charges are made exact too (only has an effect when the matching option above is on). |
| `affectBreezeWindCharges` | `false` | Wind charges shot by breezes are made exact too (needs `removeWindChargeSpread` to be on). Off by default, so breezes stay vanilla. |

A missing file, an empty file, invalid JSON, or an option that is not `true`/`false` never crashes the game: the affected options fall back to their defaults and a warning is written to the log.

## Building the jar

### Easiest: GitHub Actions (nothing to install)

1. Create a free GitHub account and a new empty repository.
2. Upload the contents of this folder (including the `.github` folder) to the repository: **Add file > Upload files**.
3. Open the **Actions** tab. The **Build mod** workflow starts by itself; wait a few minutes for the green check.
4. Open the finished run and download the **accurate-projectiles-jar** artifact at the bottom of the page. Unzip it to get `accurate-projectiles-1.0.0.jar`.

### Locally

1. Install Java 21 (a JDK, for example Temurin 21).
2. Install Gradle 8.8 (the version this project is set up for): download `gradle-8.8-bin.zip` from gradle.org/releases, unzip it, and either add its `bin` folder to your PATH or call it by its full path.
3. In this folder run: `gradle build`
4. The finished jar is `build/libs/accurate-projectiles-1.0.0.jar`.

## How it works

Every projectile launch ends up in `ProjectileEntity#setVelocity(double, double, double, float, float)` (Yarn name; `Projectile#shoot` in Mojang names). The player throw path (`setVelocity(Entity, pitch, yaw, roll, speed, divergence)`, `shootFromRotation` in Mojang names) calls it, and other code such as the breeze calls it directly. A mixin replaces that method's last argument, the inaccuracy, with `0` when the projectile is an ender pearl or a wind charge (recognised by entity type), so only the random offset disappears. Dispenser wind charges are randomised in `WindChargeItem#createEntity` instead, so a second small mixin zeroes that deviation. It sits in its own optional mixin config, so if it cannot apply the game still starts and only dispenser wind charges stay vanilla.

A projectile that has no owner when it is launched is treated as dispenser-fired.

## Manual test plan

1. **Pearls:** stand still and throw 20 ender pearls at the same wall spot. With the mod they should all land on exactly the same spot; without it they scatter slightly.
2. **Wind charges:** do the same with wind charges.
3. **Pearl vs wind charge:** throw a pearl and a wind charge at the same aim point from the same spot. Both leave along exactly the same line. Gravity is vanilla and unchanged: a pearl arcs downwards, while a wind charge flies (as far as vanilla 1.21.1 goes) in a straight line, so compare at short range, such as a wall 3 to 5 blocks away.
4. **Other projectiles:** throw snowballs and shoot arrows. They must still scatter slightly, as in vanilla.
5. **Dispenser (optional):** point a dispenser full of wind charges at a wall about 20 blocks away and power it repeatedly. With `affectDispensers` on they should all hit the same spot; with it off they scatter.
