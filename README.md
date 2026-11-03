# Mechanical Camera

Independent Minecraft Java 1.21.1 / NeoForge mod.

## Core mod
The core Mechanical Camera does not require Create, Ponder, Flywheel or Registrate. It registers a camera block and photo item, accepts paper from the top, and reacts to a rising horizontal redstone signal. A successful trigger consumes one paper and outputs one photo item.

## Optional Create integration
Create compatibility is intentionally kept outside the core mod. The planned compatibility addon will be a separate installable mod, so players without Create can load and use the core camera. Players who install Create can additionally install the compatibility addon for kinetic power support.

## Current implementation status
- Independent NeoForge block and block entity: implemented in source.
- Paper insertion, horizontal redstone edge detection, inventory persistence and item output: implemented in source.
- Real image capture and photo viewer: not implemented yet. The current photo item is a placeholder and must not be described as a working photograph.
- Optional Create kinetic addon: architecture separated; addon implementation remains to be built and tested.

## Build
Open the Gradle project in the mechanicalcamera folder with Java 21 and build using Gradle 8.10.
