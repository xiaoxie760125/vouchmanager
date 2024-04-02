plugins {
    id("java")
}

group = "com.sqds"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.itextpdf:itextpdf:5.5.13.2")
    implementation("com.itextpdf:itext-asian:5.2.0")
    implementation("com.itextpdf.tool:xmlworker:5.5.13.2")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}