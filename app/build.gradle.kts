plugins {
    alias(libs.plugins.android.application)
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.prm_project"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.prm_project"
        minSdk = 34
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
    
    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    
    // MVVM Architecture Components
    implementation ("com.google.android.gms:play-services-auth:21.2.0")
    implementation(platform("com.google.firebase:firebase-bom:33.16.0"))
    implementation ("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-analytics")
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.8.7")
    implementation("androidx.lifecycle:lifecycle-livedata:2.8.7")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.8.7")
    implementation("androidx.activity:activity:1.9.3")
    
    // Room Database
    implementation("androidx.room:room-runtime:2.6.1")
    implementation(libs.room.common.jvm)
    annotationProcessor("androidx.room:room-compiler:2.6.1")
    
    // Security & Encryption
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    
    // Dependency Injection - Hilt (Java compatible version)
    implementation("com.google.dagger:hilt-android:2.44")
    annotationProcessor("com.google.dagger:hilt-compiler:2.44")
    
    // JSR 330 - Provides @Inject, @Singleton, etc.
    implementation("javax.inject:javax.inject:1")
    
    // Fragment
    implementation("androidx.fragment:fragment:1.6.2")
    
    // Navigation Component
    implementation("androidx.navigation:navigation-fragment:2.7.5")
    implementation("androidx.navigation:navigation-ui:2.7.5")
    
    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    
    // Reactive Programming
    implementation("io.reactivex.rxjava3:rxjava:3.1.8")
    implementation("io.reactivex.rxjava3:rxandroid:3.0.2")
    implementation("com.squareup.retrofit2:adapter-rxjava3:2.11.0")

    implementation("com.stripe:stripe-android:20.40.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")


    // Image Loading
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    
    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    
    // CardView
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.google.android.material:material:1.12.0")
    // SwipeRefreshLayout
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}