# Quick Links Plugin for IntelliJ Platform

A JetBrains IDE plugin that lets each project register multiple links and automatically creates "Open \<name\>" commands. Links may contain placeholders, and each project can set placeholder values.

## Features

- **Global Links**: Configure links that are available across all projects
- **Project-Specific Links**: Configure links specific to each project
- **Placeholder Support**: URLs can contain placeholders like `{USER}`, `{REPO}`
- **Project Placeholder Values**: Each project can define its own placeholder values
- **Dynamic Actions**: Automatically generates "Open \<name\>" commands in the Tools menu

## Example

Configure a link:
- Name: `GitHub Repository`
- URL: `https://github.com/{USER}/{REPO}`

Then set placeholder values per project:
- `USER` = `myusername`
- `REPO` = `myproject`

The plugin will create an "Open GitHub Repository" command that opens `https://github.com/myusername/myproject`.

## Settings

- **Settings > Tools > Quick Links (Global)**: Configure global links shared across all projects
- **Settings > Tools > Quick Links**: Configure project-specific links and placeholder values

<!-- Plugin description -->
**Quick Links** is a JetBrains IDE plugin that lets you register custom links with placeholders and quickly open them from the Tools menu.

- Configure global links shared across all projects
- Configure project-specific links
- Use placeholders like `{USER}`, `{REPO}` in URLs
- Set placeholder values per project
- Access links via Tools > Quick Links menu

To keep everything working, do not remove `<!-- ... -->` sections. 
<!-- Plugin description end -->

## Installation

- Using the IDE built-in plugin system:

  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "quick-links"</kbd> >
  <kbd>Install</kbd>

- Using JetBrains Marketplace:

  Go to [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID) and install it by clicking the <kbd>Install to ...</kbd> button in case your IDE is running.

  You can also download the [latest release](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID/versions) from JetBrains Marketplace and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

- Manually:

  Download the [latest release](https://github.com/ndy2/quick-links/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>


---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
[docs:plugin-description]: https://plugins.jetbrains.com/docs/intellij/plugin-user-experience.html#plugin-description-and-presentation
