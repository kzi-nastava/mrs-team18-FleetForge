import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
}


fun getIpAddress(): String {
    val properties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        properties.load(FileInputStream(localPropertiesFile))
    }
    return properties.getProperty("ip_addr", "192.168.0.159")
}

fun getMapboxApiKey(): String {
    val properties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        properties.load(FileInputStream(localPropertiesFile))
    }
    return properties.getProperty("MAPBOX_API_KEY", "")
}


android {
    namespace = "com.ognjen.fleetforge"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.ognjen.fleetforge"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        buildConfigField("String","IP_ADDR","\""+getIpAddress()+"\"")
        buildConfigField("String", "MAPBOX_API_KEY", "\"${getMapboxApiKey()}\"")


        manifestPlaceholders["ip_addr"] = getIpAddress()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled=true
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
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation("com.google.code.gson:gson:2.12.1")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Fragment support
    implementation("androidx.fragment:fragment:1.6.2")

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // Navigation Component (for bottom nav)
    implementation("androidx.navigation:navigation-fragment:2.7.5")
    implementation("androidx.navigation:navigation-ui:2.7.5")

    implementation("org.osmdroid:osmdroid-android:6.1.18")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation ("com.github.bumptech.glide:glide:4.16.0"    )
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")

    implementation("com.google.code.gson:gson:2.8.7")
    implementation("com.squareup.retrofit2:retrofit:2.3.0")
    implementation("com.squareup.retrofit2:converter-gson:2.3.0")
    implementation("com.squareup.okhttp3:logging-interceptor:3.12.1")
}