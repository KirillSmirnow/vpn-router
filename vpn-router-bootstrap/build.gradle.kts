dependencies {
    implementation(project(":vpn-router-core"))
    implementation(project(":vpn-router-api"))
    implementation(project(":vpn-router-web"))
    implementation(project(":vpn-router-alice"))
    implementation("com.vaadin:vaadin-spring-boot-starter:24.4.8")
    compileOnly("com.vaadin:vaadin-push:8.26.0")
}

tasks.bootJar {
    enabled = true
}
