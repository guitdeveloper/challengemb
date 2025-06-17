plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.junit5)
}

buildscript {
    dependencies {
        classpath(libs.test.mannodermaus.junit.v5.plugin)
    }
}

android {
    namespace = "br.com.challenge.core.tests"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["runnerBuilder"] = "de.mannodermaus.junit5.AndroidJUnit5Builder"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    testOptions {
        animationsDisabled = true
        unitTests {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
        }
        junitPlatform {
            instrumentationTests.includeExtensions.set(true)
        }
    }
}

dependencies {
    api(libs.test.assertj.core)
    api(libs.test.compose.junit4)
    api(libs.test.core.testing)
    api(libs.test.coroutines)
    api(libs.test.espresso.contrib)
    api(libs.test.espresso.core)
    api(libs.test.espresso.intents)
    api(libs.test.junit.ext)
    api(libs.test.junit.platform)
    api(libs.test.junit.v4.core)
    api(libs.test.junit.v4.engine)
    api(libs.test.junit5.api)
    api(libs.test.junit5.engine)
    api(libs.test.junit5.params)
    api(libs.test.koin)
    api(libs.test.koin.junit4)
    api(libs.test.mannodermaus.junit5.core)
    api(libs.test.mannodermaus.junit5.runner)
    api(libs.test.mockito.inline)
    api(libs.test.mockito.kotlin)
    api(libs.test.mockk)
    api(libs.test.mockk.android)
    api(libs.test.room.testing)
    api(libs.test.turbine)
}