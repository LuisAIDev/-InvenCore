Write-Host "Liberando puerto 8080..." -ForegroundColor Yellow
$pids = netstat -ano | Select-String ":8080 " | ForEach-Object { ($_ -split '\s+')[-1] } | Sort-Object -Unique
foreach ($p in $pids) {
    try { Stop-Process -Id $p -Force -ErrorAction SilentlyContinue } catch {}
}
Start-Sleep -Seconds 2
Write-Host "Arrancando InvenCore Backend..." -ForegroundColor Green
$mvn = "C:\Program Files\JetBrains\IntelliJ IDEA 2025.3.2\plugins\maven\lib\maven3\bin\mvn.cmd"
& $mvn spring-boot:run -f "C:\Projects\InvenCore\backend\pom.xml"
