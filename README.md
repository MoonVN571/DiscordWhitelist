# DiscordWhitelist Plugin

A Minecraft Paper plugin that integrates with Discord to manage the server whitelist using chat commands.

---

## ✨ Features

- ✅ Add/remove players from the Minecraft whitelist via Discord
- ✅ Role-based authorization (by Discord Role ID)
- ✅ Command alias support (`mwhitelist`, `whitelist`, `wl`)
- ✅ In-game `/discordwhitelistreload` to reload config
- ✅ Action logging (`whitelist_log.txt`)
- ✅ Uses native `whitelist add/remove` commands (not API calls)

---

## 📦 Installation

1. Build the plugin with Maven/Gradle
2. Put the `.jar` in your server's `/plugins` folder
3. Start your Paper server

---

## ⚙️ Configuration (`config.yml`)

```yaml
discord:
  token: "YOUR_BOT_TOKEN"
  prefix: "m"
  authorized_user_ids:
    - "123456789012345678"  # User ID(s) allowed to use commands
    - "987654321098765432"
```

---

## 🧠 Discord Command Usage

```
mwhitelist add <username>    # ✅ Whitelist player
mwhitelist remove <username> # ❌ Remove from whitelist
mwhitelist list              # 📜 List all whitelisted players
```

---

## 🔐 In-Game Commands

```bash
/dw reload
```

Reloads the plugin configuration.

---

## 📄 Logging

Whitelist actions are logged to:
```
plugins/DiscordWhitelist/whitelist_log.txt
```

Format:
```
[2025-06-23T14:55] ADD Notch by Moonu (497768011118280716)
```

---

## 📜 License

MIT — use freely in your servers and modify as needed.
