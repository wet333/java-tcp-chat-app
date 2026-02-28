package online.awet.tui;

import online.awet.commons.Command;
import online.awet.commons.CommandSerializer;
import online.awet.system.ClientContext;
import online.awet.system.commands.ClientCommandProcessor;
import online.awet.system.commands.ClientInputParser;
import online.awet.system.commands.ClientInputParserException;
import online.awet.tui.renderers.ChatPanelRenderer;
import online.awet.tui.renderers.FrameRenderer;
import online.awet.tui.renderers.InputFieldRenderer;
import online.awet.tui.renderers.StatusBarRenderer;
import online.awet.tui.states.ChatPanelState;
import online.awet.tui.states.InputFieldState;
import online.awet.tui.states.StatusBarState;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChatTUI {

    private final Terminal terminal;
    private final StatusBarState statusBarState;
    private final ChatPanelState chatPanelState;
    private final InputFieldState inputFieldState;
    private final FrameRenderer frameRenderer;
    private final StatusBarRenderer statusBarRenderer;
    private final ChatPanelRenderer chatPanelRenderer;
    private final InputFieldRenderer inputFieldRenderer;
    private final BufferedWriter serverWriter;
    private final BlockingQueue<Command> commandQueue;
    private final AtomicBoolean running;

    // TUI Configurations
    private static final int SCROLL_LINES = 1; // Number of lines to scroll when using the mouse wheel

    private final TUILayout layout = new TUILayout()
        .fixed("statusBar", 1)
        .fill("chatPanel")
        .fixed("input", 1);

    public ChatTUI(BufferedWriter serverWriter, BlockingQueue<Command> commandQueue) {
        this.terminal = new Terminal();
        this.statusBarState = new StatusBarState();
        this.chatPanelState = new ChatPanelState();
        this.inputFieldState = new InputFieldState();
        this.frameRenderer = new FrameRenderer(terminal);
        this.statusBarRenderer = new StatusBarRenderer(terminal);
        this.chatPanelRenderer = new ChatPanelRenderer(terminal);
        this.inputFieldRenderer = new InputFieldRenderer(terminal);
        this.serverWriter = serverWriter;
        this.commandQueue = commandQueue;
        this.running = new AtomicBoolean(false);

        // Initialize the centralized client context before executor loading
        ClientContext.getInstance().initialize(
            statusBarState,
            chatPanelState,
            serverWriter,
            this::onCommandProcessed
        );

        // Triggers executor discovery — must run after ClientContext is initialized
        ClientCommandProcessor.getInstance();
    }

    public void start() throws IOException {
        terminal.enableRawMode();
        running.set(true);

        calculateLayout();
        terminal.clearScreen();
        renderAll();

        terminal.setOnResize(() -> {
            synchronized (terminal) {
                chatPanelState.resetScroll();
                calculateLayout();
                terminal.clearScreen();
                renderAll();
            }
        });

        Thread messageConsumer = new Thread(this::messageConsumerLoop, "tui-message-consumer");
        messageConsumer.setDaemon(true);
        messageConsumer.start();

        inputLoop();
    }

    public void shutdown() {
        running.set(false);
        terminal.disableRawMode();
        terminal.clearScreen();
        terminal.moveCursor(1, 1);
        terminal.flush();
    }

    private void calculateLayout() {
        layout.compute(terminal.getRows(), terminal.getCols());
    }

    private void renderAll() {
        terminal.hideCursor();
        frameRenderer.render(layout);

        renderChatPanel();
        renderStatusBar();
    }

    private void renderChatPanel() {
        chatPanelRenderer.render(chatPanelState, layout.region("chatPanel"));
    }

    private void renderInputField() {
        inputFieldRenderer.render(inputFieldState, layout.region("input"));
    }

    private void renderStatusBar() {
        statusBarRenderer.render(statusBarState, chatPanelState, layout.region("statusBar"));
        renderInputField();
    }

    /**
     * Callback invoked by {@link ClientContext#notifyStateChanged()} from the message-consumer
     * thread after an executor mutates state. Runs re-renders under the terminal lock.
     */
    private void onCommandProcessed() {
        synchronized (terminal) {
            renderChatPanel();
            renderStatusBar();
        }
    }

    private void messageConsumerLoop() {
        while (running.get()) {
            try {
                Command command = commandQueue.take();
                ClientCommandProcessor.getInstance().process(command);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void inputLoop() {
        while (running.get()) {
            try {
                int c = terminal.readChar();

                if (c == -1) {
                    break;
                }

                if (c == 3) {
                    shutdown();
                    System.exit(0);
                    return;
                }

                if (c == 13 || c == 10) {
                    handleEnter();
                } else if (c == 127 || c == 8) {
                    synchronized (terminal) {
                        inputFieldState.deleteChar();
                        renderInputField();
                    }
                } else if (c == 27) {
                    handleEscapeSequence();
                } else if (c >= 32 && c < 127) {
                    synchronized (terminal) {
                        inputFieldState.insertChar((char) c);
                        renderInputField();
                    }
                }
            } catch (IOException e) {
                break;
            }
        }
    }

    private void handleEnter() {
        String text = inputFieldState.submit();
        if (text.isBlank()) {
            synchronized (terminal) {
                renderInputField();
            }
            return;
        }

        try {
            Command command = new ClientInputParser().parse(text);
            serverWriter.write(CommandSerializer.toJson(command));
            serverWriter.newLine();
            serverWriter.flush();
        } catch (ClientInputParserException e) {
            chatPanelState.addMessage("Error: " + e.getMessage());
            synchronized (terminal) {
                renderChatPanel();
                renderStatusBar();
            }
        } catch (IOException e) {
            chatPanelState.addMessage("Error: could not send message");
        }

        synchronized (terminal) {
            renderInputField();
        }
    }

    private void handleEscapeSequence() throws IOException {
        if (!terminal.hasInput()) return;
        int second = terminal.readChar();
        if (second != '[') return;
        if (!terminal.hasInput()) return;
        int third = terminal.readChar();

        switch (third) {
            case 'D':
                synchronized (terminal) {
                    inputFieldState.moveCursorLeft();
                    renderInputField();
                }
                break;
            case 'C':
                synchronized (terminal) {
                    inputFieldState.moveCursorRight();
                    renderInputField();
                }
                break;
            case '<':
                handleSgrMouseSequence();
                break;
            default:
                break;
        }
    }

    private void handleSgrMouseSequence() throws IOException {
        StringBuilder seq = new StringBuilder();
        int ch;
        while ((ch = terminal.readChar()) != 'M' && ch != 'm' && ch != -1) {
            seq.append((char) ch);
        }
        String[] parts = seq.toString().split(";");
        if (parts.length == 3) {
            try {
                int button = Integer.parseInt(parts[0]);
                int col = Integer.parseInt(parts[1]);
                int row = Integer.parseInt(parts[2]);
                handleMouseEvent(button, col, row);
            } catch (NumberFormatException ignored) {
            }
        }
    }

    private void handleMouseEvent(int button, int col, int row) {
        boolean isMotion = (button & 32) != 0 && button != 64 && button != 65;

        // Mouse position update handling
        if (isMotion) {
            handleMouseMotion(col, row);
            return;
        }

        // Status bar scroll to bottom button handling
        TUILayout.Region statusRegion = layout.region("statusBar");
        if (button == 0 && row == statusRegion.firstRow
                && statusBarRenderer.getButtonStartCol() != -1
                && col >= statusBarRenderer.getButtonStartCol()
                && col <= statusBarRenderer.getButtonEndCol()) {
            synchronized (terminal) {
                chatPanelState.resetScroll();
                renderChatPanel();
                renderStatusBar();
            }
            return;
        }

        // Chat scroll handling
        TUILayout.Region chatRegion = layout.region("chatPanel");
        if (row < chatRegion.firstRow || row > chatRegion.lastRow) return;
        if (col < chatRegion.contentCol || col >= chatRegion.contentCol + chatRegion.contentWidth) return;

        synchronized (terminal) {
            if (button == 64) {
                chatPanelState.scrollUp(SCROLL_LINES);
                renderChatPanel();
                renderStatusBar();
            } else if (button == 65) {
                chatPanelState.scrollDown(SCROLL_LINES);
                renderChatPanel();
                renderStatusBar();
            }
        }
    }

    private void handleMouseMotion(int col, int row) {
        TUILayout.Region statusRegion = layout.region("statusBar");
        boolean overButton = row == statusRegion.firstRow
                && statusBarRenderer.getButtonStartCol() != -1
                && col >= statusBarRenderer.getButtonStartCol()
                && col <= statusBarRenderer.getButtonEndCol();

        if (overButton != statusBarRenderer.isHovering()) {
            synchronized (terminal) {
                statusBarRenderer.setHovering(overButton);
                renderStatusBar();
            }
        }
    }
}
