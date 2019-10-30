version = "${extra["minecraftVersion"]}-${extra["kotlinVersion"]}"

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
        configurations = listOf(
            project.configurations["compile"]
        )

        dependencies {
            include(dependency("org.jetbrains.kotlin:kotlin-stdlib"))
            include(dependency("org.jetbrains.kotlin:kotlin-reflect"))
        }
    }
}