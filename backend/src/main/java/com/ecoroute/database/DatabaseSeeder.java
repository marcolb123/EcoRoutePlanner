package com.ecoroute.database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import com.ecoroute.model.Journey;
import com.ecoroute.model.Member;
import com.ecoroute.model.Reward;
import com.ecoroute.model.RewardMember;
import com.ecoroute.model.Role;
import com.ecoroute.model.Staff;
import com.ecoroute.model.User;

/**
 * Small database seeder used for development. It will ensure the SQLite file
 * exists, run the createTable() methods for all managers and insert some
 * sample data when tables are empty.
 */
public class DatabaseSeeder {

	private static final Path DB_PATH = Paths.get("data", "app.db");

	public static void seed() {
		try {
			boolean dbExists = Files.exists(DB_PATH);
			if (!dbExists) {
				// create parent directories; the SQLite file will be created on first connection
				Files.createDirectories(DB_PATH.getParent());
			}

			// Ensure all tables exist
			RoleDatabaseManager.createTable();
			UserDatabaseManager.createTable();
			MemberDatabaseManager.createTable();
			StaffDatabaseManager.createTable();
			RewardDatabaseManager.createTable();
			JourneyDatabaseManager.createTable();
			RewardMemberDatabaseManager.createTable();

			// Seed roles
			if (RoleDatabaseManager.getAll().isEmpty()) {
				Role r1 = new Role(); r1.setRoleName("USER");
				Role r2 = new Role(); r2.setRoleName("ADMIN");
				Role r3 = new Role(); r3.setRoleName("GUEST");
				RoleDatabaseManager.insert(r1);
				RoleDatabaseManager.insert(r2);
				RoleDatabaseManager.insert(r3);
			}

			// Seed users
			if (UserDatabaseManager.getAll().isEmpty()) {
				User u1 = new User();
				u1.setFirstName("Alice");
				u1.setLastName("Anderson");
				u1.setUsername("alice");
				u1.setPassword("password");
				u1.setEmail("alice@example.com");
				u1.setRegisteredAt(LocalDateTime.now());
				UserDatabaseManager.insert(u1);

				User u2 = new User();
				u2.setFirstName("Bob");
				u2.setLastName("Baker");
				u2.setUsername("bob");
				u2.setPassword("password");
				u2.setEmail("bob@example.com");
				u2.setRegisteredAt(LocalDateTime.now());
				UserDatabaseManager.insert(u2);

				User u3 = new User();
				u3.setFirstName("Jim");
				u3.setLastName("Jumbo");
				u3.setUsername("jim");
				u3.setPassword("password");
				u3.setEmail("jim@example.com");
				u3.setRegisteredAt(LocalDateTime.now());
				UserDatabaseManager.insert(u3);
			}

			// Seed members
			if (MemberDatabaseManager.getAll().isEmpty()) {
				Member m1 = new Member(u1, 0, "regular");
				MemberDatabaseManager.insert(m1);

				Member m2 = new Member(u2, 0, "occasional");
				MemberDatabaseManager.insert(m2);
			}

			// Seed staff
			if (StaffDatabaseManager.getAll().isEmpty()) {
				Staff s = new Staff(u3, Role.ADMIN, "full-time");
				StaffDatabaseManager.insert(s);
			}

			// Seed rewards
			if (RewardDatabaseManager.getAll().isEmpty()) {
				Reward r1 = new Reward();
				r1.setRewardName("Free Coffee");
				r1.setRewardCost(50);
				r1.setDescription("Redeem for a free coffee at partner cafés");
				r1.setStock(100);
				RewardDatabaseManager.insert(r1);

				Reward r2 = new Reward();
				r2.setRewardName("Train Ticket Discount");
				r2.setRewardCost(200);
				r2.setDescription("10% off single-journey ticket");
				r2.setStock(50);
				RewardDatabaseManager.insert(r2);
			}

			// Seed journeys (only if none exist)
			if (JourneyDatabaseManager.getAll().isEmpty()) {
				Journey j1 = new Journey();
				j1.setVehicle("bicycle");
				j1.setDistance(java.math.BigDecimal.valueOf(3.2));
				j1.setEmissions(java.math.BigDecimal.valueOf(0));
				j1.setEmissionsReduced(java.math.BigDecimal.valueOf(1.5));
				j1.setTravelDate(LocalDateTime.now());
				j1.setTravelingFrom("Home");
				j1.setTravelingTo("Work");
				JourneyDatabaseManager.insert(j1);
			}

			// Seed reward-member links (if empty)
			if (RewardMemberDatabaseManager.getAll().isEmpty()) {
				RewardMember rm = new RewardMember();
				rm.setMemberId(1);
				rm.setRewardId(1);
				rm.setRedeemedAt(LocalDateTime.now().toString());
				RewardMemberDatabaseManager.insert(rm);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
