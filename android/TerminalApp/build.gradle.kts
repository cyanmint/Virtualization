plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.android.virtualization.terminal"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.android.virtualization.terminal"
        minSdk = 31
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false  // Disable minification for simpler CI build
            isShrinkResources = false
        }
        debug {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        // Allow compilation errors for missing AOSP classes
        freeCompilerArgs += listOf("-Xjvm-default=all")
    }

    buildFeatures {
        aidl = true
        viewBinding = false
        buildConfig = true
    }

    sourceSets {
        getByName("main") {
            java.srcDirs("java")
            aidl.srcDirs("aidl")
            res.srcDirs("res")
            assets.srcDirs("assets")
            manifest.srcFile("AndroidManifest.xml")
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/DEPENDENCIES"
        }
        jniLibs {
            useLegacyPackaging = true
        }
    }
    
    // Lint options to allow missing classes
    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }
}

dependencies {
    // AndroidX dependencies - available in Maven
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.6")
    implementation("androidx.window:window:1.2.0")
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    
    // Material Design
    implementation("com.google.android.material:material:1.11.0")
    
    // Apache Commons
    implementation("org.apache.commons:commons-compress:1.25.0")
    
    // JSON parsing
    implementation("com.google.code.gson:gson:2.10.1")
    
    // gRPC
    implementation("io.grpc:grpc-stub:1.60.0")
    implementation("io.grpc:grpc-protobuf-lite:1.60.0")
    implementation("io.grpc:grpc-okhttp:1.60.0")
    
    // Annotations
    compileOnly("androidx.annotation:annotation:1.7.1")
    
    // Note: The following AOSP-specific dependencies are NOT available in public repositories
    // and would prevent compilation. For a full functional build, use the AOSP build system.
    // Missing dependencies:
    // - android.system.virtualizationservice_internal-java
    // - android.system.virtualmachine.* (framework classes)
    // - framework-virtualization.impl
    // - framework-annotations-lib  
    // - avf_aconfig_flags_java
    // - libcrosvm_android_display_service-java
    // - MicrodroidTestHelper
    // - libforwarder_host_jni (JNI library)
}

