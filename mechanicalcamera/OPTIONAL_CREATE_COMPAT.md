# Optional Create compatibility module

The core project intentionally has no Create classes in its classpath or metadata requirements.

Create kinetic integration must be packaged as a separate addon mod with:
- a required dependency on the core mechanicalcamera mod;
- a required dependency on Create 6.x;
- its own Create-specific kinetic block and block entity implementation;
- no Create references in the core mod.

This separation prevents Create, Ponder or Flywheel class loading from breaking the standalone camera. The addon is not yet implemented; do not install or advertise it as available until its own JAR has been built and tested.
