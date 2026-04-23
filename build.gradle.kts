plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation("io.github.bonigarcia:selenium-jupiter:4.3.6")
    implementation("org.seleniumhq.selenium:selenium-java:4.41.0")
    implementation("org.seleniumhq.selenium:selenium-chrome-driver:4.41.0")
    implementation("com.codeborne:selenide:7.15.0")
    implementation("io.github.bonigarcia:webdrivermanager:6.3.3")
    implementation("org.seleniumhq.selenium:selenium-api:4.41.0")
    testImplementation("io.rest-assured:rest-assured:4.4.0")
    testImplementation("org.wiremock:wiremock-standalone:3.13.2")
}


tasks.test {
    useJUnitPlatform()
}