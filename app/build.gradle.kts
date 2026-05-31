plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    id("io.gitlab.arturbosch.detekt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "ru.practicum.shoppinglist"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "ru.practicum.shoppinglist"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.findByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeBom.get()
    }

    buildFeatures {
        compose = true
    }
}

detekt {
    toolVersion = "1.23.8"
    config = files("$rootDir/detekt-config.yml") // путь к вашему конфигу
    baseline = file("$rootDir/detekt-baseline.xml") // файл baseline (опционально)
    reports {
        html.required.set(true)
        html.outputLocation.set(file("build/reports/detekt.html"))
        xml.required.set(true)
        xml.outputLocation.set(file("build/reports/detekt.xml"))
    }
    // Исключить из анализа ресурсы и build
    tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
        exclude("**/resources/**", "**/build/**")
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Preview
    implementation(libs.androidx.compose.ui.tooling.preview)

    // Тестирование
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Room + KSP
    implementation(libs.androidx.room.runtime)
    ksp (libs.androidx.room.compiler)
    implementation (libs.androidx.room.ktx)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Lifecycle
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    detektPlugins(libs.detekt.formatting)

    // Coil
    implementation(libs.coil3)

    // Gson
    implementation(libs.gson)
    implementation(libs.converter.gson)

    // Hilt
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.androidx.hilt.compiler)

    // Dagger Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)



}