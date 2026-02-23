package online.awet.tui.renderers;

import online.awet.tui.Terminal;
import online.awet.tui.states.StatusBarState;

public class StatusBarRenderer {

    private final Terminal terminal;

    public StatusBarRenderer(Terminal terminal) {
        this.terminal = terminal;
    }

    public void render(StatusBarState state, int row, int cols) {
        terminal.moveCursor(row, 2);
        terminal.clearLine();

        terminal.moveCursor(row, 1);
        terminal.print("│");

        int innerWidth = cols - 2;

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

        int usedChars = (" Usuario: " + state.getUsername() + "  │  " + "Estado: "
                + (state.isAuthenticated() ? "Autenticado" : "No autenticado")).length();
        int padding = innerWidth - usedChars;
        if (padding > 0) {
            terminal.print(" ".repeat(padding));
        }

        terminal.moveCursor(row, cols);
        terminal.print("│");

        terminal.flush();
    }
}
