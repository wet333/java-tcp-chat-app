# java-tcp-chat-app

A simple CLI chat application built on TCP sockets with a custom terminal UI.

<hr>

Hey there! Thanks for checking out this project.

I'm genuinely excited to see where this chat app can go, and I'm curious to discover what it might turn into with the input of others. Any kind of collaboration is welcome here, whether you want to share ideas, offer feedback, contribute code, or just learn together.

This is about exploring possibilities and seeing what we can create together. No matter your experience level, I'd love to have you onboard.

<hr>

## Getting Started (DEV)

### Prerequisites

| Tool | Version | Notes |
|---|---|---|
| Java JDK | 21+ | `keytool` (bundled with the JDK) is required for TLS certificate generation |
| Maven | 3.8+ | Used for building all modules |

Verify your setup:
```bash
java -version    # should show 21+
mvn -version
keytool -help    # should be available if JDK is installed (not just JRE)
```

### Build

Clone the repo and run the build script from the project root:

```bash
git clone <repo-url>
cd java-tcp-chat-app
./build_project.sh
```

The script will:
1. Generate TLS certificates for `localhost` automatically (stored in `dist/tls/dev/`)
2. Compile all Maven modules
3. Package the server and client as fat JARs in `dist/`

> **Note:** TLS certificates are only generated once. To regenerate them (e.g. if they expire), delete `dist/tls/dev/` and re-run `./build_project.sh`.

### Run

Both the server and client must be run from the `dist/` directory so they can find the keystore files.

**Terminal 1 — Server:**
```bash
cd dist/
cp tls/dev/server.keystore.jks ./
java -jar Server_DEV_*.jar
```

**Terminal 2 — Client:**
```bash
cd dist/
java -jar Client_DEV_*.jar
```

The client has the TLS truststore bundled inside the JAR — no extra files or flags needed.

By default the client connects to `localhost:7560`. To connect to a different host or port:
```bash
java -jar Client_DEV_*.jar <serverIp> <port>
```

### In-app commands

Once connected, type `/help` to see all available commands. Common ones:

| Command | Description |
|---|---|
| `/register -username <u> -password <p>` | Create an account |
| `/login -username <u> -password <p>` | Log in |
| `/logout` | Log out |
| `/help` | Show all commands |

Plain text (no leading `/`) sends a message to all connected users.

<hr>

## Production Setup

For production deployments, copy `tls.env.example` to `tls.env`, fill in your server's real hostname and credentials, then rebuild:

```bash
cp tls.env.example tls.env
# edit tls.env with your PROD_TLS_* values
./build_project.sh
```

This produces additional `Server_PROD_*.jar` and `Client_PROD_*.jar` artifacts. See `tls.env.example` for all available options.

<hr>

## Architecture

This is a Maven multi-module project with three modules:

- **TCPChatCommons** — shared data model (`Command`, `CommandType`, routing interfaces)
- **TCPChatServer** — multi-threaded TCP server (fixed thread pool, command-based routing)
- **TCPChatClient** — CLI client with a custom ANSI terminal UI *(Unix/Linux/macOS only)*

All communication between client and server is encrypted with TLS. See [`docs/README.md`](docs/README.md) for detailed architecture documentation.
