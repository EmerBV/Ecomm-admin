import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    kotlin("plugin.serialization") version "2.1.20"
}

kotlin {
    jvm("desktop") {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

    sourceSets {
        val desktopMain by getting
        val desktopTest by getting

        commonMain.dependencies {
            // Compose
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.materialIconsExtended)
            implementation(compose.animation)

            // Ktor
            implementation("io.ktor:ktor-client-core:2.3.9")
            implementation("io.ktor:ktor-client-cio:2.3.9")
            implementation("io.ktor:ktor-client-content-negotiation:2.3.9")
            implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.9")
            implementation("io.ktor:ktor-client-logging:2.3.9")

            // Koin
            implementation("io.insert-koin:koin-core:3.5.5")

            // Serialization
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

            // Coroutines
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")

            // Multiplatform Settings
            implementation("com.russhwolf:multiplatform-settings:1.1.1")
            implementation("com.russhwolf:multiplatform-settings-serialization:1.1.1")

            // Lifecycle
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
            implementation("io.mockk:mockk:1.13.10")
        }

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }

        desktopTest.dependencies {
            implementation(kotlin("test-junit"))
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.emerbv.ecommadmin.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.emerbv.ecommadmin"
            packageVersion = "1.0.0"

            windows {
                menu = true
                upgradeUuid = "E3A7A74C-7B68-4AB0-9BE9-E2FF1FD3C190"
                iconFile.set(project.file("src/commonMain/resources/icon.ico"))
            }

            macOS {
                bundleID = "com.emerbv.ecommadmin"
                iconFile.set(project.file("src/commonMain/resources/icon.icns"))
            }

            linux {
                iconFile.set(project.file("src/commonMain/resources/icon.png"))
            }
        }
    }
}
