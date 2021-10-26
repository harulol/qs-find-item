<div align="right">

![GitHub Workflow Status](https://img.shields.io/github/workflow/status/harulol/qs-find-item/Java%20CI%20with%20Maven) ![GitHub](https://img.shields.io/github/license/harulol/qs-find-item)
</div>

<div align="center">

# Find Item
##### Quick search for items to buy and sell in QuickShop
</div>

### NOTICE
This is a fork of pocketkid2's work, which was in a private repository. But most of the files here are rewritten almost from scratch, so eh.

### Commands
`/advancedfinditem` or `/afi`: The base command for the plugin, doesn't do much for now.
- `/afi reload`: Reloads the configuration files.

`/finditem` or `/find`: Attempt to find and paginate shops that might have what you want to trade with. Additional filters may be applied.

### Permissions

| Permission | Description                          | Default |
|------------|--------------------------------------|:-------:|
| afi.main   | Allows access to the main command.   |   OP    |
| afi.find   | Allows access to the find command.   | Default |
| afi.reload | Allows access to the reload command. |   OP    |

### Installation
Just install like any other plugins lol, in the `plugins` folder.
Or it can be installed via `hpm` command in [HikariLibrary](https://github.com/harulol/hikari-library).

### Compatibility
As QuickShop has dropped support for v1.14 and legacy versions, this plugin was developed against the 1.15 api version. It should be able to scale back to 1.8, but eh, too much trouble testing.

### Dependencies
- [HikariLibrary](https://github.com/harulol/hikari-library/releases)
- [QuickShop](https://www.spigotmc.org/resources/62575)
