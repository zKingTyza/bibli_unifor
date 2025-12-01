plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
    id("kotlin-parcelize")

}

android {
    namespace = "com.example.bibliuniforav2"
    compileSdk = 34

    buildFeatures {
        viewBinding = true
    }

    defaultConfig {
        applicationId = "com.example.bibliuniforav2"
        minSdk = 30
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation("com.google.code.gson:gson:2.10.1")
    // ViewModel Lifecycle KTX (fornece viewModelScope)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0") // Versão mais recente
    // Lifecycle runtime KTX (geralmente bom ter junto)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0") // Versão mais recente
    // Para baixar e exibir as imagens (Glide)
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // Dependências do Firebase com versões EXATAS (sem BOM)
    implementation("com.google.firebase:firebase-firestore-ktx:25.1.4")
    implementation("com.google.firebase:firebase-storage-ktx:21.0.0")

    // Para baixar e exibir as imagens (Glide)
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // *** NOVAS DEPENDÊNCIAS PARA GEMINI AI ***
    // MANTENHA ESTA LINHA (ou a referência do seu 'libs.versions.toml')
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Demais dependências do seu projeto
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.recyclerview)
    implementation(libs.generativeai)
    implementation("com.google.firebase:firebase-auth-ktx:21.0.1")

    // Dependências de teste
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}