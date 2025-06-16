// Top-level build file where you can add configuration options common to all sub-projects/modules.
import com.android.build.api.dsl.CommonExtension

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kover) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.owasp.dependency.checkid) apply false
}

subprojects {
    plugins.withId("org.jetbrains.kotlin.jvm") {
        apply(plugin = "org.jetbrains.kotlinx.kover")

        tasks.withType<Test> {
            useJUnitPlatform()
        }
    }
    executeDetekt()
    afterEvaluate {
        if (plugins.hasPlugin("com.android.application") || plugins.hasPlugin("com.android.library")) {
            configureLintOptions()
        }

        tasks.findByName("check")?.let { checkTask ->
            listOf("detekt", "lint").forEach { toolTask ->
                tasks.findByName(toolTask)?.let {
                    checkTask.dependsOn(it)
                }
            }
        }
    }
}

fun Project.executeDetekt() {
    apply(plugin = "io.gitlab.arturbosch.detekt")

    extensions.configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
        config.from(files("$rootDir/config/detekt/detekt.yml"))
        buildUponDefaultConfig = true
        allRules = false
        autoCorrect = true
        parallel = true
    }

    tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
        reports {
            html.required.set(true)
            xml.required.set(true)
            txt.required.set(false)
            sarif.required.set(false)
        }
    }
}

fun Project.configureLintOptions() {
    extensions.findByType(CommonExtension::class.java)?.let { androidExt ->
        androidExt.lint {
            abortOnError = true
            checkDependencies = false
            htmlReport = true
            xmlReport = true
        }
    }
}