import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val minecraftVersion: String by project
val kotlinVersion: String by project
val kotlinCoroutinesVersion: String by project

version = "$minecraftVersion-$kotlinVersion"

dependencies {
    compile("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    compile("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    compile("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    compile("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
    compile("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$kotlinCoroutinesVersion")
}

tasks {
    named<Jar>("jar") {
        manifest {
            attributes(
                mapOf(
                    "Specification-Title" to "klang",
                    "Specification-Vendor" to "fmorgner",
                    "Specification-Version" to "1",
                    "Implementation-Title" to project.name,
                    "Implementation-Version" to project.version,
                    "Implementation-Vendor" to "fmorgner",
                    "FMLModType" to "LANGPROVIDER"
                )
            )
        }
    }

    named<ShadowJar>("shadowJar") {
        archiveClassifier.set("")

        dependencies {
            include(dependency("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion"))
            include(dependency("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion"))
            include(dependency("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion"))
            include(dependency("org.jetbrains:annotations:13"))
            include(dependency("org.jetbrains.kotlinx:kotlinx-coroutines:$kotlinCoroutinesVersion"))
            include(dependency("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$kotlinCoroutinesVersion"))
        }

        configurations = listOf(project.configurations.compile.get())
    }

    register<Jar>("deobfJar") {
        from(sourceSets["main"].output)
        archiveClassifier.set("dev")
    }
}

reobf {
    all {
        input {
            tasks.named<ShadowJar>("shadowJar").get().archiveFile.get().asFile
        }
        dependsOn("shadowJar")
    }
}
