plugins {
    id("com.vaadin") version "24.4.8"
}

dependencies {
    implementation(project(":vpn-router-api"))
    implementation(project(":vpn-router-core"))
    implementation("com.vaadin:vaadin-spring-boot-starter:24.4.8")
    implementation("com.vaadin.componentfactory:togglebutton:3.0.0")
}
