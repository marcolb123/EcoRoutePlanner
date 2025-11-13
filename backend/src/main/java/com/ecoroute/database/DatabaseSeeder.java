package com.ecoroute.database;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ecoroute.model.Journey;
import com.ecoroute.model.Member;
import com.ecoroute.model.Reward;
import com.ecoroute.model.RewardMember;
import com.ecoroute.model.Role;
import com.ecoroute.model.Staff;
import com.ecoroute.model.User;
import com.ecoroute.model.Vehicle;

/**
 * Small database seeder used for development. It will ensure the SQLite file
 * exists, run the createTable() methods for all managers and insert some
 * sample data when tables are empty.
 */
public class DatabaseSeeder {

    private static final Path DB_PATH = Paths.get("data", "app.db");
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static void seed() {
        try {
            // Create tables
            UserDatabaseManager.createTable();
            RoleDatabaseManager.createTable();
            MemberDatabaseManager.createTable();
            StaffDatabaseManager.createTable();
            RewardDatabaseManager.createTable();
            JourneyDatabaseManager.createTable();
            RewardMemberDatabaseManager.createTable();
            RewardRedemptionDatabaseManager.createTable();
            VehicleDatabaseManager.createTable();
            UserFavoriteVehicleDatabaseManager.createTable();

            // One-time backfill: ensure admin accounts have ADMIN role and fill null roles
            try {
                List<User> existingUsers = UserDatabaseManager.getAll();
                for (User u : existingUsers) {
                    boolean isKnownAdmin = "admin_user".equalsIgnoreCase(u.getUsername())
                            || "manager_user".equalsIgnoreCase(u.getUsername());
                    if (isKnownAdmin && u.getRole() != User.UserRole.ADMIN) {
                        u.setRole(User.UserRole.ADMIN);
                        UserDatabaseManager.update(u);
                    }
                    if (u.getRole() == null) {
                        u.setRole(User.UserRole.USER);
                        UserDatabaseManager.update(u);
                    }
                }
            } catch (Exception ignored) {
                // Ignore errors during backfill
            }

            // Seed Users
            if (UserDatabaseManager.getAll().isEmpty()) {
                User u1 = new User();
                u1.setUsername("john_doe");
                u1.setEmail("john@example.com");
                u1.setPassword(passwordEncoder.encode("password123"));
                u1.setFirstName("John");
                u1.setLastName("Doe");
                u1.setPhoneNumber("1234567890");
                u1.setRole(User.UserRole.USER);
                u1.setEcoPoints(150);
                UserDatabaseManager.insert(u1);

                User u2 = new User();
                u2.setUsername("jane_smith");
                u2.setEmail("jane@example.com");
                u2.setPassword(passwordEncoder.encode("password123"));
                u2.setFirstName("Jane");
                u2.setLastName("Smith");
                u2.setPhoneNumber("0987654321");
                u2.setRole(User.UserRole.USER);
                u2.setEcoPoints(250);
                UserDatabaseManager.insert(u2);

                // Create users for staff
                User u3 = new User();
                u3.setUsername("admin_user");
                u3.setEmail("admin@example.com");
                u3.setPassword(passwordEncoder.encode("admin123"));
                u3.setFirstName("Admin");
                u3.setLastName("User");
                u3.setPhoneNumber("1111111111");
                u3.setRole(User.UserRole.ADMIN);
                u3.setEcoPoints(1000);
                UserDatabaseManager.insert(u3);

                User u4 = new User();
                u4.setUsername("manager_user");
                u4.setEmail("manager@example.com");
                u4.setPassword(passwordEncoder.encode("manager123"));
                u4.setFirstName("Manager");
                u4.setLastName("User");
                u4.setPhoneNumber("2222222222");
                u4.setRole(User.UserRole.ADMIN);
                u4.setEcoPoints(800);
                UserDatabaseManager.insert(u4);
                
                // Create a guest user
                User u5 = new User();
                u5.setUsername("guest");
                u5.setEmail("guest@example.com");
                u5.setPassword(passwordEncoder.encode("guest"));
                u5.setFirstName("Guest");
                u5.setLastName("User");
                u5.setPhoneNumber("0000000000");
                u5.setRole(User.UserRole.GUEST);
                u5.setEcoPoints(0);
                UserDatabaseManager.insert(u5);

                // Create members
                Member m1 = new Member();
                m1.setId(u1.getId());
                m1.setPoints(100);
                m1.setCustomerType("BRONZE");
                MemberDatabaseManager.insert(m1);

                Member m2 = new Member();
                m2.setId(u2.getId());
                m2.setPoints(500);
                m2.setCustomerType("SILVER");
                MemberDatabaseManager.insert(m2);
            }

            // Seed Roles and Staff
            if (RoleDatabaseManager.getAll().isEmpty()) {
                // Create and insert roles
                Role adminRole = new Role();
                adminRole.setRoleName("Administrator");
                // Remove setPermissions - check Role model for correct method
                RoleDatabaseManager.insert(adminRole);

                Role managerRole = new Role();
                managerRole.setRoleName("Manager");
                // Remove setPermissions - check Role model for correct method
                RoleDatabaseManager.insert(managerRole);
                
                // Retrieve roles with their generated IDs
                List<Role> allRoles = RoleDatabaseManager.getAll();
                Role retrievedAdminRole = allRoles.stream()
                    .filter(r -> "Administrator".equals(r.getRoleName()))
                    .findFirst()
                    .orElse(null);
                Role retrievedManagerRole = allRoles.stream()
                    .filter(r -> "Manager".equals(r.getRoleName()))
                    .findFirst()
                    .orElse(null);

                // Get the staff users we created earlier
                List<User> allUsers = UserDatabaseManager.getAll();
                User u3 = allUsers.stream()
                    .filter(u -> "admin_user".equals(u.getUsername()))
                    .findFirst()
                    .orElse(null);
                User u4 = allUsers.stream()
                    .filter(u -> "manager_user".equals(u.getUsername()))
                    .findFirst()
                    .orElse(null);

                // Create staff users and link to roles
                if (u3 != null && retrievedAdminRole != null) {
                    Staff staff1 = new Staff();
                    staff1.setId(u3.getId());
                    staff1.setStaffRole(retrievedAdminRole);
                    staff1.setEmploymentStatus("FULL_TIME");
                    StaffDatabaseManager.insert(staff1);
                }

                if (u4 != null && retrievedManagerRole != null) {
                    Staff staff2 = new Staff();
                    staff2.setId(u4.getId());
                    staff2.setStaffRole(retrievedManagerRole);
                    staff2.setEmploymentStatus("PART_TIME");
                    StaffDatabaseManager.insert(staff2);
                }
            }

            // Seed rewards
            if (RewardDatabaseManager.getAll().isEmpty()) {
                // Reward 1: Free Coffee
                Reward r1 = new Reward();
                r1.setRewardName("Free Coffee");
                r1.setRewardCost(50);
                r1.setDescription("Redeem for a free coffee at partner cafés");
                r1.setStock(100);
                RewardDatabaseManager.insert(r1);

                // Reward 2: Train Ticket Discount
                Reward r2 = new Reward();
                r2.setRewardName("Train Ticket Discount");
                r2.setRewardCost(200);
                r2.setDescription("10% off single-journey ticket");
                r2.setStock(50);
                RewardDatabaseManager.insert(r2);

                // Reward 3: Eco Water Bottle
                Reward r3 = new Reward();
                r3.setRewardName("Eco Water Bottle");
                r3.setRewardCost(150);
                r3.setDescription("Reusable stainless steel water bottle");
                r3.setStock(75);
                RewardDatabaseManager.insert(r3);

                // Reward 4: Public Transport Monthly Pass
                Reward r4 = new Reward();
                r4.setRewardName("Public Transport Monthly Pass");
                r4.setRewardCost(500);
                r4.setDescription("Full month unlimited public transportation");
                r4.setStock(25);
                RewardDatabaseManager.insert(r4);

                // Reward 5: Bike Rental Voucher
                Reward r5 = new Reward();
                r5.setRewardName("Bike Rental Voucher");
                r5.setRewardCost(100);
                r5.setDescription("3-day bike rental voucher at partner shops");
                r5.setStock(60);
                RewardDatabaseManager.insert(r5);

                // Reward 6: Organic Lunch Box
                Reward r6 = new Reward();
                r6.setRewardName("Organic Lunch Box");
                r6.setRewardCost(80);
                r6.setDescription("Fresh organic lunch from sustainable restaurants");
                r6.setStock(120);
                RewardDatabaseManager.insert(r6);

                // Reward 7: Eco Shopping Bag Set
                Reward r7 = new Reward();
                r7.setRewardName("Eco Shopping Bag Set");
                r7.setRewardCost(120);
                r7.setDescription("Set of 3 reusable shopping bags");
                r7.setStock(90);
                RewardDatabaseManager.insert(r7);

                // Reward 8: Plant a Tree Certificate
                Reward r8 = new Reward();
                r8.setRewardName("Plant a Tree Certificate");
                r8.setRewardCost(300);
                r8.setDescription("We'll plant a tree in your name");
                r8.setStock(40);
                RewardDatabaseManager.insert(r8);

                // Reward 9: Car Sharing Credit
                Reward r9 = new Reward();
                r9.setRewardName("Car Sharing Credit");
                r9.setRewardCost(250);
                r9.setDescription("$25 credit for eco car sharing services");
                r9.setStock(55);
                RewardDatabaseManager.insert(r9);

                // Reward 10: Green Energy Discount
                Reward r10 = new Reward();
                r10.setRewardName("Green Energy Discount");
                r10.setRewardCost(400);
                r10.setDescription("One month 15% discount on green energy bill");
                r10.setStock(30);
                RewardDatabaseManager.insert(r10);
            }

            // Seed journeys (only if none exist)
            if (JourneyDatabaseManager.getAll().isEmpty()) {
                // Get user IDs for seeding
                List<User> allUsers = UserDatabaseManager.getAll();
                Integer user1Id = allUsers.stream().filter(u -> "john_doe".equals(u.getUsername())).findFirst().map(User::getId).orElse(1);
                Integer user2Id = allUsers.stream().filter(u -> "jane_smith".equals(u.getUsername())).findFirst().map(User::getId).orElse(2);

                // Journey 1: John's bicycle commute
                Journey j1 = new Journey();
                j1.setUserId(user1Id);
                j1.setVehicle("BICYCLING");
                j1.setDistance(java.math.BigDecimal.valueOf(5.2));
                j1.setEmissions(java.math.BigDecimal.valueOf(0));
                j1.setEmissionsReduced(java.math.BigDecimal.valueOf(0.998));
                j1.setTravelDate(LocalDateTime.now().minusDays(5));
                j1.setTravelingFrom("123 Main St, Springfield");
                j1.setTravelingTo("Springfield Office Park");
                JourneyDatabaseManager.insert(j1);

                // Journey 2: Jane's bus trip
                Journey j2 = new Journey();
                j2.setUserId(user2Id);
                j2.setVehicle("BUS");
                j2.setDistance(java.math.BigDecimal.valueOf(12.5));
                j2.setEmissions(java.math.BigDecimal.valueOf(1.3125));
                j2.setEmissionsReduced(java.math.BigDecimal.valueOf(1.0875));
                j2.setTravelDate(LocalDateTime.now().minusDays(4));
                j2.setTravelingFrom("456 Elm St, Springfield");
                j2.setTravelingTo("Downtown Springfield");
                JourneyDatabaseManager.insert(j2);

                // Journey 3: John's walking trip
                Journey j3 = new Journey();
                j3.setUserId(user1Id);
                j3.setVehicle("WALKING");
                j3.setDistance(java.math.BigDecimal.valueOf(2.1));
                j3.setEmissions(java.math.BigDecimal.valueOf(0));
                j3.setEmissionsReduced(java.math.BigDecimal.valueOf(0.403));
                j3.setTravelDate(LocalDateTime.now().minusDays(3));
                j3.setTravelingFrom("789 Pine Ave, Riverside");
                j3.setTravelingTo("Riverside Shopping Center");
                JourneyDatabaseManager.insert(j3);

                // Journey 4: Jane's transit commute
                Journey j4 = new Journey();
                j4.setUserId(user2Id);
                j4.setVehicle("TRANSIT");
                j4.setDistance(java.math.BigDecimal.valueOf(18.3));
                j4.setEmissions(java.math.BigDecimal.valueOf(0.7503));
                j4.setEmissionsReduced(java.math.BigDecimal.valueOf(2.7657));
                j4.setTravelDate(LocalDateTime.now().minusDays(2));
                j4.setTravelingFrom("321 Oak Boulevard, Greenfield");
                j4.setTravelingTo("Greenfield Station");
                JourneyDatabaseManager.insert(j4);

                // Journey 5: John's bicycle trip
                Journey j5 = new Journey();
                j5.setUserId(user1Id);
                j5.setVehicle("BICYCLING");
                j5.setDistance(java.math.BigDecimal.valueOf(8.7));
                j5.setEmissions(java.math.BigDecimal.valueOf(0));
                j5.setEmissionsReduced(java.math.BigDecimal.valueOf(1.670));
                j5.setTravelDate(LocalDateTime.now().minusDays(1));
                j5.setTravelingFrom("654 Maple Drive, Lakewood");
                j5.setTravelingTo("Lakewood University");
                JourneyDatabaseManager.insert(j5);

                // Journey 6: John's second bicycle trip
                Journey j6 = new Journey();
                j6.setUserId(user1Id);
                j6.setVehicle("BICYCLING");
                j6.setDistance(java.math.BigDecimal.valueOf(6.3));
                j6.setEmissions(java.math.BigDecimal.valueOf(0));
                j6.setEmissionsReduced(java.math.BigDecimal.valueOf(1.210));
                j6.setTravelDate(LocalDateTime.now().minusDays(1));
                j6.setTravelingFrom("Springfield Office Park");
                j6.setTravelingTo("Springfield Gym");
                JourneyDatabaseManager.insert(j6);

                // Journey 7: David's bus journey
                Journey j7 = new Journey();
                j7.setVehicle("BUS");
                j7.setDistance(java.math.BigDecimal.valueOf(15.2));
                j7.setEmissions(java.math.BigDecimal.valueOf(1.596));
                j7.setEmissionsReduced(java.math.BigDecimal.valueOf(1.322));
                j7.setTravelDate(LocalDateTime.now());
                j7.setTravelingFrom("Greenfield Station");
                j7.setTravelingTo("Greenfield Mall");
                JourneyDatabaseManager.insert(j7);

                // Journey 8: Emma's transit trip
                Journey j8 = new Journey();
                j8.setVehicle("TRANSIT");
                j8.setDistance(java.math.BigDecimal.valueOf(22.4));
                j8.setEmissions(java.math.BigDecimal.valueOf(0.918));
                j8.setEmissionsReduced(java.math.BigDecimal.valueOf(3.383));
                j8.setTravelDate(LocalDateTime.now());
                j8.setTravelingFrom("Lakewood University");
                j8.setTravelingTo("City Center");
                JourneyDatabaseManager.insert(j8);
            }

            // Seed reward-member links (if empty)
            if (RewardMemberDatabaseManager.getAll().isEmpty()) {
                // Alice redeemed Free Coffee
                RewardMember rm1 = new RewardMember();
                rm1.setMemberId(1);
                rm1.setRewardId(1);
                rm1.setRedeemedAt(LocalDateTime.now().minusDays(10).toString());
                RewardMemberDatabaseManager.insert(rm1);

                // Bob redeemed Bike Rental Voucher
                RewardMember rm2 = new RewardMember();
                rm2.setMemberId(2);
                rm2.setRewardId(5);
                rm2.setRedeemedAt(LocalDateTime.now().minusDays(7).toString());
                RewardMemberDatabaseManager.insert(rm2);

                // David redeemed Train Ticket Discount
                RewardMember rm3 = new RewardMember();
                rm3.setMemberId(4);
                rm3.setRewardId(2);
                rm3.setRedeemedAt(LocalDateTime.now().minusDays(5).toString());
                RewardMemberDatabaseManager.insert(rm3);

                // Emma redeemed Eco Water Bottle
                RewardMember rm4 = new RewardMember();
                rm4.setMemberId(5);
                rm4.setRewardId(3);
                rm4.setRedeemedAt(LocalDateTime.now().minusDays(3).toString());
                RewardMemberDatabaseManager.insert(rm4);

                // Alice redeemed Organic Lunch Box
                RewardMember rm5 = new RewardMember();
                rm5.setMemberId(1);
                rm5.setRewardId(6);
                rm5.setRedeemedAt(LocalDateTime.now().minusDays(2).toString());
                RewardMemberDatabaseManager.insert(rm5);
            }

            // Seed reward redemptions (purchase history)
            if (RewardRedemptionDatabaseManager.getAll().isEmpty()) {
                // Alice's purchases
                com.ecoroute.model.RewardRedemption red1 = new com.ecoroute.model.RewardRedemption();
                red1.setUserId(1);
                red1.setRewardId(1);
                red1.setRewardName("Free Coffee");
                red1.setPointsCost(50);
                red1.setRedeemedAt(LocalDateTime.now().minusDays(10));
                RewardRedemptionDatabaseManager.insert(red1);

                com.ecoroute.model.RewardRedemption red2 = new com.ecoroute.model.RewardRedemption();
                red2.setUserId(1);
                red2.setRewardId(6);
                red2.setRewardName("Organic Lunch Box");
                red2.setPointsCost(80);
                red2.setRedeemedAt(LocalDateTime.now().minusDays(2));
                RewardRedemptionDatabaseManager.insert(red2);

                // Bob's purchase
                com.ecoroute.model.RewardRedemption red3 = new com.ecoroute.model.RewardRedemption();
                red3.setUserId(2);
                red3.setRewardId(5);
                red3.setRewardName("Bike Rental Voucher");
                red3.setPointsCost(100);
                red3.setRedeemedAt(LocalDateTime.now().minusDays(7));
                RewardRedemptionDatabaseManager.insert(red3);

                // David's purchases
                com.ecoroute.model.RewardRedemption red4 = new com.ecoroute.model.RewardRedemption();
                red4.setUserId(4);
                red4.setRewardId(2);
                red4.setRewardName("Train Ticket Discount");
                red4.setPointsCost(200);
                red4.setRedeemedAt(LocalDateTime.now().minusDays(5));
                RewardRedemptionDatabaseManager.insert(red4);

                com.ecoroute.model.RewardRedemption red5 = new com.ecoroute.model.RewardRedemption();
                red5.setUserId(4);
                red5.setRewardId(9);
                red5.setRewardName("Car Sharing Credit");
                red5.setPointsCost(250);
                red5.setRedeemedAt(LocalDateTime.now().minusDays(1));
                RewardRedemptionDatabaseManager.insert(red5);

                // Emma's purchase
                com.ecoroute.model.RewardRedemption red6 = new com.ecoroute.model.RewardRedemption();
                red6.setUserId(5);
                red6.setRewardId(3);
                red6.setRewardName("Eco Water Bottle");
                red6.setPointsCost(150);
                red6.setRedeemedAt(LocalDateTime.now().minusDays(3));
                RewardRedemptionDatabaseManager.insert(red6);
            }

            // Seed vehicles (if empty)
            if (VehicleDatabaseManager.getAll().isEmpty()) {
                seedVehicles();
            }

        } catch (Exception e) {
            System.err.println("Error during database seeding:");
            e.printStackTrace();
            throw new RuntimeException("Database seeding failed", e);
        }
    }

    private static void seedVehicles() {
        // Tesla Models (Electric)
        Vehicle v1 = new Vehicle();
        v1.setBrand("Tesla");
        v1.setModel("Model 3");
        v1.setYear(2024);
        v1.setFuelType("ELECTRIC");
        v1.setCo2EmissionsGPerKm(java.math.BigDecimal.ZERO);
        v1.setElectricRangeKm(java.math.BigDecimal.valueOf(491));
        v1.setBatteryCapacityKwh(java.math.BigDecimal.valueOf(60));
        v1.setVehicleType("SEDAN");
        VehicleDatabaseManager.insert(v1);

        Vehicle v2 = new Vehicle();
        v2.setBrand("Tesla");
        v2.setModel("Model Y");
        v2.setYear(2024);
        v2.setFuelType("ELECTRIC");
        v2.setCo2EmissionsGPerKm(java.math.BigDecimal.ZERO);
        v2.setElectricRangeKm(java.math.BigDecimal.valueOf(533));
        v2.setBatteryCapacityKwh(java.math.BigDecimal.valueOf(75));
        v2.setVehicleType("SUV");
        VehicleDatabaseManager.insert(v2);

        // Toyota Models (Hybrid & Petrol)
        Vehicle v3 = new Vehicle();
        v3.setBrand("Toyota");
        v3.setModel("Prius");
        v3.setYear(2024);
        v3.setFuelType("HYBRID");
        v3.setCo2EmissionsGPerKm(java.math.BigDecimal.valueOf(92));
        v3.setFuelConsumptionLPer100Km(java.math.BigDecimal.valueOf(4.0));
        v3.setElectricRangeKm(java.math.BigDecimal.valueOf(40));
        v3.setBatteryCapacityKwh(java.math.BigDecimal.valueOf(8.8));
        v3.setVehicleType("HATCHBACK");
        VehicleDatabaseManager.insert(v3);

        Vehicle v4 = new Vehicle();
        v4.setBrand("Toyota");
        v4.setModel("Corolla");
        v4.setYear(2024);
        v4.setFuelType("PETROL");
        v4.setCo2EmissionsGPerKm(java.math.BigDecimal.valueOf(120));
        v4.setFuelConsumptionLPer100Km(java.math.BigDecimal.valueOf(5.3));
        v4.setVehicleType("SEDAN");
        VehicleDatabaseManager.insert(v4);

        Vehicle v5 = new Vehicle();
        v5.setBrand("Toyota");
        v5.setModel("RAV4");
        v5.setYear(2024);
        v5.setFuelType("PETROL");
        v5.setCo2EmissionsGPerKm(java.math.BigDecimal.valueOf(162));
        v5.setFuelConsumptionLPer100Km(java.math.BigDecimal.valueOf(6.9));
        v5.setVehicleType("SUV");
        VehicleDatabaseManager.insert(v5);

        // BMW Models (Electric & Plug-in Hybrid)
        Vehicle v6 = new Vehicle();
        v6.setBrand("BMW");
        v6.setModel("i4");
        v6.setYear(2024);
        v6.setFuelType("ELECTRIC");
        v6.setCo2EmissionsGPerKm(java.math.BigDecimal.ZERO);
        v6.setElectricRangeKm(java.math.BigDecimal.valueOf(590));
        v6.setBatteryCapacityKwh(java.math.BigDecimal.valueOf(83.9));
        v6.setVehicleType("SEDAN");
        VehicleDatabaseManager.insert(v6);

        Vehicle v7 = new Vehicle();
        v7.setBrand("BMW");
        v7.setModel("X5");
        v7.setYear(2024);
        v7.setFuelType("PLUG_IN_HYBRID");
        v7.setCo2EmissionsGPerKm(java.math.BigDecimal.valueOf(52));
        v7.setFuelConsumptionLPer100Km(java.math.BigDecimal.valueOf(2.3));
        v7.setElectricRangeKm(java.math.BigDecimal.valueOf(87));
        v7.setBatteryCapacityKwh(java.math.BigDecimal.valueOf(24));
        v7.setVehicleType("SUV");
        VehicleDatabaseManager.insert(v7);

        Vehicle v8 = new Vehicle();
        v8.setBrand("BMW");
        v8.setModel("3 Series");
        v8.setYear(2024);
        v8.setFuelType("DIESEL");
        v8.setCo2EmissionsGPerKm(java.math.BigDecimal.valueOf(138));
        v8.setFuelConsumptionLPer100Km(java.math.BigDecimal.valueOf(5.2));
        v8.setVehicleType("SEDAN");
        VehicleDatabaseManager.insert(v8);

        // Volkswagen Models (Electric & Petrol)
        Vehicle v9 = new Vehicle();
        v9.setBrand("Volkswagen");
        v9.setModel("ID.4");
        v9.setYear(2024);
        v9.setFuelType("ELECTRIC");
        v9.setCo2EmissionsGPerKm(java.math.BigDecimal.ZERO);
        v9.setElectricRangeKm(java.math.BigDecimal.valueOf(418));
        v9.setBatteryCapacityKwh(java.math.BigDecimal.valueOf(77));
        v9.setVehicleType("SUV");
        VehicleDatabaseManager.insert(v9);

        Vehicle v10 = new Vehicle();
        v10.setBrand("Volkswagen");
        v10.setModel("Golf");
        v10.setYear(2024);
        v10.setFuelType("PETROL");
        v10.setCo2EmissionsGPerKm(java.math.BigDecimal.valueOf(130));
        v10.setFuelConsumptionLPer100Km(java.math.BigDecimal.valueOf(5.7));
        v10.setVehicleType("HATCHBACK");
        VehicleDatabaseManager.insert(v10);

        Vehicle v11 = new Vehicle();
        v11.setBrand("Volkswagen");
        v11.setModel("Tiguan");
        v11.setYear(2024);
        v11.setFuelType("DIESEL");
        v11.setCo2EmissionsGPerKm(java.math.BigDecimal.valueOf(155));
        v11.setFuelConsumptionLPer100Km(java.math.BigDecimal.valueOf(5.9));
        v11.setVehicleType("SUV");
        VehicleDatabaseManager.insert(v11);

        // Ford Models (Electric & Petrol)
        Vehicle v12 = new Vehicle();
        v12.setBrand("Ford");
        v12.setModel("Mustang Mach-E");
        v12.setYear(2024);
        v12.setFuelType("ELECTRIC");
        v12.setCo2EmissionsGPerKm(java.math.BigDecimal.ZERO);
        v12.setElectricRangeKm(java.math.BigDecimal.valueOf(491));
        v12.setBatteryCapacityKwh(java.math.BigDecimal.valueOf(91));
        v12.setVehicleType("SUV");
        VehicleDatabaseManager.insert(v12);

        Vehicle v13 = new Vehicle();
        v13.setBrand("Ford");
        v13.setModel("F-150");
        v13.setYear(2024);
        v13.setFuelType("PETROL");
        v13.setCo2EmissionsGPerKm(java.math.BigDecimal.valueOf(295));
        v13.setFuelConsumptionLPer100Km(java.math.BigDecimal.valueOf(12.4));
        v13.setVehicleType("TRUCK");
        VehicleDatabaseManager.insert(v13);

        Vehicle v14 = new Vehicle();
        v14.setBrand("Ford");
        v14.setModel("Focus");
        v14.setYear(2024);
        v14.setFuelType("PETROL");
        v14.setCo2EmissionsGPerKm(java.math.BigDecimal.valueOf(128));
        v14.setFuelConsumptionLPer100Km(java.math.BigDecimal.valueOf(5.6));
        v14.setVehicleType("HATCHBACK");
        VehicleDatabaseManager.insert(v14);

        // Nissan Models (Electric & Petrol)
        Vehicle v15 = new Vehicle();
        v15.setBrand("Nissan");
        v15.setModel("Leaf");
        v15.setYear(2024);
        v15.setFuelType("ELECTRIC");
        v15.setCo2EmissionsGPerKm(java.math.BigDecimal.ZERO);
        v15.setElectricRangeKm(java.math.BigDecimal.valueOf(364));
        v15.setBatteryCapacityKwh(java.math.BigDecimal.valueOf(62));
        v15.setVehicleType("HATCHBACK");
        VehicleDatabaseManager.insert(v15);

        Vehicle v16 = new Vehicle();
        v16.setBrand("Nissan");
        v16.setModel("Qashqai");
        v16.setYear(2024);
        v16.setFuelType("PETROL");
        v16.setCo2EmissionsGPerKm(java.math.BigDecimal.valueOf(149));
        v16.setFuelConsumptionLPer100Km(java.math.BigDecimal.valueOf(6.3));
        v16.setVehicleType("SUV");
        VehicleDatabaseManager.insert(v16);

        // Hyundai Models (Electric & Hybrid)
        Vehicle v17 = new Vehicle();
        v17.setBrand("Hyundai");
        v17.setModel("Ioniq 5");
        v17.setYear(2024);
        v17.setFuelType("ELECTRIC");
        v17.setCo2EmissionsGPerKm(java.math.BigDecimal.ZERO);
        v17.setElectricRangeKm(java.math.BigDecimal.valueOf(480));
        v17.setBatteryCapacityKwh(java.math.BigDecimal.valueOf(77.4));
        v17.setVehicleType("SUV");
        VehicleDatabaseManager.insert(v17);

        Vehicle v18 = new Vehicle();
        v18.setBrand("Hyundai");
        v18.setModel("Tucson Hybrid");
        v18.setYear(2024);
        v18.setFuelType("HYBRID");
        v18.setCo2EmissionsGPerKm(java.math.BigDecimal.valueOf(120));
        v18.setFuelConsumptionLPer100Km(java.math.BigDecimal.valueOf(5.2));
        v18.setElectricRangeKm(java.math.BigDecimal.valueOf(50));
        v18.setBatteryCapacityKwh(java.math.BigDecimal.valueOf(13.8));
        v18.setVehicleType("SUV");
        VehicleDatabaseManager.insert(v18);

        // Audi Models (Electric & Diesel)
        Vehicle v19 = new Vehicle();
        v19.setBrand("Audi");
        v19.setModel("e-tron GT");
        v19.setYear(2024);
        v19.setFuelType("ELECTRIC");
        v19.setCo2EmissionsGPerKm(java.math.BigDecimal.ZERO);
        v19.setElectricRangeKm(java.math.BigDecimal.valueOf(488));
        v19.setBatteryCapacityKwh(java.math.BigDecimal.valueOf(93.4));
        v19.setVehicleType("SPORTS");
        VehicleDatabaseManager.insert(v19);

        Vehicle v20 = new Vehicle();
        v20.setBrand("Audi");
        v20.setModel("A4");
        v20.setYear(2024);
        v20.setFuelType("DIESEL");
        v20.setCo2EmissionsGPerKm(java.math.BigDecimal.valueOf(142));
        v20.setFuelConsumptionLPer100Km(java.math.BigDecimal.valueOf(5.4));
        v20.setVehicleType("SEDAN");
        VehicleDatabaseManager.insert(v20);

        // Mercedes Models (Electric & Petrol)
        Vehicle v21 = new Vehicle();
        v21.setBrand("Mercedes-Benz");
        v21.setModel("EQS");
        v21.setYear(2024);
        v21.setFuelType("ELECTRIC");
        v21.setCo2EmissionsGPerKm(java.math.BigDecimal.ZERO);
        v21.setElectricRangeKm(java.math.BigDecimal.valueOf(676));
        v21.setBatteryCapacityKwh(java.math.BigDecimal.valueOf(107.8));
        v21.setVehicleType("SEDAN");
        VehicleDatabaseManager.insert(v21);

        Vehicle v22 = new Vehicle();
        v22.setBrand("Mercedes-Benz");
        v22.setModel("C-Class");
        v22.setYear(2024);
        v22.setFuelType("PETROL");
        v22.setCo2EmissionsGPerKm(java.math.BigDecimal.valueOf(158));
        v22.setFuelConsumptionLPer100Km(java.math.BigDecimal.valueOf(6.8));
        v22.setVehicleType("SEDAN");
        VehicleDatabaseManager.insert(v22);

        // Honda Models (Petrol & Hybrid)
        Vehicle v23 = new Vehicle();
        v23.setBrand("Honda");
        v23.setModel("Civic");
        v23.setYear(2024);
        v23.setFuelType("PETROL");
        v23.setCo2EmissionsGPerKm(java.math.BigDecimal.valueOf(126));
        v23.setFuelConsumptionLPer100Km(java.math.BigDecimal.valueOf(5.5));
        v23.setVehicleType("SEDAN");
        VehicleDatabaseManager.insert(v23);

        Vehicle v24 = new Vehicle();
        v24.setBrand("Honda");
        v24.setModel("CR-V Hybrid");
        v24.setYear(2024);
        v24.setFuelType("HYBRID");
        v24.setCo2EmissionsGPerKm(java.math.BigDecimal.valueOf(125));
        v24.setFuelConsumptionLPer100Km(java.math.BigDecimal.valueOf(5.4));
        v24.setElectricRangeKm(java.math.BigDecimal.valueOf(45));
        v24.setBatteryCapacityKwh(java.math.BigDecimal.valueOf(10.5));
        v24.setVehicleType("SUV");
        VehicleDatabaseManager.insert(v24);

        // Mazda Models (Petrol)
        Vehicle v25 = new Vehicle();
        v25.setBrand("Mazda");
        v25.setModel("CX-5");
        v25.setYear(2024);
        v25.setFuelType("PETROL");
        v25.setCo2EmissionsGPerKm(java.math.BigDecimal.valueOf(167));
        v25.setFuelConsumptionLPer100Km(java.math.BigDecimal.valueOf(7.1));
        v25.setVehicleType("SUV");
        VehicleDatabaseManager.insert(v25);

        // Kia Models (Electric & Petrol)
        Vehicle v26 = new Vehicle();
        v26.setBrand("Kia");
        v26.setModel("EV6");
        v26.setYear(2024);
        v26.setFuelType("ELECTRIC");
        v26.setCo2EmissionsGPerKm(java.math.BigDecimal.ZERO);
        v26.setElectricRangeKm(java.math.BigDecimal.valueOf(528));
        v26.setBatteryCapacityKwh(java.math.BigDecimal.valueOf(77.4));
        v26.setVehicleType("SUV");
        VehicleDatabaseManager.insert(v26);

        Vehicle v27 = new Vehicle();
        v27.setBrand("Kia");
        v27.setModel("Sportage");
        v27.setYear(2024);
        v27.setFuelType("DIESEL");
        v27.setCo2EmissionsGPerKm(java.math.BigDecimal.valueOf(152));
        v27.setFuelConsumptionLPer100Km(java.math.BigDecimal.valueOf(5.8));
        v27.setVehicleType("SUV");
        VehicleDatabaseManager.insert(v27);

        // Peugeot Models (Petrol & Diesel)
        Vehicle v28 = new Vehicle();
        v28.setBrand("Peugeot");
        v28.setModel("208");
        v28.setYear(2024);
        v28.setFuelType("PETROL");
        v28.setCo2EmissionsGPerKm(java.math.BigDecimal.valueOf(112));
        v28.setFuelConsumptionLPer100Km(java.math.BigDecimal.valueOf(4.9));
        v28.setVehicleType("HATCHBACK");
        VehicleDatabaseManager.insert(v28);

        Vehicle v29 = new Vehicle();
        v29.setBrand("Peugeot");
        v29.setModel("3008");
        v29.setYear(2024);
        v29.setFuelType("DIESEL");
        v29.setCo2EmissionsGPerKm(java.math.BigDecimal.valueOf(145));
        v29.setFuelConsumptionLPer100Km(java.math.BigDecimal.valueOf(5.5));
        v29.setVehicleType("SUV");
        VehicleDatabaseManager.insert(v29);

        // Renault Models (Electric & Petrol)
        Vehicle v30 = new Vehicle();
        v30.setBrand("Renault");
        v30.setModel("Zoe");
        v30.setYear(2024);
        v30.setFuelType("ELECTRIC");
        v30.setCo2EmissionsGPerKm(java.math.BigDecimal.ZERO);
        v30.setElectricRangeKm(java.math.BigDecimal.valueOf(395));
        v30.setBatteryCapacityKwh(java.math.BigDecimal.valueOf(52));
        v30.setVehicleType("HATCHBACK");
        VehicleDatabaseManager.insert(v30);
    }
}
