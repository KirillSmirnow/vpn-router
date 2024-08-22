plugins {
    id("com.vaadin") version "24.4.8"
}

repositories {
    mavenCentral()
    maven(url = "https://vaadin.com/nexus/content/repositories/vaadin-addons/")
}

dependencies {
    implementation(project(":vpn-router-api"))
    implementation("com.vaadin:vaadin-spring-boot-starter:24.4.8")
    implementation("com.vaadin.componentfactory:togglebutton:3.0.0")
}
