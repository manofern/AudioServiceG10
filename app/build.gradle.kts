plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android") version "1.8.22" // Adicione o plugin Kotlin com a versão mais recente
}

android {
    namespace = "com.manofern.audioserviceaula"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.manofern.audioserviceaula"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    // Forçar uma versão única do Kotlin
    configurations.all {
        resolutionStrategy {
            force("org.jetbrains.kotlin:kotlin-stdlib:1.8.22")
            force("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.8.22")
            force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.22")
        }
    }
}

dependencies {
    // Bibliotecas Android
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("com.google.android.material:material:1.6.0")
    implementation("androidx.activity:activity-ktx:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.3")
    implementation("androidx.core:core:1.8.0")
    implementation("androidx.media:media:1.6.0")
    implementation("androidx.media2:media2-session:1.1.0")

    // Testes unitários
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.11.0")
    testImplementation("org.mockito:mockito-inline:5.2.0") // Necessário para mockStatic
    testImplementation("org.robolectric:robolectric:4.6.1")

    // Testes instrumentados Android
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}
