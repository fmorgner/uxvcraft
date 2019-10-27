import net.minecraftforge.gradle.userdev.UserDevExtension
import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    val shadowVersion: String by project
    val kotlinVersion: String by project

    repositories {
        maven { url = uri("https://files.minecraftforge.net/maven") }
        jcenter()
        mavenCentral()
    }

    dependencies {
        classpath("org.jetbrains.kotlin", "kotlin-gradle-plugin", kotlinVersion)
        classpath("com.github.jengelman.gradle.plugins", "shadow", shadowVersion)
        classpath("net.minecraftforge.gradle", "ForgeGradle", "3.+") {
            isChanging = true
        }
    }
}

subprojects {
    val minecraftVersion: String by project
    val mappingChannel: String by project
    val mappingVersion: String by project
    val forgeVersion: String by project

    group = "ch.felixmorgner"

    apply(plugin = "com.github.johnrengelman.shadow")
    apply(plugin = "net.minecraftforge.gradle")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.gradle.java")
    apply(plugin = "maven-publish")

    dependencies {
        "compile"(kotlin("stdlib"))
        "testCompile"("junit:junit:4.12")
        "minecraft"("net.minecraftforge:forge:$minecraftVersion-$forgeVersion")
    }

    configure<KotlinJvmProjectExtension> {
        experimental.coroutines = Coroutines.ENABLE
    }

    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    tasks.named("build") {
        dependsOn("reobfJar")
    }

    configure<UserDevExtension> {
        mappings(
            mapOf(
                "channel" to mappingChannel,
                "version" to mappingVersion
            )
        )
    }

    val reobfArtifact = artifacts.add("default", file("$buildDir/reobfJar/output.jar")) {
        type = "jar"
        builtBy("reobfJar")
    }

    configure<PublishingExtension> {
        publications {
            register("mavenJava", MavenPublication::class) {
                artifact(reobfArtifact)
            }
        }

        repositories {
            maven {
                url = uri("file:///${rootProject.projectDir}/repo")
            }
        }
    }
}
