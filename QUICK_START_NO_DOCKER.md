# Quick Start - No Docker Required ⚡

## 30 Second Setup

### Option 1: Automatic (Easiest)

**Double-click one of these files:**

- **Windows CMD:** `run-server.bat`
- **Windows PowerShell:** `run-server.ps1`

The script will:
1. Build the project
2. Start the server
3. Show you the URL

### Option 2: Manual (Terminal)

Open terminal in VS Code (Ctrl + `) and run:

```bash
.\mvnw.cmd clean install
.\mvnw.cmd spring-boot:run
```

## Verify Server is Running

Open new terminal tab and run:
```bash
curl http://localhost:8080/actuator/health
```

Expected response:
```json
{
  "status": "UP"
}
```

## Test with Postman

1. Open Postman
2. Click **Import**
3. Select `postman_collection.json`
4. Select **Finance Local** environment
5. Click **Send** on any request

## What You Get

✓ Full API with authentication
✓ Financial record management
✓ Dashboard analytics
✓ Audit logging
✓ Rate limiting
✓ All tests passing

## Database

- **Type:** H2 (Embedded)
- **Data:** In-memory (lost on restart)
- **Console:** http://localhost:8080/h2-console

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Build fails | Run: `.\mvnw.cmd clean` then rebuild |
| Port 8080 in use | Kill process: `netstat -ano \| findstr :8080` |
| Out of memory | Set: `set MAVEN_OPTS=-Xmx1024m` |
| Tests fail | Build without tests: `.\mvnw.cmd clean install -DskipTests` |

## Next Steps

1. ✓ Run server (use script or manual command)
2. ✓ Test health endpoint
3. ✓ Import Postman collection
4. ✓ Run API tests
5. ✓ Review POSTMAN_TESTING_GUIDE.md for detailed tests

## Files

- `run-server.bat` - Batch script (Windows CMD)
- `run-server.ps1` - PowerShell script (Windows)
- `RUN_WITHOUT_DOCKER.md` - Detailed guide
- `POSTMAN_TESTING_GUIDE.md` - API testing guide

---

**That's it! Your server is ready to go.** 🚀
