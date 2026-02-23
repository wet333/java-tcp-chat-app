package online.awet.tui.renderers;

import online.awet.tui.TUILayout;
import online.awet.tui.Terminal;
import online.awet.tui.states.StatusBarState;

public class StatusBarRenderer {

    private final Terminal terminal;

    public StatusBarRenderer(Terminal terminal) {
        this.terminal = terminal;
    }

    public void render(StatusBarState state, TUILayout.Region region) {
        // Move cursor to the start of the region and clear the entire line
        terminal.moveCursor(region.firstRow, region.contentCol);
        terminal.clearLine();

        // Draw the left border "│"
        terminal.moveCursor(region.firstRow, 1);
        terminal.print("│");

        // Draw the "Usuario:" label in bold followed by the username in cyan
        terminal.setBold();
        terminal.print(" Usuario: ");
        terminal.setColor(Terminal.Color.CYAN);
        terminal.print(state.getUsername());
        terminal.resetColor();

        // Draw the separator between the user section and the status section
        terminal.print("  │  ");

        // Draw the "Estado:" label in bold and its value colored by auth status:
        // green if authenticated, red if not
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

        // Calculate and draw space padding to fill the rest of the region
        String statusText = state.isAuthenticated() ? "Autenticado" : "No autenticado";
        int usedChars = (" Usuario: " + state.getUsername() + "  │  " + "Estado: " + statusText).length();
        int padding = region.contentWidth - usedChars;
        if (padding > 0) {
            terminal.print(" ".repeat(padding));
        }

        // Draw the right border "│" at the end of the region
        terminal.moveCursor(region.firstRow, region.contentCol + region.contentWidth + 1);
        terminal.print("│");

        // Flush all buffered content to the terminal
        terminal.flush();
    }
}
