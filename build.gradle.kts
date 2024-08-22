plugins {
    java
    id("io.freefair.lombok") version "8.7.1"
    id("org.springframework.boot") version "3.3.2"
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "io.freefair.lombok")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    repositories {
        mavenCentral()
        maven(url = "https://vaadin.com/nexus/content/repositories/vaadin-addons/")
    }

    dependencies {
        implementation("org.springframework.boot:spring-boot-starter")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    tasks.bootJar {
        enabled = false
    }
}
