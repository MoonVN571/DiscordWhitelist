# Discord Whitelist Plugin - Command System Documentation

## Overview
The Discord Whitelist Plugin now features a comprehensive command system with separate handlers for Discord commands and in-game commands, providing better organization, extensibility, and maintainability.

## Architecture

### 🏗️ **Command System Structure**

```
📁 Command System
├── 🎮 Game Commands (In-game)
│   ├── GameCommandHandler (Interface)
│   ├── GameCommandLoader (Manager)
│   └── DiscordWhitelistCommand (Implementation)
└── 💬 Discord Commands
    ├── DiscordCommand (Interface)
    ├── DiscordCommandManager (Manager)
    └── WhitelistDiscordCommand (Implementation)
```

## Game Commands (In-Game)

### 📋 **Available Commands**

#### `/discordwhitelist` (Aliases: `/dw`)
Main command for managing the plugin in-game.

**Subcommands:**
- `reload` - Reload plugin configuration
- `status` - Check Discord bot connection status
- `info` - Display plugin information

**Usage Examples:**
```
/discordwhitelist reload
/discordwhitelist status
/discordwhitelist info
/dw reload
```

### 🔐 **Permissions**

| Permission | Description | Default |
|------------|-------------|---------|
| `discordwhitelist.use` | Basic plugin access | `true` |
| `discordwhitelist.admin` | Full admin access | `op` |
| `discordwhitelist.admin.reload` | Reload configuration | `op` |
| `discordwhitelist.admin.status` | Check bot status | `op` |

### 🔧 **Tab Completion**
Smart tab completion is provided for all commands:
- `/discordwhitelist <TAB>` → Shows: reload, status, info
- Partial matching (e.g., `re<TAB>` → `reload`)

## Discord Commands

### 💬 **Available Commands**

#### `!whitelist` (Aliases: `!wl`)
Main Discord command for managing the server whitelist.

**Subcommands:**
- `add <username>` - Add player to whitelist
- `remove <username>` - Remove player from whitelist
- `list` - Show all whitelisted players
- `help` - Display command help

**Usage Examples:**
```
!whitelist add Steve
!whitelist remove Alex
!whitelist list
!wl add Notch
!wl help
```

### 🔒 **Authorization**
Discord commands require:
- User must have one of the configured authorized roles
- Bot must have permission to read messages and send responses

### 📝 **Response Messages**
All Discord responses are localized and include:
- ✅ Success confirmations
- ❌ Error messages
- ℹ️ Information displays
- 📋 Help messages

## Implementation Details

### 🎮 **Game Command System**

#### **GameCommandHandler Interface**
```java
public interface GameCommandHandler {
    boolean onCommand(CommandSender sender, Command command, String label, String[] args);
    List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args);
    String getCommandName();
}
```

#### **GameCommandLoader Class**
- Manages all game command handlers
- Provides centralized command routing
- Handles tab completion automatically
- Supports dynamic command registration

#### **DiscordWhitelistCommand Class**
- Implements the `/discordwhitelist` command
- Handles all subcommands (reload, status, info)
- Provides context-aware tab completion
- Includes permission checking

### 💬 **Discord Command System**

#### **DiscordCommand Interface**
```java
public interface DiscordCommand {
    void execute(MessageReceivedEvent event, String[] args);
    String getName();
    String[] getAliases();
    String getDescription();
    String getUsage();
    boolean hasPermission(MessageReceivedEvent event);
}
```

#### **DiscordCommandManager Class**
- Routes Discord messages to appropriate handlers
- Manages command aliases automatically
- Handles permission checking
- Provides error handling and logging

#### **WhitelistDiscordCommand Class**
- Implements whitelist management via Discord
- Handles all whitelist operations (add, remove, list)
- Provides help system
- Includes comprehensive error handling

## Configuration

### 🛠️ **Basic Configuration**
```yaml
# config.yml
locale: "en"  # Language: en, vi
discord:
  token: "YOUR_BOT_TOKEN"
  prefix: "!"
  authorized_roles:
    - "ROLE_ID_1"
    - "ROLE_ID_2"
```

### 🌍 **Localization**
Messages are fully localized in:
- **English** (`locale_en.yml`)
- **Vietnamese** (`locale_vi.yml`)

#### **Message Categories:**
- `discord.*` - Discord bot responses
- `game.*` - In-game command responses
- `console.*` - Server log messages (always English)

## Features

### ✨ **Key Features**

1. **🔌 Modular Design**
   - Easy to add new commands
   - Separate concerns for different command types
   - Interface-driven architecture

2. **🌐 Multi-language Support**
   - Localized user messages
   - Console messages remain in English
   - Easy to add new languages

3. **🔒 Permission System**
   - Granular permissions for game commands
   - Role-based Discord authorization
   - Secure command execution

4. **📝 Smart Tab Completion**
   - Context-aware suggestions
   - Partial matching
   - Extensible completion system

5. **🛡️ Error Handling**
   - Comprehensive error messages
   - Graceful failure handling
   - Detailed logging

6. **📋 Help System**
   - Built-in help for Discord commands
   - Clear usage instructions
   - Command descriptions

## Development

### 🔧 **Adding New Game Commands**

1. Create a class implementing `GameCommandHandler`
2. Register it in `GameCommandLoader.registerCommands()`
3. Add command definition to `plugin.yml`
4. Add localized messages to locale files

### 💬 **Adding New Discord Commands**

1. Create a class implementing `DiscordCommand`
2. Register it in `DiscordCommandManager.registerCommands()`
3. Add localized messages to locale files

### 🌍 **Adding New Languages**

1. Create `locale_XX.yml` (XX = language code)
2. Copy message structure from existing locale file
3. Translate all messages
4. Update configuration documentation

## Usage Examples

### 🎮 **In-Game Usage**
```
Player: /discordwhitelist reload
Server: §a[DiscordWhitelist] Configuration reloaded successfully.

Player: /dw status
Server: §a[DiscordWhitelist] Discord Bot Status: §fConnected

Player: /discordwhitelist info
Server: §a[DiscordWhitelist] §7Plugin by §bYourName §7- Version 1.0
        §7Manages server whitelist through Discord commands.
```

### 💬 **Discord Usage**
```
User: !whitelist add Steve
Bot: ✅ | Added `Steve` to server whitelist

User: !wl list
Bot: 📜 | Whitelisted Players: Steve, Alex, Notch

User: !whitelist help
Bot: 📋 | Discord Whitelist Commands:
     !whitelist add <username> - Add player to whitelist
     !whitelist remove <username> - Remove player from whitelist
     !whitelist list - Show all whitelisted players
     !wl - Short form of whitelist command
```

## Logging

All actions are logged with detailed information:
- Whitelist additions/removals with user details
- Command executions and errors
- Bot connection status changes
- Configuration reload events

Log files: `plugins/DiscordWhitelist/whitelist_log.txt`
