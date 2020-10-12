dependencies {
    implementation("org.junit.jupiter:junit-jupiter:5.4.2")
    implementation("org.junit.jupiter:junit-jupiter:5.4.2")
    kapt(project(":zally-core"))

    compile(project(":zally-core"))
    compile("de.mpg.mpi-inf:javatools:1.1")

    testCompile(project(":zally-test"))
}
