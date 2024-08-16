dependencies {
    implementation(project(":vpn-router-core"))
    implementation(project(":vpn-router-api"))
    implementation(project(":vpn-router-web"))
}

tasks.bootJar {
    enabled = true
}
