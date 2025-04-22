dependencies {
    implementation("org.springframework.boot:spring-boot-starter-oauth2-authorization-server")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
