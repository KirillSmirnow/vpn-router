plugins {
    id("com.vaadin") version "24.4.8"
}

dependencies {
    implementation(project(":vpn-router-api"))
    implementation("com.vaadin:vaadin-spring-boot-starter:24.4.8")
}
