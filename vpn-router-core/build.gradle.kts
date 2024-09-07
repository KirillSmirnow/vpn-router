dependencies {
    implementation(project(":vpn-router-api"))
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")
    implementation("com.maxmind.geoip2:geoip2:4.2.0")
}
