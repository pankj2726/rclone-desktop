plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "what.thefcuk.pankxu"
    compileSdk = 36

    defaultConfig {
        applicationId = "what.thefcuk.pankxu"
        minSdk = 26
        targetSdk = 36
        versionCode = 10000          // P2-01: major*10000 + minor*100 + patch -> 1.0.0
        versionName = "1.0.0"
    }

    // P8-01 / R3: the keystore never lives in the repo and is never read from
    // a committed file. Paths and passwords arrive only as environment variables.
    signingConfigs {
        create("release") {
            val ksPath = System.getenv("PANKXU_KEYSTORE_PATH")
            if (!ksPath.isNullOrBlank()) {
                storeFile = file(ksPath)
                storePassword = System.getenv("PANKXU_KEYSTORE_PASSWORD")
                keyAlias = System.getenv("PANKXU_KEY_ALIAS")
                keyPassword = System.getenv("PANKXU_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            // Unsigned when no keystore is supplied, so CI can still build and test.
            if (!System.getenv("PANKXU_KEYSTORE_PATH").isNullOrBlank()) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    lint {
        abortOnError = false     // reported, not fatal, until first clean CI run
        warningsAsErrors = false
    }
}

// Compile as UTF-8 regardless of the host locale. Caught for real: em dashes in
// comments failed to compile under a C-locale javac default (DECISIONS D-09).
// Must sit outside the android { } block: `tasks` is a project property.
tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

dependencies {
    implementation(libs.androidx.browser)
    implementation(libs.androidbrowserhelper)

    testImplementation(libs.junit)
}
