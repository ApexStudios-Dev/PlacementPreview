# PlacementPreview (Beta)

PlacementPreview is a mod for Minecraft (NeoForge) which attempts to visualize the placement of the item the player is currently holding.

Currently, this just visualizes Block placements, but there are plans to expand this to spawn egg placements, bucket placements, and other non-BlockItem placements.

This is all intended to be the most visually accurate representation of the placement possible. Because of this, block placements do not go through vanilla's `getStateForPlacement` method; due to its nullable nature, we lose the block state trying to be placed for invalid placements. To get around this, a `PlacementResult` system has been implemented, allowing us to track the success state of the placement as well as the state being placed.

<details>
<summary> Groovy DSL (build.gradle) </summary>

```groovy
repositories {
    maven { url "https://maven.apexmodder.com/releases" }
}

dependencies {
    implementation "dev.apexstudios:placementpreview:<version>"
}
```

</details>

<details>
<summary> Kotlin DSL (build.gradle.kts) </summary>

```kotlin
repositories {
    maven("https://maven.apexmodder.com/releases")
}

dependencies {
    implementation("dev.apexstudios:placementpreview:<version>")
}
```

</details>
