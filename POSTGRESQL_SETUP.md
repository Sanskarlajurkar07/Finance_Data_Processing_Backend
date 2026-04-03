# PostgreSQL Setup Guide

## Prerequisites
- PostgreSQL installed locally on your machine
- PostgreSQL service running

## Step 1: Create Database

Open PostgreSQL command line (psql) or pgAdmin and run:

```sql
CREATE DATABASE financedb;
```

## Step 2: Configure Database Credentials

The application uses these default credentials:
- **Database**: `financedb`
- **Username**: `postgres`
- **Password**: `postgres`
- **Host**: `localhost`
- **Port**: `5432`

If your PostgreSQL uses different credentials, create a `.env` file in the project root:

```env
DATABASE_URL=jdbc:postgresql://localhost:5432/financedb
DATABASE_USERNAME=your_username
DATABASE_PASSWORD=your_password
```

## Step 3: Verify PostgreSQL is Running

Check if PostgreSQL service is running:

**Windows:**
```powershell
Get-Service -Name postgresql*
```

Or check in Services (services.msc) for "postgresql" service.

## Step 4: Test Connection

You can test the connection using psql:

```bash
psql -U postgres -d financedb
```

## Step 5: Start the Server

Stop the current server (if running) and restart:

```powershell
.\run-server.ps1
```

The application will:
1. Connect to PostgreSQL
2. Run Flyway migrations to create tables
3. Insert default admin user
4. Start on port 8080

## Default Admin User

After migrations run, you'll have:
- **Username**: `admin`
- **Password**: `admin123`
- **Role**: `ADMIN`

## Troubleshooting

### Connection Refused
- Verify PostgreSQL service is running
- Check port 5432 is not blocked by firewall
- Verify credentials in `.env` file

### Authentication Failed
- Update `DATABASE_PASSWORD` in `.env` file
- Ensure PostgreSQL user has access to `financedb` database

### Database Does Not Exist
- Create the database manually: `CREATE DATABASE financedb;`
- Or enable auto-creation in PostgreSQL config

## Migration Files

The application will automatically run these migrations:
1. `V1__create_users_table.sql` - Creates users table
2. `V2__create_financial_records_table.sql` - Creates financial records table
3. `V3__create_audit_logs_table.sql` - Creates audit logs table
4. `V4__insert_default_admin_user.sql` - Inserts default admin user

All tables will be created automatically when you start the server.
