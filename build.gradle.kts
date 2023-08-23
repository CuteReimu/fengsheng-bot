plugins {
    kotlin("jvm") version "1.9.10"
    application
    kotlin("plugin.serialization") version "1.9.0"
    id("net.mamoe.mirai-console") version "2.15.0"
}

group = "com.fengsheng.bot"
version = "1.0.1"

repositories {
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
}

dependencies {
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
}

mirai {
    jvmTarget = JavaVersion.VERSION_17
}
