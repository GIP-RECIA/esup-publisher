/**
 * Copyright (C) 2014 Esup Portail http://www.esup-portail.org
 * @Author (C) 2012 Julien Gribonvald <julien.gribonvald@recia.fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *                 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.esupportail.publisher;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.esupportail.publisher.annotation.ExcludeFromJacocoGeneratedReport;
import org.esupportail.publisher.config.Constants;

import com.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.core.env.SimpleCommandLinePropertySource;

@SpringBootApplication
@EnableConfigurationProperties({ LiquibaseProperties.class, MailProperties.class})
@ExcludeFromJacocoGeneratedReport
public class Application {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	private final Environment env;

	public Application(Environment env) {
		this.env = env;
	}

	/**
	 * Initializes publisher.
	 * <p/>
	 * Spring profiles can be configured with a program arguments --spring.profiles.active=your-active-profile
	 * <p/>
	 */
	@PostConstruct
	public void initApplication() throws IOException {
        if (env.getActiveProfiles().length == 0) {
            log.warn("No Spring profile configured, running with default configuration");
        } else {
            log.info("Running with Spring profile(s) : {}", Arrays.toString(env.getActiveProfiles()));
            final List<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
            if (activeProfiles.contains(Constants.SPRING_PROFILE_DEVELOPMENT) && activeProfiles.contains(Constants.SPRING_PROFILE_PRODUCTION)) {
                log.error("You have misconfigured your application! " +
                    "It should not run with both the 'dev' and 'prod' profiles at the same time.");
            }
            if (activeProfiles.contains(Constants.SPRING_PROFILE_PRODUCTION) && activeProfiles.contains(Constants.SPRING_PROFILE_FAST)) {
                log.error("You have misconfigured your application! " +
                    "It should not run with both the 'prod' and 'fast' profiles at the same time.");
            }
			if (activeProfiles.contains(Constants.SPRING_PROFILE_TEST) && activeProfiles.contains(Constants.SPRING_PROFILE_PRODUCTION)) {
				log.error("You have misconfigured your application! " +
						"It should not run with both the 'prod' and 'test' profiles at the same time.");
			}
			if (activeProfiles.contains(Constants.SPRING_PROFILE_TEST) && activeProfiles.contains(Constants.SPRING_PROFILE_DEVELOPMENT)) {
				log.error("You have misconfigured your application! " +
						"It should not run with both the 'dev' and 'test' profiles at the same time.");
			}
        }
	}

	/**
	 * Main method, used to run the application.
	 */
	public static void main(String[] args) throws UnknownHostException {
		SpringApplication app = new SpringApplication(Application.class);
		app.setBannerMode(Mode.OFF);

		SimpleCommandLinePropertySource source = new SimpleCommandLinePropertySource(args);
		log.info("Properties from command line " + Arrays.toString(args));

		// Check if the selected profile has been set as argument.
		// if not the development profile will be added
		addDefaultProfile(app, source);
		addLiquibaseScanPackages();
		Environment env = app.run(args).getEnvironment();
		log.info("Access URLs:\n----------------------------------------------------------\n\t"
				+ "Local: \t\thttp://127.0.0.1:{}\n\t"
				+ "External: \thttp://{}:{}\n\t"
				+ "Profiles: \t{}\n----------------------------------------------------------",
				env.getProperty("server.port"), InetAddress.getLocalHost().getHostAddress(),
				env.getProperty("server.port"), env.getProperty("spring.profiles.active"));
	}

	/**
	 * Set a default profile if it has not been set
	 */
	private static void addDefaultProfile(SpringApplication app, SimpleCommandLinePropertySource source) {
        if (!source.containsProperty("spring.profiles.active") &&
            !System.getenv().containsKey("SPRING_PROFILES_ACTIVE")) {
            app.setAdditionalProfiles(Constants.SPRING_PROFILE_DEVELOPMENT);
        }
	}

	/**
	 * Set the liquibases.scan.packages to avoid an exception from ServiceLocator.
	 */
	private static void addLiquibaseScanPackages() {
		System.setProperty(
				"liquibase.scan.packages",
				Joiner.on(",").join("liquibase.change", "liquibase.database", "liquibase.parser",
						"liquibase.precondition", "liquibase.datatype", "liquibase.serializer",
						"liquibase.sqlgenerator", "liquibase.executor", "liquibase.snapshot", "liquibase.logging",
						"liquibase.diff", "liquibase.structure", "liquibase.structurecompare", "liquibase.lockservice",
						"liquibase.ext", "liquibase.changelog"));
	}
}
