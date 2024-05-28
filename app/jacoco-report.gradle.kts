package plugins

val exclusions = listOf(
    "**/R.class",
    "**/R\$*.class",
    "**/BuildConfig.*",
    "**/Manifest*.*",
    "**/*Test*.*",
    "android/**/*.*",
    "**/*Binding.class",
    "**/*Binding*.*",
    "**/*Args.class",
    "**/*Args.Builder.class",
    "**/*Builder.class",
    "**/*Factory*",
    "**/*Injector*",
    "**/*Module*",
    "**/*Component*",
    "**/*Extension*",
    "**/*Base*",
    "**/*Util*",
    "**/*App*",
    "**/*Preference*"

)

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("createDebugUnitTestCoverageReport", "createDebugAndroidTestCoverageReport")

    group = "reporting"
    description = "Code Jacoco coverage report for both Android and Unit tests."

    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    sourceDirectories.setFrom(layout.projectDirectory.dir("src/main/java"))
    classDirectories.setFrom(files(
        fileTree(layout.buildDirectory.dir("intermediates/javac/debug/")) {
            exclude(exclusions)
        },// generated classes
        fileTree(layout.buildDirectory.dir("tmp/kotlin-classes/debug/")) {
            exclude(exclusions)
        }
    ))
    executionData.setFrom(files(
        fileTree(layout.buildDirectory) {
            include(listOf("**/*.exec", "**/*.ec"))
        }
    ))
}