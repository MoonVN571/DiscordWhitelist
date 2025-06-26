# Discord Whitelist Plugin - Refactored Structure

## Overview
The Discord Whitelist Plugin has been refactored into multiple classes to improve code organization, readability, and maintainability. The plugin now follows a clean separation of concerns pattern.

## Class Structure

### 1. **Main.java** (Entry Point)
- **Purpose**: Main plugin class that initializes and coordinates all other components
- **Responsibilities**:
  - Plugin lifecycle management (onEnable/onDisable)
  - Configuration loading
  - Manager initialization
  - In-game command handling (`/discordwhitelist reload`)

### 2. **MessageManager.java** (Message Handling)
- **Purpose**: Handles all message loading, formatting, and localization
- **Responsibilities**:
  - Load locale files based on configuration
  - Format messages with placeholder replacement
  - Provide hardcoded English console messages
  - Support multiple languages (English, Vietnamese)

### 3. **WhitelistManager.java** (Whitelist Operations)
- **Purpose**: Manages all whitelist-related operations and logging
- **Responsibilities**:
  - Add/remove players from whitelist
  - Get list of whitelisted players
  - Log whitelist actions to file
  - Handle whitelist file operations

### 4. **DiscordBotManager.java** (Discord Bot Connection)
- **Purpose**: Manages Discord bot connection and initialization
- **Responsibilities**:
  - Initialize JDA (Discord bot)
  - Handle bot connection lifecycle
  - Manage bot shutdown
  - Configure bot intents and cache settings

### 5. **DiscordCommandHandler.java** (Discord Command Processing)
- **Purpose**: Handles all Discord bot events and command processing
- **Responsibilities**:
  - Process Discord messages
  - Handle whitelist commands (!whitelist, !wl)
  - Check user permissions
  - Send Discord responses
  - Parse and validate command arguments

## Key Features

### 🌍 **Localization Support**
- **English** (`locale_en.yml`): Default language
- **Vietnamese** (`locale_vi.yml`): Vietnamese translation
- **Console messages**: Always in English for server administration consistency
- **User messages**: Localized based on config setting

### 🔧 **Configuration**
```yaml
# Set locale for Discord and in-game messages
locale: "en"  # Available: en, vi

discord:
  token: "YOUR_BOT_TOKEN"
  prefix: "!"
  authorized_roles:
    - "ROLE_ID_1"
    - "ROLE_ID_2"
```

### 📝 **Discord Commands**
- `!whitelist add <username>` - Add player to whitelist
- `!whitelist remove <username>` - Remove player from whitelist
- `!whitelist list` - Show all whitelisted players
- `!wl add <username>` - Short form of add command
- `!wl remove <username>` - Short form of remove command
- `!wl list` - Short form of list command

### 🎮 **In-Game Commands**
- `/discordwhitelist reload` - Reload plugin configuration

## Benefits of Refactoring

### ✅ **Improved Code Organization**
- Each class has a single, clear responsibility
- Easier to understand and maintain
- Better separation of concerns

### ✅ **Enhanced Readability**
- Code is more modular and focused
- Easier to locate specific functionality
- Better documentation and comments

### ✅ **Better Maintainability**
- Changes to one feature don't affect others
- Easier to add new features
- Simplified debugging and testing

### ✅ **Scalability**
- Easy to add new languages
- Simple to extend with new Discord commands
- Modular design supports feature additions

## File Structure
```
src/main/java/dev/moonu/Whitelist/
├── Main.java                    # Plugin entry point
├── MessageManager.java          # Message handling and localization
├── WhitelistManager.java        # Whitelist operations
├── DiscordBotManager.java       # Discord bot connection
└── DiscordCommandHandler.java   # Discord command processing

src/main/resources/
├── config.yml                  # Main configuration
├── locale_en.yml               # English messages
├── locale_vi.yml               # Vietnamese messages
└── plugin.yml                  # Plugin metadata
```

## Usage Instructions

1. **Install the plugin** by placing the JAR file in your server's `plugins` folder
2. **Configure the bot** by editing `config.yml` with your Discord bot token and settings
3. **Set your preferred language** by changing the `locale` setting in `config.yml`
4. **Customize messages** by editing the appropriate locale file
5. **Reload configuration** using `/discordwhitelist reload` in-game

## Placeholder Support
Messages support placeholders that are automatically replaced:
- `{player}` - Player username
- `{botname}` - Discord bot name
- `{prefix}` - Command prefix
- `{error}` - Error messages
- `{list}` - List of players

## Logging
All whitelist actions are logged to `plugins/DiscordWhitelist/whitelist_log.txt` with timestamps and user information.
