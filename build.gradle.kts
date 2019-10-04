plugins {
    `java-library`
    `maven-publish`
}

group = "eu.mikroskeem"
version = "1.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies  {
    // None!
}

val jar by tasks.getting(Jar::class) {
    manifest {
        attributes(
                "Automatic-Module-Name" to "eu.mikroskeem.jvmhiccup"
        )
    }
}

val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifact(sourcesJar)
        }
    }

    repositories {
        mavenLocal()
        if (rootProject.hasProperty("wutee.repository.deploy.username") && rootProject.hasProperty("wutee.repository.deploy.password")) {
            maven("https://repo.wut.ee/repository/mikroskeem-repo").credentials {
                username = rootProject.properties["wutee.repository.deploy.username"]!! as String
                password = rootProject.properties["wutee.repository.deploy.password"]!! as String
            }
        }
    }
}