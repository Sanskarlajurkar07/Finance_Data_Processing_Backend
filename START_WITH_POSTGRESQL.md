# Start Server with PostgreSQL

## Step 1: Update Password

Open the `.env` file and update `DATABASE_PASSWORD` with your PostgreSQL password:

```env
DATABASE_PASSWORD=your_actual_password_here
```

## Step 2: Create Database

Open PostgreSQL (pgAdmin or psql) and run:

```sql
CREATE DATABASE financedb;
```

**Using psql command line:**
```bash
psql -U postgres
CREATE DATABASE financedb;
\q
```

## Step 3: Start Server

Run:
```powershell
.\start-server-fixed.ps1
```

Or manually:
```powershell
.\mvnw-fixed.cmd spring-boot:run
```

## Troubleshooting

### Password Authentication Failed
- Open `.env` file
- Update `DATABASE_PASSWORD` with correct password
- Save and restart server

### Database Does Not Exist
- Open PostgreSQL
- Run: `CREATE DATABASE financedb;`
- Restart server

### PostgreSQL Not Running
- Check if PostgreSQL service is running
- Windows: Open Services (services.msc) and start "postgresql" service

## What Happens on Startup

1. Server connects to PostgreSQL
2. Flyway runs migrations to create tables:
   - users
   - financial_records
   - audit_logs
3. Default admin user is created (username: `admin`, password: `admin123`)
4. Server starts on http://localhost:8080

## Test Server

Once started, test with:
```powershell
curl http://localhost:8080/actuator/health
```

Should return: `{"status":"UP"}`
