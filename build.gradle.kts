import org.jetbrains.compose.desktop.application.dsl.TargetFormat

val ktor_version: String by project

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    kotlin("plugin.serialization") version "1.9.22"
}

group = "com.seljaki.desktop"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral() {
        content {
            excludeModule("javax.media", "jai_core")
        }
    }
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
    maven ("https://repo.osgeo.org/repository/release/")
}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    testImplementation(kotlin("test"))
    implementation(compose.desktop.currentOs)
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-cio-jvm:2.3.10")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation ("ch.qos.logback:logback-classic:1.4.12")
    implementation("it.skrape:skrapeit:1.2.2")

    implementation("org.geotools:gt-main:31.0")
    implementation("org.geotools:gt-shapefile:31.0")
    implementation("org.geotools:gt-geojson:31.0")
    //implementation("org.locationtech.jts:jts-core:1.18.2")
    implementation("org.geotools:gt-epsg-hsql:31-RC")
    implementation("io.github.serpro69:kotlin-faker:1.12.0")
    implementation("org.iban4j:iban4j:3.2.8-RELEASE")


    //implementation("com.google.maps.android:maps-compose:5.0.1")
    //implementation("org.osmdroid:osmdroid-android:6.1.18")
    //implementation("ovh.plrapps:mapcompose-mp:0.9.3")

}

tasks.test {
    useJUnitPlatform()
}

compose {
    kotlinCompilerPlugin.set("1.5.7")
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "AgroMajsterDesktop"
            packageVersion = "1.0.0"
        }
    }
}
