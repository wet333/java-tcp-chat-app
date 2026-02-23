package online.awet.tui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TUILayout {

    public static final int BORDER = 1;
    public static final int PADDING = 1;

    private final List<Slot> slots = new ArrayList<>();
    private final Map<String, Region> regions = new LinkedHashMap<>();
    private final List<Integer> separatorRows = new ArrayList<>();
    private int topBorderRow;
    private int bottomBorderRow;
    private int totalCols;

    public TUILayout fixed(String name, int height) {
        slots.add(new Slot(name, height));
        return this;
    }

    public TUILayout fill(String name) {
        slots.add(new Slot(name, -1));
        return this;
    }

    public void compute(int terminalRows, int terminalCols) {
        totalCols = terminalCols;
        topBorderRow = 1;
        bottomBorderRow = terminalRows;

        int chromeRows = 2 * BORDER + (slots.size() - 1);
        int fixedSum = slots.stream()
            .filter(s -> s.fixedHeight > 0)
            .mapToInt(s -> s.fixedHeight).sum();
        long fillCount = slots.stream()
            .filter(s -> s.fixedHeight < 0).count();
        int fillHeight = fillCount > 0
            ? (terminalRows - chromeRows - fixedSum) / (int) fillCount
            : 0;

        int contentCol = BORDER + PADDING + 1;
        int contentWidth = terminalCols - 2 * (BORDER + PADDING);

        regions.clear();
        separatorRows.clear();

        int cursor = topBorderRow + BORDER;
        for (int i = 0; i < slots.size(); i++) {
            Slot slot = slots.get(i);
            int h = slot.fixedHeight > 0 ? slot.fixedHeight : fillHeight;
            regions.put(slot.name, new Region(cursor, cursor + h - 1, h, contentCol, contentWidth));
            cursor += h;
            if (i < slots.size() - 1) {
                separatorRows.add(cursor);
                cursor += 1;
            }
        }
    }

    public Region region(String name) {
        return regions.get(name);
    }

    public int topBorderRow() {
        return topBorderRow;
    }

    public int bottomBorderRow() {
        return bottomBorderRow;
    }

    public List<Integer> separatorRows() {
        return Collections.unmodifiableList(separatorRows);
    }

    public int totalCols() {
        return totalCols;
    }

    public static class Region {

        public final int firstRow;
        public final int lastRow;
        public final int height;
        public final int contentCol;
        public final int contentWidth;

        Region(int firstRow, int lastRow, int height, int contentCol, int contentWidth) {
            this.firstRow = firstRow;
            this.lastRow = lastRow;
            this.height = height;
            this.contentCol = contentCol;
            this.contentWidth = contentWidth;
        }
    }

    private static class Slot {

        final String name;
        final int fixedHeight;

        Slot(String name, int fixedHeight) {
            this.name = name;
            this.fixedHeight = fixedHeight;
        }
    }
}
