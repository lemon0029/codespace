import com.google.protobuf.gradle.id

plugins {
    id("java")
    id("io.spring.dependency-management") version "1.1.7"
    id("com.google.protobuf") version "0.9.4"
}

group = "io.nullptr"
version = "1.0-SNAPSHOT"

extra["protobuf-java.version"] = "3.25.6"
extra["grpc.version"] = "1.70.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.grpc:grpc-core")
    implementation("io.grpc:grpc-protobuf")
    implementation("io.grpc:grpc-stub")
    implementation("io.grpc:grpc-netty")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

dependencyManagement {
    imports {
        mavenBom("io.grpc:grpc-bom:${property("grpc.version")}")
//        mavenBom("com.google.protobuf:protobuf-bom:${property("protobuf-java.version")}")
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${property("protobuf-java.version")}"
    }

    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:${property("grpc.version")}"
        }
    }

    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc") {
                    option("jakarta_omit")
                    option("@generated=omit")
                }
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()
}