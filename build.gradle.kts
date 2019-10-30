import Build_gradle.DeobfExtension
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.minecraftforge.gradle.userdev.UserDevExtension
import net.minecraftforge.gradle.userdev.tasks.GenerateSRG
import net.minecraftforge.gradle.userdev.tasks.RenameJarInPlace
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

typealias DeobfExtension = NamedDomainObjectContainer<RenameJarInPlace>

buildscript {
    repositories {
        maven { url = uri("https://files.minecraftforge.net/maven") }
        jcenter()
        mavenCentral()
    }

    dependencies {
        classpath("org.jetbrains.kotlin", "kotlin-gradle-plugin", "${extra["kotlinVersion"]}")
        classpath("com.github.jengelman.gradle.plugins", "shadow", "${extra["shadowVersion"]}")
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
    apply(plugin = "maven-publish")

    dependencies {
        "compile"(kotlin("stdlib"))
        "compile"(kotlin("stdlib-jdk8"))
        "compile"(kotlin("reflect"))
        "testCompile"("junit:junit:4.12")
        "minecraft"("net.minecraftforge:forge:${extra["minecraftVersion"]}-${extra["forgeVersion"]}")
    }

    configure<UserDevExtension> {
        mappings(
            mapOf(
                "channel" to "${extra["mappingChannel"]}",
                "version" to "${extra["mappingVersion"]}"
            )
        )
    }

    configure<DeobfExtension> {
        maybeCreate("shadowJar").run {
            mappings = tasks.getByName<GenerateSRG>("createMcpToSrg").output
        }
    }

    configure<PublishingExtension> {
        publications {
            register("mavenJava", MavenPublication::class) {
                artifact(this@subprojects.artifacts.add("default", file("$buildDir/reobfShadowJar/output.jar")) {
                    type = "jar"
                    builtBy("reobfShadowJar")
                })
            }
        }

        repositories {
            maven {
                url = uri("file:///${rootProject.projectDir}/repo")
            }
        }
    }

    tasks {
        withType<KotlinCompile> {
            kotlinOptions.jvmTarget = "1.8"
        }

        withType<ShadowJar> {
            archiveClassifier.set("shadow")
            configurations = emptyList()
        }

        named("build") {
            dependsOn("reobfShadowJar")
        }
    }
}
