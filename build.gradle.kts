plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.8.21"
    id("org.jetbrains.intellij") version "1.13.3"
    id("io.gitlab.arturbosch.detekt") version "1.22.0"
}

group = "com.androidstudiotoolsmissed"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}


dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.7.22")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.6.4")
    implementation("io.insert-koin:koin-core:3.2.2")
    testImplementation("io.insert-koin:koin-test:3.2.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.6.0")
    testImplementation("io.mockk:mockk:1.13.5")
    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
    testImplementation ("app.cash.turbine:turbine:1.0.0")
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.22.0")
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2022.2.5")
//    type.set("IC") // Target IDE Platform

    plugins.set(listOf("android"))
}

tasks {
    runIde {
        // Absolute path to installed target 3.5 Android Studio to use as
        // IDE Development Instance (the "Contents" directory is macOS specific):
        ideDir.set(file("/Applications/Android Studio.app/Contents"))
    }
}

detekt {
    toolVersion = "1.22.0"
//    input = files("src/main/kotlin")
    source = files(
        "src/main/kotlin",
        "src/test/java"

    )
    config = files("detekt/detekt.yml")
    autoCorrect = true
}


tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("222")
        untilBuild.set("232.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
