package online.awet.tui;

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
    private final BlockingQueue<String> messageQueue;
    private final AtomicBoolean running;

    private int statusBarRow;
    private int chatStartRow;
    private int chatEndRow;
    private int inputSeparatorRow;
    private int inputRow;
    private int frameBottomRow;

    public ChatTUI(BufferedWriter serverWriter, BlockingQueue<String> messageQueue) {
        this.terminal = new Terminal();
        this.statusBarState = new StatusBarState();
        this.chatPanelState = new ChatPanelState();
        this.inputFieldState = new InputFieldState();
        this.frameRenderer = new FrameRenderer(terminal);
        this.statusBarRenderer = new StatusBarRenderer(terminal);
        this.chatPanelRenderer = new ChatPanelRenderer(terminal);
        this.inputFieldRenderer = new InputFieldRenderer(terminal);
        this.serverWriter = serverWriter;
        this.messageQueue = messageQueue;
        this.running = new AtomicBoolean(false);
    }

    public void start() throws IOException {
        terminal.enableRawMode();
        running.set(true);

        calculateLayout();
        terminal.clearScreen();
        renderAll();

        terminal.setOnResize(() -> {
            synchronized (terminal) {
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
        int rows = terminal.getRows();

        statusBarRow = 1;
        chatStartRow = 3;
        inputSeparatorRow = rows - 2;
        inputRow = rows - 1;
        frameBottomRow = rows;
        chatEndRow = inputSeparatorRow;
    }

    private void renderAll() {
        int cols = terminal.getCols();
        terminal.hideCursor();
        frameRenderer.render(terminal.getRows(), cols, statusBarRow, chatStartRow, inputSeparatorRow, inputRow);
        statusBarRenderer.render(statusBarState, statusBarRow + 1, cols);

        renderChatPanel();
        renderInputField();
    }

    private void renderChatPanel() {
        int cols = terminal.getCols();
        chatPanelRenderer.render(chatPanelState, chatStartRow + 1, chatEndRow, cols);
    }

    private void renderInputField() {
        int cols = terminal.getCols();
        inputFieldRenderer.render(inputFieldState, inputRow, cols);
    }

    private void renderStatusBar() {
        int cols = terminal.getCols();
        statusBarRenderer.render(statusBarState, statusBarRow + 1, cols);
        renderInputField();
    }

    private void messageConsumerLoop() {
        while (running.get()) {
            try {
                String message = messageQueue.take();
                statusBarState.updateFromServerMessage(message);
                chatPanelState.addMessage(message);

                synchronized (terminal) {
                    renderStatusBar();
                    renderChatPanel();
                    renderInputField();
                }
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
            serverWriter.write(text);
            serverWriter.newLine();
            serverWriter.flush();
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
            default:
                break;
        }
    }
}
