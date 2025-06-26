# GitHub Actions Workflows

This project includes several GitHub Actions workflows for automated building, testing, and publishing.

## Workflows

### 1. CI (`ci.yml`)
- **Triggers**: Push to main/master/develop branches, Pull requests to main/master
- **Purpose**: Continuous integration testing
- **Actions**: Validates, compiles, tests, and packages the plugin

### 2. Build and Release (`build-and-release.yml`)
- **Triggers**: Git tags starting with 'v', Manual dispatch
- **Purpose**: Creates GitHub releases with built artifacts
- **Actions**: Builds the plugin and creates a GitHub release with the JAR file

### 3. Publish to GitHub Packages (`publish.yml`)
- **Triggers**: Git tags starting with 'v', Manual dispatch
- **Purpose**: Publishes the plugin to GitHub Packages Maven repository
- **Actions**: Builds and deploys the plugin to GitHub Packages

## How to Use

### Creating a Release

1. **Tag your commit**: Create a git tag with version format `v1.0.1`
   ```bash
   git tag v1.0.1
   git push origin v1.0.1
   ```

2. **Automatic Release**: The `build-and-release.yml` workflow will automatically:
   - Build the plugin
   - Create a GitHub release
   - Upload the JAR file as a release asset

### Manual Workflow Dispatch

You can manually trigger the `build-and-release.yml` and `publish.yml` workflows:

1. Go to the "Actions" tab in your GitHub repository
2. Select the workflow you want to run
3. Click "Run workflow"
4. Choose the branch and click "Run workflow"

### GitHub Packages

The plugin will be published to GitHub Packages Maven repository when a tag is created. Users can depend on your plugin by adding this to their `pom.xml`:

```xml
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/YOUR_USERNAME/YOUR_REPOSITORY</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>dev.moonu</groupId>
        <artifactId>DiscordWhitelist</artifactId>
        <version>1.0.1</version>
    </dependency>
</dependencies>
```

## Requirements

- Repository must have Actions enabled
- For private repositories, ensure proper permissions are set
- Tags should follow semantic versioning (e.g., v1.0.0, v1.0.1, v2.0.0)

## Troubleshooting

### Build Failures
- Check the Actions tab for detailed error logs
- Ensure all dependencies are properly configured in `pom.xml`
- Verify Java version compatibility

### Permission Issues
- Ensure the repository has appropriate permissions
- Check if GITHUB_TOKEN has sufficient permissions for releases and packages

### Missing Artifacts
- Verify the build process completes successfully
- Check that the JAR file is generated in the `target/` directory
- Ensure the file paths in workflows match the actual build output
