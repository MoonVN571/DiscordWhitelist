# Release script for DiscordWhitelist plugin
# Usage: .\release.ps1 [version]

param(
    [Parameter(Mandatory=$true)]
    [string]$Version
)

# Colors for output
$ErrorColor = "Red"
$SuccessColor = "Green"
$WarningColor = "Yellow"

function Write-Info {
    param([string]$Message)
    Write-Host "[INFO] $Message" -ForegroundColor $SuccessColor
}

function Write-Warning {
    param([string]$Message)
    Write-Host "[WARN] $Message" -ForegroundColor $WarningColor
}

function Write-Error {
    param([string]$Message)
    Write-Host "[ERROR] $Message" -ForegroundColor $ErrorColor
}

# Validate version format
if ($Version -notmatch '^\d+\.\d+\.\d+$') {
    Write-Error "Version should be in format X.Y.Z (e.g., 1.0.2)"
    exit 1
}

$Tag = "v$Version"
Write-Info "Preparing release $Tag"

# Check if we're in a git repository
try {
    git rev-parse --git-dir | Out-Null
} catch {
    Write-Error "Not in a git repository"
    exit 1
}

# Check for uncommitted changes
$changes = git status --porcelain
if ($changes) {
    Write-Error "You have uncommitted changes. Please commit or stash them first."
    exit 1
}

# Update version in pom.xml
Write-Info "Updating version in pom.xml to $Version"
$pomContent = Get-Content "pom.xml" -Raw
$pomContent = $pomContent -replace '<version>[\d\.]+</version>', "<version>$Version</version>"
Set-Content "pom.xml" -Value $pomContent

# Build the project
Write-Info "Building project..."
try {
    mvn clean package -q
    if ($LASTEXITCODE -ne 0) {
        throw "Maven build failed"
    }
} catch {
    Write-Error "Build failed: $_"
    exit 1
}

# Commit version change
Write-Info "Committing version change"
git add pom.xml
git commit -m "Bump version to $Version"

# Create and push tag
Write-Info "Creating and pushing tag $Tag"
git tag -a $Tag -m "Release $Tag"
git push origin main
git push origin $Tag

Write-Info "Release $Tag has been created and pushed!"
Write-Info "GitHub Actions will automatically build and create the release."
Write-Info "Check the Actions tab in your GitHub repository for progress."
