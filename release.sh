#!/bin/bash

# Release script for DiscordWhitelist plugin
# Usage: ./release.sh [version]

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Functions
info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if version is provided
if [ -z "$1" ]; then
    error "Please provide a version number"
    echo "Usage: $0 <version>"
    echo "Example: $0 1.0.2"
    exit 1
fi

VERSION=$1
TAG="v$VERSION"

# Validate version format (basic check)
if ! [[ $VERSION =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
    error "Version should be in format X.Y.Z (e.g., 1.0.2)"
    exit 1
fi

info "Preparing release $TAG"

# Check if we're in a git repository
if ! git rev-parse --git-dir > /dev/null 2>&1; then
    error "Not in a git repository"
    exit 1
fi

# Check for uncommitted changes
if ! git diff --quiet; then
    error "You have uncommitted changes. Please commit or stash them first."
    exit 1
fi

# Update version in pom.xml
info "Updating version in pom.xml to $VERSION"
sed -i.bak "s/<version>.*<\/version>/<version>$VERSION<\/version>/" pom.xml
rm pom.xml.bak 2>/dev/null || true

# Build the project
info "Building project..."
if ! mvn clean package -q; then
    error "Build failed"
    exit 1
fi

# Commit version change
info "Committing version change"
git add pom.xml
git commit -m "Bump version to $VERSION"

# Create and push tag
info "Creating and pushing tag $TAG"
git tag -a "$TAG" -m "Release $TAG"
git push origin main
git push origin "$TAG"

info "Release $TAG has been created and pushed!"
info "GitHub Actions will automatically build and create the release."
info "Check the Actions tab in your GitHub repository for progress."
