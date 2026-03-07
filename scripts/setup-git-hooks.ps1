# Install git hooks. Run from project root.
$ErrorActionPreference = "Stop"
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$ProjectRoot = Split-Path -Parent $ScriptDir
$HooksSrc = Join-Path $ScriptDir "hooks"
$HooksDst = Join-Path $ProjectRoot ".git\hooks"

New-Item -ItemType Directory -Force -Path $HooksDst | Out-Null
Copy-Item (Join-Path $HooksSrc "pre-commit") (Join-Path $HooksDst "pre-commit") -Force
Write-Host "Git hooks installed. Pre-commit will run mvn verify before each commit."
Write-Host "Note: On Windows, Git uses Git Bash for hooks. Ensure 'mvn' is in PATH when running git commit."
