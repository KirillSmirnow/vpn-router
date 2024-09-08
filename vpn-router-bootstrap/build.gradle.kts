dependencies {
    implementation(project(":vpn-router-core"))
    implementation(project(":vpn-router-api"))
    implementation(project(":vpn-router-web"))
    implementation(project(":vpn-router-alice"))
}

tasks.bootJar {
    enabled = true
}
