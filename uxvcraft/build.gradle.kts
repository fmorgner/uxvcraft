import net.minecraftforge.gradle.userdev.tasks.GenerateSRG

version = "${rootProject.extra["modVersion"]}"

dependencies {
    implementation(project(":klang", "shadow"))
}

tasks {
    jar {
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

    register<Copy>("installMods") {
        dependsOn(":klang:shadowJar")
        from(configurations.compile)
        include("klang*")
        into(project.file("run/mods").canonicalPath)
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

reobf {
    maybeCreate("jar").mappings = tasks.createMcpToSrg.orNull?.output
}

artifacts {
    add("default", file("$buildDir/reobfJar/output.jar")) {
        type = "jar"
        builtBy("reobfJar")
    }
}

afterEvaluate {
    project.tasks["prepareRuns"].dependsOn(project.tasks["installMods"])
}
