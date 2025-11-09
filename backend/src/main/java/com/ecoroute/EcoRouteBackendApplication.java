package com.ecoroute;

import java.util.Arrays;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import com.ecoroute.database.DatabaseSeeder;

@SpringBootApplication
public class EcoRouteBackendApplication {
    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(EcoRouteBackendApplication.class, args);

        // Run DB seeder once at startup for the sqlite dev profile.
        // Seeder will run if either:
        //  - environment variable ECOROUTE_USE_SQLITE=true
        //  - the active Spring profile contains 'sqlite'
        String useSqlite = System.getenv("ECOROUTE_USE_SQLITE");
        Environment env = ctx.getEnvironment();
        boolean sqliteProfileActive = Arrays.asList(env.getActiveProfiles()).contains("sqlite");

        if ("true".equalsIgnoreCase(useSqlite) || sqliteProfileActive) {
            try {
                DatabaseSeeder.seed();
            } catch (Exception e) {
                // Log to stderr; don't prevent application from starting
                System.err.println("Database seeding failed: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
