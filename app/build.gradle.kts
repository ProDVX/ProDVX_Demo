import java.io.FileInputStream
import java.util.Properties
import kotlin.collections.all

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

val keystoreproperties = rootProject.file("keystore.properties")
val keystore = Properties()
keystore.load(FileInputStream(keystoreproperties))

val secretsPropertiesFile = rootProject.file("secrets.properties")
val secrets = Properties()

if(secretsPropertiesFile.exists()) {
    secrets.load(secretsPropertiesFile.inputStream())
}



android {
    namespace = "com.prodvx.prodvx_demo"
    compileSdk = 36
    buildFeatures.buildConfig = true

    defaultConfig {
        applicationId = "com.prodvx.prodvx_demo"
        minSdk = 28
        //noinspection OldTargetApi
        targetSdk = 35
        versionCode = 9
        versionName = "1.4.3"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "API_TOKEN", "\"\"")
    }

    signingConfigs {
        create("AOSP-R25") {
//            if (keystore.containsKey("keyStoreFile"))
        }
    }

    buildTypes {
        debug {
            buildConfigField("boolean", "IS_DEVELOPMENT", "true")
            val debugToken = secrets.getProperty("API_TOKEN", "")
            buildConfigField("String", "API_TOKEN", "\"$debugToken\"")
            applicationIdSuffix = ".debug"
        }
        create("demo") {
            buildConfigField("boolean", "IS_DEVELOPMENT", "false")
            val token = secrets.getProperty("API_TOKEN", "")
            buildConfigField("String", "API_TOKEN", "\"${token}\"")
            matchingFallbacks += listOf("debug", "release")
            signingConfig = signingConfigs.getByName("debug")
        }
        release {
            buildConfigField("boolean", "IS_DEVELOPMENT", "false")
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
    buildFeatures {
        compose = true
    }
    applicationVariants.all {
        val variant = this
        outputs.all {
             val output = this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
             val buildType = variant.buildType.name
             val versionName = variant.versionName
             output.outputFileName= "ProDVX_Demo-${buildType}-v${versionName}.apk"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation("io.coil-kt:coil-compose:2.7.0")
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    runtimeOnly(libs.kotlinx.coroutines.core)

    implementation(project(":pledlibrary"))
}