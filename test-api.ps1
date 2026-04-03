# Test Finance Data Processing Backend API

Write-Host "=== Testing Finance Data Processing Backend API ===" -ForegroundColor Cyan

# Step 1: Login
Write-Host "`n1. Testing Login..." -ForegroundColor Yellow
$loginBody = @{
    username = "admin"
    password = "Admin@123"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -Body $loginBody -ContentType "application/json"
    $token = $loginResponse.token
    Write-Host "Login successful! Token received." -ForegroundColor Green
    Write-Host "Token expires in: $($loginResponse.expiresIn) seconds" -ForegroundColor Gray
} catch {
    Write-Host "Login failed: $_" -ForegroundColor Red
    exit 1
}

# Step 2: Create Income Record
Write-Host "`n2. Creating Income Record..." -ForegroundColor Yellow
$headers = @{
    Authorization = "Bearer $token"
    "Content-Type" = "application/json"
}

$incomeBody = @{
    description = "Monthly Salary"
    amount = 5000.00
    type = "INCOME"
    category = "Salary"
    recordDate = "2026-04-01"
    idempotencyKey = "income-test-001"
} | ConvertTo-Json

try {
    $incomeResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/financial-records" -Method POST -Body $incomeBody -Headers $headers
    $recordId = $incomeResponse.id
    Write-Host "Income record created! ID: $recordId" -ForegroundColor Green
    Write-Host "Description: $($incomeResponse.description)" -ForegroundColor Gray
    Write-Host "Amount: $($incomeResponse.amount)" -ForegroundColor Gray
} catch {
    Write-Host "Failed to create income record: $_" -ForegroundColor Red
    Write-Host "Error details: $($_.Exception.Response)" -ForegroundColor Red
}

# Step 3: Create Expense Record
Write-Host "`n3. Creating Expense Record..." -ForegroundColor Yellow
$expenseBody = @{
    description = "Grocery Shopping"
    amount = 150.00
    type = "EXPENSE"
    category = "Food"
    recordDate = "2026-04-02"
    idempotencyKey = "expense-test-001"
} | ConvertTo-Json

try {
    $expenseResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/financial-records" -Method POST -Body $expenseBody -Headers $headers
    Write-Host "Expense record created! ID: $($expenseResponse.id)" -ForegroundColor Green
    Write-Host "Description: $($expenseResponse.description)" -ForegroundColor Gray
    Write-Host "Amount: $($expenseResponse.amount)" -ForegroundColor Gray
} catch {
    Write-Host "Failed to create expense record: $_" -ForegroundColor Red
}

# Step 4: Get All Records
Write-Host "`n4. Fetching All Records..." -ForegroundColor Yellow
try {
    $uri = "http://localhost:8080/api/financial-records?page=0&size=20"
    $recordsResponse = Invoke-RestMethod -Uri $uri -Method GET -Headers $headers
    Write-Host "Records retrieved! Total: $($recordsResponse.totalElements)" -ForegroundColor Green
    Write-Host "Records on this page: $($recordsResponse.content.Count)" -ForegroundColor Gray
} catch {
    Write-Host "Failed to fetch records: $_" -ForegroundColor Red
}

# Step 5: Get Dashboard Analytics
Write-Host "`n5. Fetching Dashboard Analytics..." -ForegroundColor Yellow
try {
    $analyticsResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/dashboard/analytics" -Method GET -Headers $headers
    Write-Host "Analytics retrieved!" -ForegroundColor Green
    Write-Host "Total Income: $($analyticsResponse.totalIncome)" -ForegroundColor Gray
    Write-Host "Total Expenses: $($analyticsResponse.totalExpenses)" -ForegroundColor Gray
    Write-Host "Net Balance: $($analyticsResponse.netBalance)" -ForegroundColor Gray
} catch {
    Write-Host "Failed to fetch analytics: $_" -ForegroundColor Red
}

Write-Host "`n=== API Testing Complete ===" -ForegroundColor Cyan
