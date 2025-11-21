# Eco Route Planner

A sustainable travel application that helps users plan eco-friendly routes, track their environmental impact, and earn rewards for sustainable transportation choices.


![demo](https://github.com/user-attachments/assets/1252c5fd-196c-45b3-89ec-c5bf9360612a)

## Technology Stack

**Frontend:**
- React.js
- React Router
- React Icons
- Google Maps JavaScript API

**Backend:**
- Spring Boot (Java)
- Spring Security (BCrypt password hashing)
- Maven

**Database:**
- SQLite (local development)

**APIs:**
- Google Maps API
- Google Directions API
- Google Distance Matrix API

---

## Project Structure

```
EcoRoutePlanner/
├── backend/
│   └── src/main/java/com/ecoroute/
│       ├── controller/     # REST API endpoints
│       ├── service/        # Business logic
│       ├── model/          # Entity classes
│       └── database/       # Database managers & seeder
├── src/
│   ├── components/         # Reusable React components
│   ├── pages/              # Page components
│   └── services/           # API service layer
└── data/                   # SQLite database file
```

---

## Setup Instructions

### Prerequisites

- Java Development Kit (JDK 17)
- Maven 3.x
- Node.js 16+ and npm
- Git
- Visual Studio Code (recommended)

### Installation Steps

#### 1. Install Java and Maven

**Windows (using winget):**
```bash
# Install JDK 17
winget install Oracle.JDK.17
java -version

# Install Maven (using Chocolatey)
Set-ExecutionPolicy Bypass -Scope Process -Force
iwr https://community.chocolatey.org/install.ps1 -UseBasicParsing | iex
choco install maven -y
mvn -v
```

#### 2. Clone the Repository

```bash
git clone https://github.com/marcolb123/EcoRoutePlanner.git
cd EcoRoutePlanner
```

#### 3. Backend Setup

The backend uses SQLite and automatically creates/seeds the database on first run.

```bash
cd backend
mvn spring-boot:run
```

Backend runs on: http://localhost:8080

#### 4. Frontend Setup

Install dependencies and start the development server:

```bash
# From project root
npm install
npm start
```

Frontend runs on: http://localhost:3000

---

## Features Implemented

### User Features
- **Authentication System**: Register, login, and guest access with role-based authorization
- **Route Planning**: Calculate eco-friendly routes with real-time Google Maps integration
- **Emissions Comparison**: Compare CO2 emissions across different transport modes (Walking, Cycling, Bus, Transit, Driving)
- **Journey History**: View and manage past trips with detailed emission statistics
- **Statistics Dashboard**: Track total distance, emissions saved, and eco points earned
- **Rewards System**: Earn points automatically (1 point per 0.1kg CO2 saved) and redeem for rewards
- **Purchase History**: View all reward redemptions
- **Vehicle Comparison**: Compare emissions between 30+ vehicles across 13 brands

### Admin Features
- **User Management**: Create, edit, suspend, and delete user accounts
- **Role Management**: Assign roles (USER, ADMIN, GUEST)
- **Reward Management**: Create, edit, and delete rewards with stock tracking
- **System Statistics**: View aggregate user and system metrics

### Security Features
- BCrypt password encryption
- Role-based access control (RBAC)
- Input validation on all forms
- Protected API endpoints

---

## Database Schema

The application uses 10 interconnected tables:

1. **User** - User accounts and authentication
2. **Role** - Staff role definitions
3. **Member** - Member-specific data
4. **Staff** - Staff-specific data
5. **Reward** - Available rewards catalog
6. **Journey** - Trip history records
7. **RewardMember** - Legacy reward redemption links
8. **RewardRedemption** - Purchase history
9. **Vehicle** - Vehicle database (30 vehicles)
10. **UserFavoriteVehicle** - User's favorite vehicles

---

## Test Credentials

The database is automatically seeded with test accounts:

**Regular Users:**
- Username: `john_doe` | Password: `password123` (150 points)
- Username: `jane_smith` | Password: `password123` (250 points)

**Admin Users:**
- Username: `admin_user` | Password: `admin123`
- Username: `manager_user` | Password: `manager123`

**Guest User:**
- Username: `guest` | Password: `guest` (limited access)

---

## API Endpoints

### Authentication
- `POST /api/users/register` - Create new account
- `POST /api/users/login` - User login
- `POST /api/users/guest` - Guest login

### User Management
- `GET /api/users/{id}` - Get user profile
- `PUT /api/users/{id}` - Update user profile
- `GET /api/users/{id}/purchase-history` - Get purchase history

### Journeys
- `GET /api/journeys/user/{userId}` - Get user's journeys
- `POST /api/journeys` - Create new journey (auto-awards points)
- `DELETE /api/journeys/{id}` - Delete journey

### Statistics
- `GET /api/statistics/totalJourneys?userId={id}`
- `GET /api/statistics/totalDistance?userId={id}`
- `GET /api/statistics/totalEmissions?userId={id}`
- `GET /api/statistics/totalEmissionsReduced?userId={id}`

### Rewards
- `GET /api/rewards/all` - List all rewards
- `POST /api/rewards/redeem` - Redeem reward (deducts points & stock)
- `POST /api/rewards/create` - Admin: create reward
- `PUT /api/rewards/update/{id}` - Admin: update reward
- `DELETE /api/rewards/delete/{id}` - Admin: delete reward

### Vehicles
- `GET /api/vehicles/all` - List all vehicles
- `GET /api/vehicles/brands` - Get unique brands
- `GET /api/vehicles/models?brand={name}` - Get models by brand
- `POST /api/vehicles/favorites/{userId}/{vehicleId}` - Toggle favorite

### Admin
- `GET /api/admin/users` - List all users
- `POST /api/admin/users` - Create user
- `PUT /api/admin/users/{id}` - Update user
- `DELETE /api/admin/users/{id}` - Delete user
- `PUT /api/admin/users/{id}/suspend` - Suspend/unsuspend user
- `PUT /api/admin/users/{id}/role` - Change user role
- `GET /api/admin/statistics/system` - System statistics

---

## Verification Checklist

After setup, verify the following:

- [ ] Visit http://localhost:3000 - Homepage loads
- [ ] Login with test credentials works
- [ ] Guest access works
- [ ] Route planner calculates routes
- [ ] Journey saving awards eco points
- [ ] Statistics page displays data
- [ ] Admin dashboard accessible (admin accounts only)
- [ ] Reward redemption works
- [ ] Vehicle comparison displays correctly

---

## Development Workflow

### Working with Branches

Always create a new branch for features:

```bash
git checkout -b feature-name
```

### Committing Changes

```bash
git add .
git commit -m "Description of changes"
git push origin feature-name
```

### Pull Requests

Submit a pull request on GitHub for team review before merging to main.

---

## Database Reset

To reset the database with fresh seed data:

```bash
# Delete the database file
rm data/app.db

# Restart backend (database recreates automatically)
cd backend
mvn spring-boot:run
```

---

## Troubleshooting

**Backend won't start:**
- Verify Java 17 is installed: `java -version`
- Check Maven is installed: `mvn -v`
- Ensure port 8080 is not in use

**Frontend won't start:**
- Verify Node.js is installed: `node -v`
- Delete `node_modules` and reinstall: `rm -rf node_modules && npm install`
- Clear npm cache: `npm cache clean --force`

**Database issues:**
- Delete `data/app.db` and restart backend
- Check file permissions on `data/` folder

**Google Maps not loading:**
- Verify `REACT_APP_GOOGLE_MAPS_API_KEY` in `.env` file
- Check API key has Maps, Directions, and Distance Matrix enabled
- Ensure billing is enabled on Google Cloud Console

---

## Contributing

Contributions are welcome! Please follow these guidelines:

1. Fork the repository
2. Create a feature branch
3. Write clear commit messages
4. Test your changes thoroughly
5. Submit a pull request with detailed description



## License

This project is licensed under the MIT License.

## Contact

For questions or issues, please open a GitHub issue or contact the development team.

