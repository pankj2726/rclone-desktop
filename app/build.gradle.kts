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
        // S0-03: one shared DEBUG key, committed on purpose, so consecutive CI builds can
        // upgrade over each other. Without it every run invents a new debug key, adb refuses
        // the install with INSTALL_FAILED_UPDATE_INCOMPATIBLE, and upgrade testing (S4-06)
        // is impossible without wiping the app first.
        //
        // A debug key is not a secret: the password below is the Android convention and
        // anything signed with it is explicitly not a release artifact. The release key is
        // still supplied only through environment variables, never from the repository.
        create("sharedDebug") {
            storeFile = rootProject.file("ci/debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
        create("release") {
            val ksPath = System.getenv("PANKXU_KEYSTORE_PATH")
            if (!ksPath.isNullOrBlank()) {
                storeFile = file(ksPath)
                storePassword = System.getenv("PANKXU_KEYSTORE_PASSWORD")
                keyAlias = System.getenv("PANKXU_KEY_ALIAS")
                keyPassword = System.getenv("PANKXU_KEY_PASSWORD")
            }
            // Roadmap section 7 asks for v2 AND v3 verification. AGP signs with v2 only by
            // default, so v3 was false until this was set (observed in run 42513037427).
            // v1 (JAR signing) is pointless above minSdk 24 and only slows installs.
            enableV1Signing = false
            enableV2Signing = true
            enableV3Signing = true
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            if (rootProject.file("ci/debug.keystore").exists()) {
                signingConfig = signingConfigs.getByName("sharedDebug")
            }
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
