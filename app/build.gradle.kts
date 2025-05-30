plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.navigation.safe.args)
    alias(libs.plugins.detekt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.practicum.playlistmaker"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.practicum.playlistmaker"
        minSdk = 31
        targetSdk = 35
        versionCode = 16
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            abiFilters.add("arm64-v8a") // Добавляем поддержку архитектуры arm64-v8a
            abiFilters.add("x86_64") // Добавляем поддержку архитектуры x86_64
        }
    }

    // Настройка подписи релизных сборок
    signingConfigs {
        create("release") {
            // Используем переменные окружения (для CI/CD)
            storeFile = file(System.getenv("RELEASE_KEYSTORE_FILE") ?: "debug.keystore")
            storePassword = System.getenv("RELEASE_KEYSTORE_PASSWORD")
            keyAlias = System.getenv("RELEASE_KEY_ALIAS")
            keyPassword = System.getenv("RELEASE_KEY_PASSWORD")
        }
    }

    buildTypes {
        debug {
            buildConfigField("Boolean", "DEBUG", "true")
            applicationIdSuffix = ".debug"
            isMinifyEnabled = false
        }
        release {
            buildConfigField("Boolean", "DEBUG", "false")
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
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
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
}

detekt {
    toolVersion = libs.versions.detekt.get()
    config.setFrom("${rootDir}/config/detekt/detekt.yml")
    buildUponDefaultConfig = true  // Совмещает с дефолтными правилами
    autoCorrect = true
    source.setFrom("src/main/java")
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.preference)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.viewpager2)
    implementation(libs.material)
    implementation(libs.glide)
    implementation(libs.gson)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.timber)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    detektPlugins(libs.detekt.cli)
    detektPlugins(libs.detekt.formatting)

    ksp(libs.glide.compiler)
    ksp(libs.room.compiler)
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    reports {
        html.required.set(true)
    }
}