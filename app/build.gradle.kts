plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.explorador3d_hiro_projeto_final"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.explorador3d_hiro_projeto_final"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // Bibliotecas Android básicas com material desing
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.activity:activity-ktx:1.9.3")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    // WebView, exibe as páginas HTML com os modelos 3D e imagens em realidade aumentada
    implementation(libs.androidx.webkit)
    implementation("androidx.webkit:webkit:1.8.0")
    // Retrofit, para requisições HTTP à API do LibreTranslate
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // Conversor Gson (ler JSON automaticamente)
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // OkHttp, verificação de respostas da API
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    implementation(libs.androidx.activity)

}
