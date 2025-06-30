plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("kotlin-kapt")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22" // Asegúrate de la versión
}

android {
    namespace = "com.jesus.pokemaker"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.jesus.pokemaker"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    // Dependencias de Lifecycle (ViewModel, LiveData)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")

    // Retrofit (librería base)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    // ¡IMPORTANTE! ELIMINADA: Conflicto con kotlinx.serialization-converter
    // implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Para CardView (necesario para la UI)
    implementation("androidx.cardview:cardview:1.0.0") // O la versión más reciente

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")

    // kotlinx.serialization (Json y su convertidor para Retrofit)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3") // O la versión más reciente
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0") // O la versión más reciente

    // Room (para la base de datos local)
    implementation("androidx.room:room-runtime:2.6.1") // O la versión más reciente
    kapt("androidx.room:room-compiler:2.6.1")         // O la versión más reciente
    implementation("androidx.room:room-ktx:2.6.1")     // O la versión más reciente

    // Coil (para carga de imágenes)
    implementation("io.coil-kt:coil:2.7.0") // O la versión más reciente

    // Dependencias de AndroidX que probablemente vienen de tu libs.versions.toml
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    // ¡CORRECCIÓN CRÍTICA AQUÍ! Para que funcione 'by viewModels()' en Activity
    implementation("androidx.activity:activity-ktx:1.9.0") // Usa la versión más reciente y estable
    implementation("androidx.fragment:fragment-ktx:1.7.0") // También es buena práctica para Fragmentos
    implementation(libs.androidx.constraintlayout)

    // Dependencias de test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
