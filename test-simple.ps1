# Simple API test with error details

# Login
$loginBody = @{username="admin"; password="Admin@123"} | ConvertTo-Json
$loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -Body $loginBody -ContentType "application/json"
$token = $loginResponse.token
Write-Host "Login successful! Token: $($token.Substring(0,20))..."

# Try to create a record and capture the error
$headers = @{
    Authorization = "Bearer $token"
    "Content-Type" = "application/json"
}

$recordBody = @{
    description = "Test Income"
    amount = 1000.00
    type = "INCOME"
    category = "Salary"
    recordDate = "2026-04-01"
} | ConvertTo-Json

Write-Host "`nTrying to create record..."
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/financial-records" -Method POST -Body $recordBody -Headers $headers
    Write-Host "Success! Record created: $($response | ConvertTo-Json)"
} catch {
    Write-Host "Error occurred:"
    Write-Host "Status Code: $($_.Exception.Response.StatusCode.value__)"
    Write-Host "Status Description: $($_.Exception.Response.StatusDescription)"
    
    # Try to read the error response body
    $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
    $reader.BaseStream.Position = 0
    $reader.DiscardBufferedData()
    $responseBody = $reader.ReadToEnd()
    Write-Host "Response Body: $responseBody"
}
