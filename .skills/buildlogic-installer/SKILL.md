---
name: buildlogic-installer
description: "Install, link, and configure the sjy-build-logic convention plugins and shared modules into any Android or Kotlin Multiplatform (KMP) / Compose Multiplatform (CMP) project. Make sure to use this skill whenever the user mentions setting up, importing, adding, or installing sjy-build-logic or its submodules, or when they want to link data-pref, utils, presentation, data-supabase, data-local, data-network, or other shared utilities from sjy-build-logic."
---

# Buildlogic Installer Skill

This skill guides the installation and configuration of the reusable `sjy-build-logic` convention plugins and shared modules in any Android or Kotlin Multiplatform (KMP) / Compose Multiplatform (CMP) project.

<instructions>
Refer to the index below to find step-by-step installation instructions or catalog module details, and adhere strictly to the constraints.
</instructions>

---

## Reference Index

- [installation.md](file:///Users/jsanjaya/Projects/learning/berbudget/sjy-build-logic/.skills/buildlogic-installer/references/installation.md)
  - Contains step-by-step setup guides, version and SDK baselines, and scenario-specific installation targets (Android-only vs KMP/CMP).
  - *Read when*: Setting up a new project, adding git submodules, modifying `settings.gradle.kts`, or applying module plugins.
- [shared-modules.md](file:///Users/jsanjaya/Projects/learning/berbudget/sjy-build-logic/.skills/buildlogic-installer/references/shared-modules.md)
  - Contains mappings, registration declarations, descriptions, and dependency usage syntax for all multiplatform and Android-specific shared modules in `sjy-build-logic`.
  - *Read when*: Linking shared utility/data submodules, registering `projectDir` structures, or adding dependencies between local modules.

---

<constraints>
- NEVER copy or redefine the version catalog or plugin mappings inside the target project's `gradle/libs.versions.toml` file. Always rely on the injected `sjy` version catalog.
- ALWAYS apply the plugins using their type-safe accessor paths from the `sjy` catalog (e.g., `alias(sjy.plugins.buildlogic.*)`) instead of raw plugin IDs where possible.
- Verify that `includeBuild("sjy-build-logic")` is placed at the top of the root `settings.gradle.kts` pluginManagement block.
- For KMP/CMP modules, verify that the module is configured as a library (`com.android.kotlin.multiplatform.library`) and NOT an application under AGP 9.0+.
</constraints>
