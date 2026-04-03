# Postman Quick Start - 5 Minutes

## 1. Start Backend (Terminal 1)
```bash
docker-compose up
mvn spring-boot:run
```

## 2. Open Postman

## 3. Import Collection
- Click **Import**
- Select `postman_collection.json`
- Click **Import**

## 4. Create Environment
- Click **Environments** → **Create New**
- Name: `Finance Local`
- Add variable: `base_url` = `http://localhost:8080`
- Click **Save**

## 5. Select Environment
- Top-right dropdown → Select **Finance Local**

## 6. Run Tests

### Quick Test (2 minutes)
```
1. Authentication → Login - Admin User → Send
2. Financial Records → Create Income Record → Send
3. Dashboard & Analytics → Get Dashboard Analytics → Send
4. Health Check → Health Status → Send
```

### Full Test Suite (5 minutes)
```
1. Click collection name
2. Click Run button
3. Select Finance Local environment
4. Click Run Finance Data Processing Backend API
5. Watch all tests execute
```

## Expected Results

✓ All tests should pass (green checkmarks)

If any fail:
- Check backend is running
- Verify Docker services: `docker-compose ps`
- Check environment variables are set

## Key Endpoints

| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/api/auth/login` | Get JWT token |
| POST | `/api/users` | Create user |
| POST | `/api/records` | Create financial record |
| GET | `/api/records` | List records |
| GET | `/api/dashboard/analytics` | View analytics |
| GET | `/actuator/health` | Health check |

## Variables Used

- `{{base_url}}` - API base URL (http://localhost:8080)
- `{{jwt_token}}` - JWT token (auto-set by login)
- `{{user_id}}` - User ID (auto-set by create user)
- `{{record_id}}` - Record ID (auto-set by create record)

## Common Commands

| Action | Steps |
|--------|-------|
| Run single request | Click request → Click Send |
| Run folder | Right-click folder → Run Folder |
| Run collection | Click collection → Click Run |
| View test results | Check bottom panel after Send |
| Export results | After run → Click Export Results |

## Troubleshooting

| Issue | Solution |
|-------|----------|
| 401 Unauthorized | Re-run login test |
| 404 Not Found | Run create test first |
| Connection refused | Start backend with `mvn spring-boot:run` |
| Tests not running | Select environment from dropdown |

## Next: Full Testing Guide

See `POSTMAN_TESTING_GUIDE.md` for detailed documentation.
