# Contributing to EverTerra-TPA

Thank you for your interest in contributing! 🎉

## 📋 Table of Contents

- [Code of Conduct](#code-of-conduct)
- [How to Contribute](#how-to-contribute)
- [Issue Guidelines](#issue-guidelines)
- [Pull Request Guidelines](#pull-request-guidelines)
- [Commit Message Convention](#commit-message-convention)
- [Development Setup](#development-setup)

---

## Code of Conduct

Be respectful, constructive, and collaborative. Harassment or toxic behavior will not be tolerated.

---

## How to Contribute

1. **Fork** the repository
2. **Create** a feature branch: `git checkout -b feat/my-feature`
3. **Commit** your changes (see [Commit Convention](#commit-message-convention))
4. **Push** to your fork: `git push origin feat/my-feature`
5. **Open** a Pull Request against the `dev` branch

---

## Issue Guidelines

### 🐛 Bug Reports

Please include:
- Plugin version
- Server version (Paper / Spigot / Fork)
- Java version (`java -version`)
- Steps to reproduce
- Expected vs actual behavior
- Relevant log output (use code blocks)

### 💡 Feature Requests

Please include:
- Clear description of the feature
- Use case / why it's useful
- Any implementation ideas (optional)

---

## Pull Request Guidelines

- Target the `dev` branch, not `master`
- One PR = one feature or fix
- Keep changes focused and minimal
- Add JavaDoc for new public methods
- Ensure `./gradlew build` passes
- Update `README.md` if needed
- Reference related issues with `Closes #123`

### Code Style

- Follow existing code conventions
- UTF-8 encoding for all files
- Use `final` for immutable locals where appropriate
- Prefer `ConcurrentHashMap` for shared state
- Log with plugin logger, not `System.out`

---

## Commit Message Convention

We follow [Conventional Commits](https://www.conventionalcommits.org/):

```
<type>: <short description>

[optional body]
```

### Types

| Type | Usage |
|------|-------|
| `feat` | New feature |
| `fix` | Bug fix |
| `docs` | Documentation only |
| `style` | Formatting, missing semicolons, etc. |
| `refactor` | Code change that neither fixes a bug nor adds a feature |
| `perf` | Performance improvement |
| `test` | Adding or updating tests |
| `chore` | Build process, CI, dependencies |
| `ci` | CI/CD configuration changes |

### Examples

```
feat: add multi-world teleport restriction
fix: NPE when target player goes offline during countdown
docs: update README with Geyser setup instructions
refactor: extract cooldown logic to CooldownService
chore: upgrade Gradle to 8.10
```

---

## Development Setup

```bash
# Prerequisites: JDK 21+

# Clone
git clone https://github.com/EverTerra/EverTerra-TPA.git
cd EverTerra-TPA

# Build
./gradlew build

# Test on local server
cp build/libs/EverTerra-TPA-*.jar test-server/plugins/

# If behind a proxy, edit gradle.properties
# Uncomment the HTTP proxy lines
```

### Project Structure

```
src/main/java/com/everterra/tpa/
├── EverTerraTPA.java          # Main plugin class
├── command/                   # Command handlers
├── config/                    # Configuration management
├── core/                      # Core logic (requests, cooldowns)
├── economy/                   # Vault integration
├── gui/                       # GUI (Java + Bedrock)
├── i18n/                      # Internationalization
├── listener/                  # Event listeners
├── teleport/                  # Teleport scheduler
└── util/                      # Utilities
```

---

## Questions?

Open a [Discussion](https://github.com/EverTerra/EverTerra-TPA/discussions) or join our community!
