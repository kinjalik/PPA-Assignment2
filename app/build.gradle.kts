plugins {
    id("org.jetbrains.kotlin.jvm") version "1.8.10"
    application
    id("antlr")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.8.1")
    testImplementation(kotlin("test"))

    implementation("com.google.guava:guava:31.1-jre")
    implementation("io.github.rchowell:dotlin:1.0.2")

    antlr("org.antlr:antlr4:4.12.0")
}

application {
    mainClass.set("org.example.AppKt")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.compileKotlin {
    dependsOn(tasks.generateGrammarSource)
}
tasks.compileTestKotlin {
    dependsOn(tasks.generateTestGrammarSource)
}

tasks.generateGrammarSource {
    maxHeapSize = "64m"
    arguments = arguments + listOf("-visitor", "-long-messages")
}