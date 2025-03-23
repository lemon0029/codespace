import com.google.protobuf.gradle.id

plugins {
    id("java")
    id("org.springframework.boot") version "3.4.4"
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

extra["springGrpcVersion"] = "0.5.0"

dependencies {
    implementation("io.grpc:grpc-services")
    implementation("org.springframework.grpc:spring-grpc-spring-boot-starter")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.grpc:spring-grpc-dependencies:${property("springGrpcVersion")}")
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc"
    }

    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java"
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