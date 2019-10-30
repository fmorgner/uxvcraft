version = "${rootProject.extra["minecraftVersion"]}-${rootProject.extra["kotlinVersion"]}"

dependencies {
    compile(
        group = "org.jetbrains.kotlin",
        name = "kotlin-stdlib",
        version = "${rootProject.extra["kotlinVersion"]}"
    )
    compile(
        group = "org.jetbrains.kotlin",
        name = "kotlin-stdlib-jdk8",
        version = "${rootProject.extra["kotlinVersion"]}"
    )
    compile(
        group = "org.jetbrains.kotlin",
        name = "kotlin-reflect",
        version = "${rootProject.extra["kotlinVersion"]}"
    )
    compile(
        group = "org.jetbrains.kotlinx",
        name = "kotlinx-coroutines-core",
        version = "${rootProject.extra["kotlinCoroutinesVersion"]}"
    )
    compile(
        group = "org.jetbrains.kotlinx",
        name = "kotlinx-coroutines-jdk8",
        version = "${rootProject.extra["kotlinCoroutinesVersion"]}"
    )
}

tasks {
    jar {
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

    shadowJar {
        archiveClassifier.set("shadow")

        configurations = listOf(
            project.configurations.compile.get()
        )

        dependencies {
            include(dependency("org.jetbrains.kotlin:kotlin-stdlib"))
            include(dependency("org.jetbrains.kotlin:kotlin-stdlib-jdk8"))
            include(dependency("org.jetbrains.kotlin:kotlin-reflect"))
            include(dependency("org.jetbrains.kotlinx:kotlinx-coroutines"))
            include(dependency("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8"))
        }
    }
}

reobf {
    maybeCreate("shadowJar").mappings = tasks.createMcpToSrg.orNull?.output
}

artifacts {
    add("default", file("$buildDir/reobfShadowJar/output.jar")) {
        type = "jar"
        builtBy("reobfShadowJar")
    }

    add("shadow", tasks.shadowJar.get().archiveFile.get()) {
        type = "jar"
        builtBy(tasks.shadowJar)
    }
}
