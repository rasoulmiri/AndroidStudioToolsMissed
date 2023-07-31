import java.util.*

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.8.21"
    id("org.jetbrains.intellij") version "1.15.0"
    id("io.gitlab.arturbosch.detekt") version "1.22.0"
}

group = "com.androidstudiotoolsmissed"
version = "1.0.1-SNAPSHOT"

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
intellij {
    version.set("2022.2.5")
    plugins.set(listOf("android"))
}

tasks {
    runIde {
        // IDE Development Instance (the "Contents" directory is macOS specific):
        ideDir.set(file("/Applications/Android Studio.app/Contents"))
    }
}

detekt {
    toolVersion = "1.22.0"
    source = files(
        "src/main/kotlin",
        "src/test/java"
    )
    config = files("detekt/detekt.yml")
    autoCorrect = true
}


tasks {
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
        val certificatePasswordFile = File("certificate/CertificatePassword.txt")
        val properties = Properties()
        certificatePasswordFile.inputStream().use { inputStream ->
            properties.load(inputStream)
        }
        val certificatePassword: String = properties.getProperty("password").trim()
        certificateChain.set(System.getenv("/certificate/chain.crt"))
        privateKey.set(System.getenv("certificate/private.pem"))
        password.set(certificatePassword)
    }

    publishPlugin {
        val intelliJTokenFile = File("certificate/IntelliJToken.txt")
        val properties = Properties()
        intelliJTokenFile.inputStream().use { inputStream ->
            properties.load(inputStream)
        }
        val intelliJToken: String = properties.getProperty("token").trim()
        token.set(System.getenv(intelliJToken))
    }
}
