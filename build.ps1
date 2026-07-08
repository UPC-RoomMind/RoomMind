$ErrorActionPreference = "Stop"

Clear-Host
Write-Host "================================================" -ForegroundColor Cyan
Write-Host "          RoomMind 自动打包脚本" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""

$projectRoot = $PSScriptRoot
$outputDir = Join-Path $projectRoot "build_output"
$backendDir = Join-Path $projectRoot "backend"
$frontendDir = Join-Path $projectRoot "frontend"

Write-Host "[1/4] 创建输出目录..." -ForegroundColor Yellow
if (Test-Path $outputDir) {
    Remove-Item -Recurse -Force $outputDir
}
New-Item -ItemType Directory -Path $outputDir | Out-Null
Write-Host "      ✓ 输出目录已创建: $outputDir" -ForegroundColor Green
Write-Host ""

Write-Host "[2/4] 开始打包前端..." -ForegroundColor Yellow
Set-Location $frontendDir
Write-Host "      正在安装依赖..." -ForegroundColor Gray
npm install
if ($LASTEXITCODE -ne 0) {
    Write-Host "      ✗ 前端依赖安装失败!" -ForegroundColor Red
    Read-Host "按 Enter 退出"
    exit 1
}
Write-Host "      ✓ 依赖安装完成" -ForegroundColor Green

Write-Host "      正在构建生产版本..." -ForegroundColor Gray
npm run build
if ($LASTEXITCODE -ne 0) {
    Write-Host "      ✗ 前端构建失败!" -ForegroundColor Red
    Read-Host "按 Enter 退出"
    exit 1
}
Write-Host "      ✓ 前端构建完成" -ForegroundColor Green

Write-Host "      复制 dist 到输出目录..." -ForegroundColor Gray
Copy-Item -Path (Join-Path $frontendDir "dist") -Destination $outputDir -Recurse -Force
Write-Host "      ✓ 前端 dist 已复制" -ForegroundColor Green
Write-Host ""

Write-Host "[3/4] 开始打包后端..." -ForegroundColor Yellow
Set-Location $backendDir
Write-Host "      正在清理并打包..." -ForegroundColor Gray
.\mvnw.cmd clean package -DskipTests
if ($LASTEXITCODE -ne 0) {
    Write-Host "      ✗ 后端打包失败!" -ForegroundColor Red
    Read-Host "按 Enter 退出"
    exit 1
}
Write-Host "      ✓ 后端打包完成" -ForegroundColor Green

Write-Host "      复制 jar 包到输出目录..." -ForegroundColor Gray
$jarFile = Get-ChildItem -Path $backendDir -Recurse -Filter "*.jar" | Where-Object { $_.Name -notlike "*-sources.jar" } | Select-Object -First 1
if ($jarFile) {
    Copy-Item -Path $jarFile.FullName -Destination $outputDir -Force
    Write-Host "      ✓ 已复制: $($jarFile.Name)" -ForegroundColor Green
} else {
    Write-Host "      ✗ 未找到 jar 包!" -ForegroundColor Red
    Read-Host "按 Enter 退出"
    exit 1
}
Write-Host ""

Write-Host "[4/4] 打包完成!" -ForegroundColor Yellow
Write-Host "================================================" -ForegroundColor Cyan
Write-Host "输出目录: $outputDir" -ForegroundColor White
Write-Host ""
Write-Host "包含文件:" -ForegroundColor White
Write-Host "  - $($jarFile.Name)  (后端服务)" -ForegroundColor White
Write-Host "  - dist/                        (前端静态资源)" -ForegroundColor White
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "部署方式:" -ForegroundColor White
Write-Host "  1. 将 build_output 整个目录上传到服务器" -ForegroundColor White
Write-Host "  2. 运行: java -jar $($jarFile.Name)" -ForegroundColor White
Write-Host "  3. 访问 http://服务器IP:8080" -ForegroundColor White
Write-Host "================================================" -ForegroundColor Cyan

Set-Location $projectRoot
Read-Host "按 Enter 退出"