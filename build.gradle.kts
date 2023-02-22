import org.lwjgl.lwjgl
import org.lwjgl.Lwjgl.Module.*

plugins {
    id("java")
    id("org.lwjgl.plugin") version "0.0.30"
    id("io.freefair.lombok") version "6.6.2"
}

group = "com.chaottic"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")

    lwjgl {
        implementation(glfw, vulkan)
    }
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}