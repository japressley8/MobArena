plugins {
    id("java-library")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.garbagemule"
version = "0.109"

layout.buildDirectory.set(layout.projectDirectory.dir("build-cli"))

repositories {
    mavenLocal()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://jitpack.io")
    maven("https://repo.maven.apache.org/maven2/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
    compileOnly("org.bstats:bstats-bukkit:2.2.1")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.hamcrest:hamcrest-all:1.3")
    testImplementation("org.mockito:mockito-inline:5.2.0")
}

java {
    // Use the current Java version (25) for compilation
    // The Spigot API 1.19 is compatible with Java 25
}

sourceSets {
    test {
        // This is a bit of a hack. It ensures that the dependencies for the
        // "main" source set are available to the test suite. Without it, the
        // dependencies would have to be listed twice, and even that doesn't
        // seem to work with the currently used version of Mockito.
        configurations.testImplementation.configure {
            extendsFrom(configurations.compileOnly.get())
        }
    }
}

tasks {
    processResources {
        filesMatching("plugin.yml") {
            expand("project" to mapOf(
                "name" to "MobArena",
                "version" to version,
            ))
        }
    }

    shadowJar {
        enabled = false

        // minimize() is not compatible with Gradle 9.0 and Shadow 8.1.1
        // minimize()

        relocate("org.bstats", "com.garbagemule.MobArena.metrics")

        // Avoid failures when copying certain META-INF entries from dependencies.
        // Exclude signature files and merge service descriptors.
        mergeServiceFiles()
        exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        archiveBaseName.set("MobArena")
        archiveClassifier.set("")
    }

    jar {
        enabled = false
    }

    test {
        systemProperty("net.bytebuddy.experimental", "true")
        include("**/*Test.class", "**/*Tests.class", "**/*TestCase.class")
    }

    build {
        dependsOn(named("fatJar"))
    }

    // Fallback fat JAR task: assemble a merged JAR including runtime deps.
    val fatJar by registering(Jar::class) {
        archiveBaseName.set("MobArena")
        archiveClassifier.set("")
        from(sourceSets.main.get().output)

        // Unpack runtime JAR dependencies into the fat jar
        dependsOn(configurations.runtimeClasspath)
        from({
            configurations.runtimeClasspath.get()
                .filter { it.name.endsWith(".jar") && !it.name.contains("bstats") }
                .map { zipTree(it) }
        })

        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        // Avoid signature files
        exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
    }
}
