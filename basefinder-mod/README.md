# BaseFinder (Fabric client mod)

A small client-side menu that periodically checks nearby **loaded** blocks for a cluster of base-like indicators and prints an in-game alert.

## Features

- Press **N** to open the BaseFinder menu.
- Turn scanning on or off.
- Set a scan radius of 8–32 blocks.
- Adjust the minimum indicator-block count that triggers an alert.
- Shows the nearest detected indicator coordinates in chat; alerts are throttled to once every 30 seconds.

## Important limitations

- Built as a source project for **Minecraft Java 1.21.1 + Fabric**.
- It is a heuristic, not a true base detector: villages, mineshafts, and other structures can trigger false positives.
- It only scans blocks in chunks already loaded by your client. It cannot locate bases beyond loaded terrain or read server files.
- The project has not been built/tested in a Minecraft runtime in this environment. Build it locally before use.
- Use only in single-player or on multiplayer servers where the server rules and staff permit this kind of scanning.

## Build

Install JDK 21 and Gradle 8.10 or a compatible Gradle 8.x release, then run from this folder:

```sh
gradle build
```

The mod jar will be generated under `build/libs/`. Install Fabric Loader and the matching Fabric API for Minecraft 1.21.1, then put the jar in your `.minecraft/mods` folder.

## Defaults

Scanning starts enabled, radius is 16 blocks, threshold is 7 indicators, and the menu key is **N** (changeable in Minecraft's Controls settings).
