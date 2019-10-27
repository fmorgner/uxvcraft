import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val modVersion: String by project

version = modVersion

configurations {
    create("mod")
}

dependencies {
    compile(project(":klang"))
    "mod"(project(":klang"))
}

tasks {

    named<Jar>("jar") {
        manifest {
            attributes(
                mapOf(
                    "Specification-Title" to "uxvcraft",
                    "Specification-Vendor" to "fmorgner",
                    "Specification-Version" to "1",
                    "Implementation-Title" to project.name,
                    "Implementation-Version" to project.version,
                    "Implementation-Vendor" to "fmorgner"
                )
            )
        }
    }

    named<ShadowJar>("shadowJar") {
        archiveClassifier.set("")

        dependencies {
            include(project(":klang"))
        }

        configurations = listOf(project.configurations.compile.get())
    }

    register<Jar>("deobfJar") {
        from(sourceSets["main"].output)
        archiveClassifier.set("dev")
    }

    register<Copy>("installMods") {
        from(configurations["mod"])
        include("klang*")
        into(project.file("run/mods").canonicalPath)
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

minecraft {

    runs {
        val configProperties = mapOf(
            "forge.logging.markers" to "CORE,SCAN,REGISTRIES",
            "forge.logging.console.level" to "info"
        )
        val configWorkdir = project.file("run").canonicalPath

        create("client") {
            properties(configProperties)
            workingDirectory = configWorkdir
            source(sourceSets["main"])
        }

        create("server") {
            properties(configProperties)
            workingDirectory = configWorkdir
            source(sourceSets["main"])
        }

        create("data") {
            properties(configProperties)
            workingDirectory = configWorkdir
            args(
                listOf(
                    "--mod",
                    "uxvcraft",
                    "--all",
                    "--output",
                    project.file("src/generated/resources/").canonicalPath
                )
            )
            source(sourceSets["main"])
        }
    }
}

afterEvaluate {
    project.tasks["prepareRuns"].dependsOn(project.tasks["installMods"])
}
