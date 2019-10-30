import net.minecraftforge.gradle.userdev.UserDevExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    extra.apply {
        set("kotlinVersion", "1.3.50")
        set("kotlinCoroutinesVersion", "1.3.2")
        set("minecraftVersion", "1.14.4")
        set("minecraftForgeVersion", "28.1.0")
        set("minecraftMappingVersion", "20190719-1.14.3")
        set("minecraftMappingChannel", "snapshot")
        set("shadowVersion", "5.1.0")
        set("modVersion", "1.0.0")
    }

    repositories {
        maven { url = uri("https://files.minecraftforge.net/maven") }
        jcenter()
        mavenCentral()
    }

    dependencies {
        classpath("org.jetbrains.kotlin", "kotlin-gradle-plugin", "${project.extra["kotlinVersion"]}")
        classpath("com.github.jengelman.gradle.plugins", "shadow", "${project.extra["shadowVersion"]}")
        classpath("net.minecraftforge.gradle", "ForgeGradle", "3.+") {
            isChanging = true
        }
    }
}

subprojects {
    group = "ch.felixmorgner"

    apply(plugin = "com.github.johnrengelman.shadow")
    apply(plugin = "net.minecraftforge.gradle")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.gradle.java")
    apply(plugin = "maven-publish")

    dependencies {
        "testCompile"("junit:junit:4.12")
        "minecraft"(group="net.minecraftforge", name="forge", version = "${rootProject.extra["minecraftVersion"]}-${rootProject.extra["minecraftForgeVersion"]}")
    }

    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    configure<UserDevExtension> {
        mappings(
            mapOf(
                "channel" to "${rootProject.extra["minecraftMappingChannel"]}",
                "version" to "${rootProject.extra["minecraftMappingVersion"]}"
            )
        )
    }

    tasks {
        withType<KotlinCompile> {
            kotlinOptions.jvmTarget = "1.8"
        }

        named("build") {
            dependsOn("reobfJar")
        }
    }

    configure<PublishingExtension> {
        publications {
            register("mavenJava", MavenPublication::class) {
                artifacts.all {
                    artifact(this)
                }
            }
        }

        repositories {
            maven {
                url = uri("file:///${rootProject.projectDir}/repo")
            }
        }
    }
}
