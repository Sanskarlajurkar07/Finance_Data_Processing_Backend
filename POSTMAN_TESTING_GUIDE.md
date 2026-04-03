# Postman Testing Guide - Finance Data Processing Backend

## Overview

This guide explains how to use Postman to test the Finance Data Processing Backend API.

## Prerequisites

1. **Postman Desktop App** - Download from [postman.com](https://www.postman.com/downloads/)
2. **Running Backend** - Start the application with `mvn spring-boot:run`
3. **Docker Services** - Redis and PostgreSQL running via `docker-compose up`

## Setup Steps

### Step 1: Import Collection

1. Open Postman
2. Click **Import** button (top-left)
3. Select **Upload Files**
4. Choose `postman_collection.json` from the project root
5. Click **Import**

### Step 2: Create Environment

1. Click **Environments** (left sidebar)
2. Click **Create New**
3. Name it: `Finance Local`
4. Add variables:
   - `base_url`: `http://localhost:8080`
   - `jwt_token`: (leave empty, will be set by login test)
   - `user_id`: (leave empty, will be set by create user test)
   - `record_id`: (leave empty, will be set by create record test)
5. Click **Save**

### Step 3: Select Environment

1. Top-right corner, select **Finance Local** from environment dropdown
2. Verify `base_url` shows `http://localhost:8080`

## Running Tests

### Option 1: Run Individual Request

1. Navigate to **Authentication** → **Login - Admin User**
2. Click **Send**
3. View response in bottom panel
4. Tests run automatically (green checkmarks = passed)

### Option 2: Run Entire Collection

1. Click on collection name: **Finance Data Processing Backend API**
2. Click **Run** button (right side)
3. Select **Finance Local** environment
4. Click **Run Finance Data Processing Backend API**
5. Watch tests execute in sequence
6. View summary at end

### Option 3: Run Specific Folder

1. Right-click on folder (e.g., **Financial Records**)
2. Select **Run Folder**
3. Select environment and run

## Test Execution Order

**Recommended sequence:**

1. **Health Check** - Verify backend is running
2. **Authentication** - Login to get JWT token
3. **User Management** - Create and manage users
4. **Financial Records** - Create income/expense records
5. **Dashboard & Analytics** - View aggregated data
6. **Audit Logs** - Verify all actions logged

## Test Cases Explained

### Authentication Tests

**Login - Admin User**
- Endpoint: `POST /api/auth/login`
- Credentials: `admin` / `Admin@123`
- Expected: 200 OK, JWT token returned
- Saves token to `{{jwt_token}}` variable

**Login - Invalid Credentials**
- Endpoint: `POST /api/auth/login`
- Invalid password
- Expected: 401 Unauthorized

### User Management Tests

**Create User**
- Endpoint: `POST /api/users`
- Creates new user with ANALYST role
- Expected: 201 Created
- Saves user ID to `{{user_id}}`

**Get User by Username**
- Endpoint: `GET /api/users/{username}`
- Retrieves user details
- Expected: 200 OK

**Update User Role**
- Endpoint: `PUT /api/users/{id}/role`
- Changes role to VIEWER
- Expected: 200 OK

### Financial Records Tests

**Create Income Record**
- Endpoint: `POST /api/records`
- Creates $5000 income record
- Expected: 201 Created
- Saves record ID to `{{record_id}}`

**Create Expense Record**
- Endpoint: `POST /api/records`
- Creates $150 expense record
- Expected: 201 Created

**Get Record by ID**
- Endpoint: `GET /api/records/{id}`
- Retrieves specific record
- Expected: 200 OK

**Get All Records (Paginated)**
- Endpoint: `GET /api/records?page=0&size=20`
- Lists records with pagination
- Expected: 200 OK, array of records

**Update Record**
- Endpoint: `PUT /api/records/{id}`
- Updates amount to $5500
- Expected: 200 OK

**Delete Record**
- Endpoint: `DELETE /api/records/{id}`
- Soft deletes record (marks inactive)
- Expected: 204 No Content

### Dashboard Tests

**Get Dashboard Analytics**
- Endpoint: `GET /api/dashboard/analytics`
- Returns aggregated financial data
- Expected: 200 OK
- Validates: totalIncome, totalExpenses, netBalance, categoryBreakdown

### Audit Logs Tests

**Get Audit Logs**
- Endpoint: `GET /api/audit-logs?page=0&size=20`
- Lists all audit log entries
- Expected: 200 OK
- Shows all CREATE, UPDATE, DELETE actions

### Health Check Tests

**Health Status**
- Endpoint: `GET /actuator/health`
- Checks database and Redis connectivity
- Expected: 200 OK, status = UP
- Validates both components are healthy

## Understanding Test Results

### Green Checkmark ✓
- Test passed
- Assertion succeeded
- Response matches expected values

### Red X ✗
- Test failed
- Assertion failed
- Check response body and error message

### Common Issues

**401 Unauthorized**
- JWT token expired or invalid
- Solution: Re-run login test to get new token

**404 Not Found**
- Resource doesn't exist
- Solution: Ensure create test ran before get/update/delete

**500 Internal Server Error**
- Backend error
- Solution: Check backend logs, verify database/Redis running

**Connection Refused**
- Backend not running
- Solution: Start with `mvn spring-boot:run`

## Advanced Features

### Pre-request Scripts

Add logic before request:
```javascript
// Example: Add timestamp
pm.environment.set("timestamp", new Date().toISOString());
```

### Post-request Scripts

Add logic after response:
```javascript
// Example: Extract data
var jsonData = pm.response.json();
pm.environment.set("extracted_value", jsonData.field);
```

### Variables

**Global Variables** - Available across all requests
**Environment Variables** - Specific to selected environment
**Collection Variables** - Specific to collection
**Local Variables** - Temporary, request-only

### Assertions

Common test assertions:
```javascript
// Status code
pm.response.to.have.status(200);

// Response time
pm.expect(pm.response.responseTime).to.be.below(1000);

// JSON field
pm.expect(jsonData.username).to.equal("admin");

// Array length
pm.expect(jsonData.content).to.have.lengthOf(5);

// Field exists
pm.expect(jsonData.token).to.exist;
```

## Exporting Results

1. After running collection, click **Export Results**
2. Choose format (JSON, HTML)
3. Save to file
4. Share with team

## Rate Limiting

The API enforces rate limiting: **5 failed attempts per 15 minutes**

Test rate limiting:
1. Run **Login - Invalid Credentials** 5 times
2. 6th attempt returns 429 Too Many Requests
3. Wait 15 minutes or reset via admin

## Idempotency

Financial records support idempotency keys to prevent duplicates:

```json
{
  "description": "Salary",
  "amount": 5000,
  "type": "INCOME",
  "idempotencyKey": "unique-key-123"
}
```

Same key = same response (no duplicate created)

## Troubleshooting

### Tests Not Running
- Verify environment selected
- Check base_url is correct
- Ensure backend is running

### JWT Token Expired
- Re-run login test
- Token valid for 15 minutes (900 seconds)

### Database Errors
- Verify PostgreSQL running: `docker-compose ps`
- Check migrations: `docker-compose logs db`

### Redis Errors
- Verify Redis running: `docker-compose ps`
- Check Redis connection: `redis-cli ping`

## Next Steps

1. ✓ Import collection
2. ✓ Create environment
3. ✓ Run health check
4. ✓ Run login test
5. ✓ Run all tests
6. ✓ Review results
7. ✓ Export report

## Support

For issues:
1. Check backend logs: `mvn spring-boot:run` output
2. Verify Docker services: `docker-compose ps`
3. Review Postman console: Ctrl+Alt+C (Windows) or Cmd+Option+C (Mac)
4. Check API documentation in code comments
