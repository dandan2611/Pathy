import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.papermc.paperweight.userdev") version "1.3.8"
}

group = "dev.dandan2611"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = "https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven(url = "https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
}

dependencies {
    paperDevBundle("1.19.3-R0.1-SNAPSHOT")
    api("org.jetbrains:annotations:23.0.0")
    implementation("com.google.code.gson:gson:2.10.1")
}

val targetJavaVersion = JavaVersion.VERSION_17
java {
    sourceCompatibility = targetJavaVersion
    targetCompatibility = targetJavaVersion
    if (JavaVersion.current() < targetJavaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion.majorVersion))
    }
}

tasks.withType(JavaCompile::class).configureEach {
    if (targetJavaVersion >= JavaVersion.VERSION_1_10 || JavaVersion.current().isJava10Compatible) {
        options.release.set(targetJavaVersion.majorVersion.toInt())
    }

    options.encoding = Charsets.UTF_8.name()
}

tasks.assemble {
    dependsOn(tasks.reobfJar)
}

tasks.withType(PublishToMavenRepository::class.java) {
    dependsOn(tasks.reobfJar)
}

tasks.processResources.configure {
    // Define properties
    val props = mapOf(Pair("version", version))

    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

tasks.withType(JavaCompile::class).configureEach {
    if (targetJavaVersion >= JavaVersion.VERSION_1_10 || JavaVersion.current().isJava10Compatible) {
        options.release.set(targetJavaVersion.majorVersion.toInt())
    }
    options.encoding = Charsets.UTF_8.name()
}
