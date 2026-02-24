package online.awet.tui.renderers;

import online.awet.tui.TUILayout;
import online.awet.tui.Terminal;
import online.awet.tui.states.ChatPanelState;
import online.awet.tui.states.StatusBarState;

public class StatusBarRenderer {

    private final Terminal terminal;
    private int buttonStartCol = -1;
    private int buttonEndCol = -1;
    private boolean hovering = false;

    public StatusBarRenderer(Terminal terminal) {
        this.terminal = terminal;
    }

    public void render(StatusBarState state, ChatPanelState chatState, TUILayout.Region region) {
        terminal.moveCursor(region.firstRow, region.contentCol);
        terminal.clearLine();

        terminal.moveCursor(region.firstRow, 1);
        terminal.print("│");

        terminal.setBold();
        terminal.print(" Usuario: ");
        terminal.setColor(Terminal.Color.CYAN);
        terminal.print(state.getUsername());
        terminal.resetColor();

        terminal.print("  │  ");

        terminal.setBold();
        terminal.print("Estado: ");
        if (state.isAuthenticated()) {
            terminal.setColor(Terminal.Color.GREEN);
            terminal.print("Autenticado");
        } else {
            terminal.setColor(Terminal.Color.RED);
            terminal.print("No autenticado");
        }
        terminal.resetColor();

        String statusText = state.isAuthenticated() ? "Autenticado" : "No autenticado";
        int leftUsed = (" Usuario: " + state.getUsername() + "  │  " + "Estado: " + statusText).length();

        String scrollInfo = "Linea: " + chatState.getLastVisibleLine() + "/" + chatState.getTotalWrappedLines();
        String buttonText = " Ultimo Mensaje ";

        int rightUsed = scrollInfo.length() + 2 + buttonText.length() + 1;

        int padding = region.contentWidth - leftUsed - rightUsed;
        if (padding > 0) {
            terminal.print(" ".repeat(padding));
        }

        terminal.setColor(Terminal.Color.GRAY);
        terminal.print(scrollInfo);
        terminal.resetColor();
        terminal.print("  ");

        int currentCol = region.contentCol + leftUsed + Math.max(padding, 0) + scrollInfo.length() + 2;
        buttonStartCol = currentCol;
        buttonEndCol = currentCol + buttonText.length() - 1;

        if (hovering) {
            terminal.setColor(Terminal.Color.BRIGHT_CYAN);
            terminal.setReverseVideo();
        } else {
            terminal.setBold();
            terminal.setColor(Terminal.Color.BRIGHT_CYAN);
        }
        terminal.print(buttonText);
        terminal.resetColor();

        terminal.moveCursor(region.firstRow, region.contentCol + region.contentWidth + 1);
        terminal.print("│");

        terminal.flush();
    }

    public void setHovering(boolean hovering) {
        this.hovering = hovering;
    }

    public boolean isHovering() {
        return hovering;
    }

    public int getButtonStartCol() {
        return buttonStartCol;
    }

    public int getButtonEndCol() {
        return buttonEndCol;
    }
}
