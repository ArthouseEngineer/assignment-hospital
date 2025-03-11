plugins {
	java
	id("org.springframework.boot") version "3.2.0"
	id("io.spring.dependency-management") version "1.1.4"
	id("org.liquibase.gradle") version "2.2.0"
}

group = "nl.gerimedica"
version = "1.0.0"

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(21))
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	// Spring Boot Dependencies
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-actuator")

	// Database Dependencies
	implementation("org.liquibase:liquibase-core")
	runtimeOnly("org.postgresql:postgresql")

	// Metrics and Monitoring
	implementation("io.micrometer:micrometer-registry-prometheus")

	// Documentation
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")

	// MapStruct and Lombok
	implementation("org.mapstruct:mapstruct:1.5.5.Final")
	compileOnly("org.projectlombok:lombok")

	// Annotation Processors
	annotationProcessor("org.projectlombok:lombok")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
	annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")

	// Testing Dependencies
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("com.h2database:h2")
	testImplementation("org.testcontainers:postgresql:1.19.3")
	testImplementation("org.testcontainers:junit-jupiter:1.19.3")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// Liquibase configuration
liquibase {
	activities {
		register("main") {
			arguments = mapOf(
				"changeLogFile" to "src/main/resources/db/changelog/db.changelog-master.xml",
				"url" to "jdbc:postgresql://localhost:5432/gerimedica_hospital",
				"username" to "postgres",
				"password" to "postgres"
			)
		}
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

// Task to generate Liquibase changelog
tasks.register<org.liquibase.gradle.LiquibaseTask>("generateChangeLog") {
	dependsOn("classes")
	doFirst {
		args = listOf(
			"--diffTypes=tables,views,columns,indexes,foreignkeys,primarykeys,uniqueconstraints",
			"--changeLogFile=src/main/resources/db/changelog/db.changelog-master.xml"
		)
	}
}