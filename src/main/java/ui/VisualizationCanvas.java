package ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import model.AlgorithmStep;
import model.ArrayUtils;
import model.TreeNodeSnapshot;
import model.VisualizationType;
import structures.CustomLinearQueue;
import structures.CustomQueue;

/**
 * Dynamic JavaFX visualization canvas supporting all four CSC2107 Algorithm Explorer modules:
 * - Module A: Fibonacci 3-Way Table/Bars, Factorial Recursive vs Iterative, Animated 3-Peg Tower of Hanoi
 * - Module B: Fixed-Capacity Stack, Bracket Checker Stack, Linear Queue ("False Full"), Circular Queue,
 *             Singly Linked List, Doubly Linked List (bidirectional prev/next), and BST
 * - Module C: 5 Sorting Algorithms (with Yellow/Green partition highlights) + Side-by-Side "Compare All" Table
 * - Module D: Experimental Time Complexity Benchmark Table & O(n^2) vs O(n log n) Growth Plots
 */
public class VisualizationCanvas extends Pane {

    private static final Color BG_COLOR = Color.web("#0F172A");
    private static final Color GRID_LINE_COLOR = Color.web("#1E293B");
    private static final Color COLOR_DEFAULT = Color.web("#3B82F6");
    private static final Color COLOR_COMPARING = Color.web("#FACC15"); // Yellow
    private static final Color COLOR_SWAPPING = Color.web("#EF4444");  // Red
    private static final Color COLOR_SORTED = Color.web("#10B981");    // Green
    private static final Color COLOR_POINTER = Color.web("#A855F7");   // Purple badge
    private static final Color COLOR_TEXT = Color.web("#F8FAFC");
    private static final Color COLOR_SUBTEXT = Color.web("#94A3B8");

    private final Canvas canvas;
    private VisualizationType currentType = VisualizationType.ARRAY_BARS;
    private AlgorithmStep currentStep = null;
    private String[] tableHeaders = null;
    private String[][] tableRows = null;

    public VisualizationCanvas() {
        this.canvas = new Canvas(800, 460);
        getChildren().add(canvas);

        widthProperty().addListener((obs, oldVal, newVal) -> {
            canvas.setWidth(newVal.doubleValue());
            render();
        });
        heightProperty().addListener((obs, oldVal, newVal) -> {
            canvas.setHeight(newVal.doubleValue());
            render();
        });
    }

    public void updateState(VisualizationType type, AlgorithmStep step) {
        updateState(type, step, null, null);
    }

    public void updateState(VisualizationType type, AlgorithmStep step, String[] headers, String[][] rows) {
        this.currentType = type != null ? type : VisualizationType.ARRAY_BARS;
        this.currentStep = step;
        this.tableHeaders = headers;
        this.tableRows = rows;
        render();
    }

    public void render() {
        double w = canvas.getWidth();
        double h = canvas.getHeight();
        if (w <= 10 || h <= 10) {
            return;
        }

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(BG_COLOR);
        gc.fillRoundRect(0, 0, w, h, 12, 12);

        gc.setStroke(GRID_LINE_COLOR);
        gc.setLineWidth(1.0);
        for (double x = 40; x < w; x += 40) {
            gc.strokeLine(x, 0, x, h);
        }
        for (double y = 40; y < h; y += 40) {
            gc.strokeLine(0, y, w, y);
        }

        if (currentStep == null && tableRows == null) {
            drawEmptyMessage(gc, w, h, "Click 'Execute' to run and visualize the selected module.");
            return;
        }

        switch (currentType) {
            case ARRAY_BARS:
                renderArrayBars(gc, w, h, currentStep);
                break;
            case LINKED_LIST_NODES:
                renderLinkedList(gc, w, h, currentStep, false);
                break;
            case DOUBLY_LINKED_LIST_NODES:
                renderLinkedList(gc, w, h, currentStep, true);
                break;
            case STACK_VIEW:
                renderStack(gc, w, h, currentStep, false);
                break;
            case BRACKET_CHECKER_VIEW:
                renderStack(gc, w, h, currentStep, true);
                break;
            case LINEAR_QUEUE_VIEW:
                renderPhysicalQueue(gc, w, h, currentStep, false);
                break;
            case QUEUE_VIEW:
                renderPhysicalQueue(gc, w, h, currentStep, true);
                break;
            case BST_TREE:
                renderBinarySearchTree(gc, w, h, currentStep);
                break;
            case HANOI_TOWERS:
                renderTowerOfHanoi(gc, w, h, currentStep);
                break;
            case RECURSION_TABLE_VIEW:
                renderTableAndBarsView(gc, w, h, currentStep, "Module A: Recursion Lab Comparison Table");
                break;
            case SORT_COMPARE_TABLE_VIEW:
                renderTableAndBarsView(gc, w, h, currentStep, "Module C: Side-by-Side Comparison of All Sorting Algorithms");
                break;
            case COMPLEXITY_BENCHMARK_VIEW:
                renderBenchmarkPlotsAndTable(gc, w, h);
                break;
            default:
                renderArrayBars(gc, w, h, currentStep);
                break;
        }
    }

    private void drawEmptyMessage(GraphicsContext gc, double w, double h, String message) {
        gc.setFill(COLOR_SUBTEXT);
        gc.setFont(Font.font("System", FontWeight.SEMI_BOLD, 15));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(message, w / 2.0, h / 2.0);
    }

    private void renderArrayBars(GraphicsContext gc, double w, double h, AlgorithmStep step) {
        int[] arr = step.getArraySnapshot();
        if (arr == null || arr.length == 0) {
            drawEmptyMessage(gc, w, h, "Array is empty.");
            return;
        }

        int n = arr.length;
        int maxAbs = 1;
        for (int i = 0; i < n; i++) {
            int abs = arr[i] < 0 ? -arr[i] : arr[i];
            if (abs > maxAbs) {
                maxAbs = abs;
            }
        }

        double leftPad = 45;
        double rightPad = 45;
        double topPad = 55;
        double bottomPad = 75;
        double availW = w - leftPad - rightPad;
        double availH = h - topPad - bottomPad;

        double slotW = availW / n;
        double gap = Math.max(2.0, Math.min(10.0, slotW * 0.18));
        double barW = Math.max(4.0, slotW - gap);

        double baselineY = h - bottomPad;
        gc.setStroke(Color.web("#334155"));
        gc.setLineWidth(2.0);
        gc.strokeLine(leftPad - 10, baselineY, w - rightPad + 10, baselineY);

        for (int i = 0; i < n; i++) {
            int val = arr[i];
            double absRatio = (double) Math.abs(val) / (double) maxAbs;
            double barH = Math.max(18.0, absRatio * (availH - 25.0));
            double x = leftPad + i * slotW + gap / 2.0;
            double y = baselineY - barH;

            Color barColor = resolveIndexColor(i, step);
            gc.setFill(barColor);
            gc.fillRoundRect(x, y, barW, barH, 6, 6);

            gc.setStroke(barColor.brighter());
            gc.setLineWidth(1.2);
            gc.strokeRoundRect(x, y, barW, barH, 6, 6);

            double centerX = x + barW / 2.0;
            gc.setFill(COLOR_TEXT);
            int fontSize = n > 28 ? 10 : (n > 18 ? 11 : 13);
            gc.setFont(Font.font("System", FontWeight.BOLD, fontSize));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText(String.valueOf(val), centerX, y - 6);

            gc.setFill(COLOR_SUBTEXT);
            gc.setFont(Font.font("Consolas", FontWeight.NORMAL, Math.max(9, fontSize - 1)));
            gc.fillText("[" + i + "]", centerX, baselineY + 18);

            StringBuilder ptrBuilder = new StringBuilder();
            if (step.getPointerA() == i && !step.getPointerALabel().isEmpty()) {
                ptrBuilder.append(step.getPointerALabel());
            }
            if (step.getPointerB() == i && !step.getPointerBLabel().isEmpty()) {
                if (ptrBuilder.length() > 0) ptrBuilder.append("/");
                ptrBuilder.append(step.getPointerBLabel());
            }
            if (step.getPointerC() == i && !step.getPointerCLabel().isEmpty()) {
                if (ptrBuilder.length() > 0) ptrBuilder.append("/");
                ptrBuilder.append(step.getPointerCLabel());
            }

            if (ptrBuilder.length() > 0) {
                drawPointerBadge(gc, centerX, baselineY + 38, ptrBuilder.toString());
            }
        }
    }

    /**
     * Renders either a Singly Linked List ([ data | next ]) or a Doubly Linked List ([ prev | data | next ]).
     */
    private void renderLinkedList(GraphicsContext gc, double w, double h, AlgorithmStep step, boolean isDoubly) {
        int[] arr = step.getArraySnapshot();
        gc.setFill(COLOR_TEXT);
        gc.setFont(Font.font("System", FontWeight.BOLD, 14));
        gc.setTextAlign(TextAlignment.LEFT);
        String header = isDoubly
                ? "Custom Doubly Linked List (Bidirectional DoublyNode: NULL <- [prev | data | next] <-> ... -> NULL)"
                : "Custom Singly Linked List (Node Chain: HEAD -> [data | next] -> ... -> NULL)";
        gc.fillText(header, 24, 30);

        if (arr == null || arr.length == 0) {
            drawEmptyMessage(gc, w, h, "HEAD / TAIL ──► NULL (Linked List is currently empty)");
            return;
        }

        int n = arr.length;
        double nodeW = isDoubly ? 104 : 86;
        double nodeH = 44;
        double arrowGap = 44;
        double unitW = nodeW + arrowGap;

        int maxCols = Math.max(1, (int) ((w - 110) / unitW));
        double startX = 45;
        double startY = 95;
        double rowGap = 110;

        for (int i = 0; i < n; i++) {
            int row = i / maxCols;
            int col = i % maxCols;
            double x = startX + col * unitW;
            double y = startY + row * rowGap;

            Color fill = resolveIndexColor(i, step);
            gc.setFill(fill);
            gc.fillRoundRect(x, y, nodeW, nodeH, 8, 8);
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(1.5);
            gc.strokeRoundRect(x, y, nodeW, nodeH, 8, 8);

            if (isDoubly) {
                double d1 = x + nodeW * 0.24;
                double d2 = x + nodeW * 0.76;
                gc.strokeLine(d1, y, d1, y + nodeH);
                gc.strokeLine(d2, y, d2, y + nodeH);

                gc.setFill(Color.web("#0F172A"));
                gc.fillOval(x + nodeW * 0.12 - 3.5, y + nodeH / 2.0 - 3.5, 7, 7);
                gc.fillOval(x + nodeW * 0.88 - 3.5, y + nodeH / 2.0 - 3.5, 7, 7);

                gc.setFont(Font.font("System", FontWeight.BOLD, 14));
                gc.setTextAlign(TextAlignment.CENTER);
                gc.fillText(String.valueOf(arr[i]), x + nodeW * 0.50, y + 27);
            } else {
                double dividerX = x + nodeW * 0.68;
                gc.strokeLine(dividerX, y, dividerX, y + nodeH);
                gc.setFill(Color.web("#0F172A"));
                gc.setFont(Font.font("System", FontWeight.BOLD, 14));
                gc.setTextAlign(TextAlignment.CENTER);
                gc.fillText(String.valueOf(arr[i]), x + (nodeW * 0.34), y + 27);
                gc.fillOval(dividerX + (nodeW * 0.16) - 4, y + nodeH / 2.0 - 4, 8, 8);
            }

            gc.setFill(COLOR_SUBTEXT);
            gc.setFont(Font.font("Consolas", FontWeight.NORMAL, 11));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText("Node #" + i, x + nodeW / 2.0, y - 8);

            StringBuilder badge = new StringBuilder();
            if (i == 0) badge.append("HEAD");
            if (i == n - 1) {
                if (badge.length() > 0) badge.append("/");
                badge.append("TAIL");
            }
            if (step.getPointerC() == i && !step.getPointerCLabel().isEmpty()) {
                if (badge.length() > 0) badge.append("/");
                badge.append(step.getPointerCLabel());
            }
            if (badge.length() > 0) {
                drawPointerBadge(gc, x + nodeW / 2.0, y + nodeH + 22, badge.toString());
            }

            double arrowStartX = x + nodeW;
            double arrowY = y + nodeH / 2.0;
            if (i < n - 1) {
                if (col < maxCols - 1) {
                    if (isDoubly) {
                        drawHorizontalArrow(gc, arrowStartX, arrowY - 5, arrowStartX + arrowGap, Color.web("#38BDF8"));
                        drawLeftArrow(gc, arrowStartX + arrowGap, arrowY + 6, arrowStartX, Color.web("#FACC15"));
                    } else {
                        drawHorizontalArrow(gc, arrowStartX, arrowY, arrowStartX + arrowGap, Color.web("#94A3B8"));
                    }
                } else {
                    drawHorizontalArrow(gc, arrowStartX, arrowY, arrowStartX + 20, Color.web("#94A3B8"));
                }
            } else {
                drawHorizontalArrow(gc, arrowStartX, arrowY, arrowStartX + 28, Color.web("#F87171"));
                double nullX = arrowStartX + 28;
                gc.setFill(Color.web("#1E293B"));
                gc.fillRoundRect(nullX, y + 6, 52, 32, 6, 6);
                gc.setStroke(Color.web("#F87171"));
                gc.strokeRoundRect(nullX, y + 6, 52, 32, 6, 6);
                gc.setFill(Color.web("#F87171"));
                gc.setFont(Font.font("Consolas", FontWeight.BOLD, 12));
                gc.fillText("NULL", nullX + 26, y + 26);
            }
        }
    }

    /**
     * Renders either the Fixed-Capacity integer Stack or the Bracket Checker char Stack.
     */
    private void renderStack(GraphicsContext gc, double w, double h, AlgorithmStep step, boolean asCharBrackets) {
        int[] arr = step.getArraySnapshot();
        int size = arr != null ? arr.length : 0;
        int cap = Math.max(step.getStructureCapacity(), Math.max(size, 6));
        int displaySlots = Math.min(cap, 12);

        gc.setFill(COLOR_TEXT);
        gc.setFont(Font.font("System", FontWeight.BOLD, 14));
        gc.setTextAlign(TextAlignment.LEFT);
        String title = asCharBrackets
                ? "Stack Application: Balanced Bracket Checker (Active Opening Brackets on CustomStack)"
                : "Custom LIFO Stack (Fixed Capacity = " + cap + ", Size = " + size
                + ", is_empty = " + (size == 0) + ", is_full = " + (size >= cap) + ")";
        gc.fillText(title, 24, 30);

        double boxW = 175;
        double availH = h - 105;
        double slotH = Math.min(40.0, Math.max(22.0, availH / displaySlots));
        double centerX = w / 2.0;
        double boxX = centerX - boxW / 2.0;
        double bottomY = h - 38;

        double containerTopY = bottomY - displaySlots * slotH - 6;
        gc.setStroke(Color.web("#64748B"));
        gc.setLineWidth(3.0);
        gc.strokeLine(boxX - 6, containerTopY, boxX - 6, bottomY + 4);
        gc.strokeLine(boxX + boxW + 6, containerTopY, boxX + boxW + 6, bottomY + 4);
        gc.strokeLine(boxX - 6, bottomY + 4, boxX + boxW + 6, bottomY + 4);

        for (int i = 0; i < displaySlots; i++) {
            double slotY = bottomY - (i + 1) * slotH;

            gc.setFill(COLOR_SUBTEXT);
            gc.setFont(Font.font("Consolas", FontWeight.NORMAL, 12));
            gc.setTextAlign(TextAlignment.RIGHT);
            gc.fillText("slot [" + i + "]", boxX - 16, slotY + slotH / 2.0 + 4);

            if (i < size) {
                Color c = resolveIndexColor(i, step);
                gc.setFill(c);
                gc.fillRoundRect(boxX, slotY + 2, boxW, slotH - 4, 6, 6);
                gc.setStroke(Color.WHITE);
                gc.setLineWidth(1.2);
                gc.strokeRoundRect(boxX, slotY + 2, boxW, slotH - 4, 6, 6);

                String cellText = asCharBrackets ? ("'" + (char) arr[i] + "'") : String.valueOf(arr[i]);
                gc.setFill(Color.web("#0F172A"));
                gc.setFont(Font.font("System", FontWeight.BOLD, 15));
                gc.setTextAlign(TextAlignment.CENTER);
                gc.fillText(cellText, centerX, slotY + slotH / 2.0 + 5);
            } else {
                gc.setStroke(Color.web("#1E293B"));
                gc.setLineWidth(1.0);
                gc.strokeRoundRect(boxX, slotY + 2, boxW, slotH - 4, 6, 6);
                gc.setFill(Color.web("#334155"));
                gc.setFont(Font.font("Consolas", FontWeight.NORMAL, 11));
                gc.setTextAlign(TextAlignment.CENTER);
                gc.fillText("(empty)", centerX, slotY + slotH / 2.0 + 4);
            }

            if (i == size - 1 && size > 0) {
                drawPointerBadge(gc, boxX + boxW + 85, slotY + slotH / 2.0 + 4, "◄── TOP (" + i + ")");
            }
        }

        if (size == 0) {
            drawPointerBadge(gc, boxX + boxW + 90, bottomY - 10, "TOP = -1 (EMPTY)");
        }
    }

    /**
     * Renders the physical fixed-size backing array of either the Linear Queue or the Circular Queue,
     * showing front and rear pointers, occupied slots, and (for Linear Queue) vacated "False Full" slots!
     */
    private void renderPhysicalQueue(GraphicsContext gc, double w, double h, AlgorithmStep step, boolean isCircular) {
        int[] physical = step.getArraySnapshot();
        int cap = physical != null && physical.length > 0 ? physical.length : Math.max(6, step.getStructureCapacity());
        int emptySentinel = isCircular ? CustomQueue.EMPTY_SLOT : CustomLinearQueue.EMPTY_SLOT;

        gc.setFill(COLOR_TEXT);
        gc.setFont(Font.font("System", FontWeight.BOLD, 14));
        gc.setTextAlign(TextAlignment.LEFT);
        String header = isCircular
                ? "Custom Circular Queue (Fixed Array Capacity = " + cap
                + " | Modular Wrap-Around: rear = (rear + 1) % " + cap + ")"
                : "Custom Linear Queue (Fixed Array Capacity = " + cap
                + " | Demonstrates 'False Full' when rear == " + (cap - 1) + " & front > 0)";
        gc.fillText(header, 24, 30);

        double slotW = Math.min(100.0, Math.max(58.0, (w - 90) / cap));
        double slotH = 68.0;
        double totalW = cap * slotW;
        double startX = (w - totalW) / 2.0;
        double centerY = h / 2.0 - 25;

        for (int i = 0; i < cap; i++) {
            double x = startX + i * slotW;
            int val = (physical != null && i < physical.length) ? physical[i] : emptySentinel;
            boolean occupied = (val != emptySentinel);

            Color boxColor;
            if (ArrayUtils.contains(step.getSwapIndices(), i)) {
                boxColor = COLOR_SWAPPING;
            } else if (ArrayUtils.contains(step.getActiveIndices(), i)) {
                boxColor = COLOR_COMPARING;
            } else if (occupied) {
                boxColor = COLOR_SORTED;
            } else {
                boxColor = Color.web("#1E293B");
            }

            gc.setFill(boxColor);
            gc.fillRoundRect(x + 3, centerY, slotW - 6, slotH, 8, 8);
            gc.setStroke(occupied ? Color.WHITE : Color.web("#475569"));
            gc.setLineWidth(1.5);
            gc.strokeRoundRect(x + 3, centerY, slotW - 6, slotH, 8, 8);

            gc.setTextAlign(TextAlignment.CENTER);
            if (occupied) {
                gc.setFill(Color.web("#0F172A"));
                gc.setFont(Font.font("System", FontWeight.BOLD, 15));
                gc.fillText(String.valueOf(val), x + slotW / 2.0, centerY + 40);
            } else {
                boolean wastedLinearSlot = !isCircular && ArrayUtils.contains(step.getActiveIndices(), i);
                gc.setFill(wastedLinearSlot ? Color.web("#0F172A") : Color.web("#64748B"));
                gc.setFont(Font.font("Consolas", FontWeight.BOLD, 11));
                gc.fillText(wastedLinearSlot ? "WASTED" : "(empty)", x + slotW / 2.0, centerY + 38);
            }

            gc.setFill(COLOR_SUBTEXT);
            gc.setFont(Font.font("Consolas", FontWeight.NORMAL, 11.5));
            gc.fillText("idx [" + i + "]", x + slotW / 2.0, centerY + slotH + 18);

            StringBuilder badge = new StringBuilder();
            if (step.getPointerA() == i && !step.getPointerALabel().isEmpty()) {
                badge.append(step.getPointerALabel());
            }
            if (step.getPointerB() == i && !step.getPointerBLabel().isEmpty()) {
                if (badge.length() > 0) badge.append(" / ");
                badge.append(step.getPointerBLabel());
            }
            if (badge.length() > 0) {
                drawPointerBadge(gc, x + slotW / 2.0, centerY - 12, badge.toString());
            }
        }

        // Explanatory note at bottom of queue canvas
        gc.setFill(Color.web("#1E293B"));
        gc.fillRoundRect(30, h - 62, w - 60, 44, 8, 8);
        gc.setFill(Color.web("#38BDF8"));
        gc.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12.5));
        gc.setTextAlign(TextAlignment.LEFT);
        String note = isCircular
                ? "Circular Queue Solution: When rear reaches index " + (cap - 1)
                + ", the next enqueue wraps around to index (rear + 1) % " + cap + " = 0, reusing empty front slots!"
                : "Linear Queue 'False Full' Problem: Dequeuing advances front++, leaving empty slots at 0..front-1 that can NEVER be reused once rear reaches " + (cap - 1) + "!";
        gc.fillText(note, 44, h - 35);
    }

    private void renderBinarySearchTree(GraphicsContext gc, double w, double h, AlgorithmStep step) {
        TreeNodeSnapshot root = step.getTreeRootSnapshot();
        int[] traversalArr = step.getArraySnapshot();

        gc.setFill(COLOR_TEXT);
        gc.setFont(Font.font("System", FontWeight.BOLD, 14));
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText("Custom Binary Search Tree (Recursive TreeNode Hierarchy)", 24, 28);

        if (root == null) {
            drawEmptyMessage(gc, w, h, "BST is currently empty (root == NULL).");
            return;
        }

        int maxDepth = computeTreeDepth(root);
        double topY = 68;
        double treeAreaH = h - 135;
        double levelGap = Math.max(48.0, Math.min(78.0, treeAreaH / Math.max(1, maxDepth)));

        drawTreeNodeRecursive(gc, root, w / 2.0, topY, w / 4.2, levelGap, true);

        if (traversalArr != null && traversalArr.length > 0) {
            double stripY = h - 32;
            gc.setFill(Color.web("#1E293B"));
            gc.fillRoundRect(20, stripY - 22, w - 40, 32, 8, 8);
            gc.setFill(COLOR_SORTED);
            gc.setFont(Font.font("Consolas", FontWeight.BOLD, 13));
            gc.setTextAlign(TextAlignment.LEFT);
            gc.fillText("Traversal / Sorted Output: " + ArrayUtils.arrayToString(traversalArr), 34, stripY - 2);
        }
    }

    private int computeTreeDepth(TreeNodeSnapshot node) {
        if (node == null) return 0;
        int l = computeTreeDepth(node.getLeft());
        int r = computeTreeDepth(node.getRight());
        return 1 + (l > r ? l : r);
    }

    private void drawTreeNodeRecursive(
            GraphicsContext gc,
            TreeNodeSnapshot node,
            double x,
            double y,
            double hOffset,
            double levelGap,
            boolean isRoot) {
        if (node == null) return;
        double nextOffset = Math.max(26.0, hOffset * 0.52);
        double childY = y + levelGap;

        gc.setStroke(Color.web("#64748B"));
        gc.setLineWidth(2.0);
        if (node.getLeft() != null) {
            double leftX = x - hOffset;
            gc.strokeLine(x, y, leftX, childY);
            drawTreeNodeRecursive(gc, node.getLeft(), leftX, childY, nextOffset, levelGap, false);
        }
        if (node.getRight() != null) {
            double rightX = x + hOffset;
            gc.strokeLine(x, y, rightX, childY);
            drawTreeNodeRecursive(gc, node.getRight(), rightX, childY, nextOffset, levelGap, false);
        }

        Color nodeColor = COLOR_DEFAULT;
        if (node.getHighlightState() == TreeNodeSnapshot.STATE_COMPARING) {
            nodeColor = COLOR_COMPARING;
        } else if (node.getHighlightState() == TreeNodeSnapshot.STATE_SWAPPING_OR_MODIFIED) {
            nodeColor = COLOR_SWAPPING;
        } else if (node.getHighlightState() == TreeNodeSnapshot.STATE_VISITED_OR_FOUND) {
            nodeColor = COLOR_SORTED;
        }

        double radius = 20.0;
        gc.setFill(nodeColor);
        gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2.0);
        gc.strokeOval(x - radius, y - radius, radius * 2, radius * 2);

        gc.setFill(Color.web("#0F172A"));
        gc.setFont(Font.font("System", FontWeight.BOLD, 13));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(String.valueOf(node.getValue()), x, y + 5);

        if (isRoot) {
            drawPointerBadge(gc, x, y - radius - 10, "ROOT");
        }
    }

    private void renderTowerOfHanoi(GraphicsContext gc, double w, double h, AlgorithmStep step) {
        int[][] pegs = step.getPegsSnapshot();
        if (pegs == null || pegs.length < 3) {
            drawEmptyMessage(gc, w, h, "Tower of Hanoi pegs not initialized.");
            return;
        }

        int maxDisk = 1;
        for (int p = 0; p < 3; p++) {
            if (pegs[p] != null) {
                for (int i = 0; i < pegs[p].length; i++) {
                    if (pegs[p][i] > maxDisk) {
                        maxDisk = pegs[p][i];
                    }
                }
            }
        }
        int expectedTotal = (1 << maxDisk) - 1;

        gc.setFill(COLOR_TEXT);
        gc.setFont(Font.font("System", FontWeight.BOLD, 14));
        gc.setTextAlign(TextAlignment.LEFT);
        String moveBanner = step.getMovingDisk() > 0
                ? "Move Counter: " + step.getOperationsCount() + " / " + expectedTotal
                + " (2^" + maxDisk + " - 1 = " + expectedTotal + ") | Active Move: Disk "
                + step.getMovingDisk() + " from Peg " + (char) ('A' + step.getFromPeg())
                + " ──► Peg " + (char) ('A' + step.getToPeg())
                : "Running Move Counter: " + step.getOperationsCount() + " / " + expectedTotal
                + " (Formula: 2^" + maxDisk + " - 1 = " + expectedTotal + " total moves)";
        gc.fillText(moveBanner, 24, 30);

        double baseY = h - 65;
        double topY = 95;
        double pegHeight = baseY - topY;
        double sectionW = (w - 80) / 3.0;

        gc.setFill(Color.web("#334155"));
        gc.fillRoundRect(40, baseY, w - 80, 14, 8, 8);

        String[] labels = new String[]{"Peg A", "Peg B", "Peg C"};
        double maxDiskW = Math.min(180.0, sectionW * 0.82);
        double minDiskW = 46.0;
        double diskH = Math.min(28.0, (pegHeight - 25.0) / Math.max(3, maxDisk));

        for (int p = 0; p < 3; p++) {
            double pegCenterX = 40 + sectionW * (p + 0.5);

            gc.setFill(Color.web("#64748B"));
            gc.fillRoundRect(pegCenterX - 5, topY, 10, pegHeight, 5, 5);

            gc.setFill(COLOR_TEXT);
            gc.setFont(Font.font("System", FontWeight.BOLD, 13));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText(labels[p], pegCenterX, baseY + 34);

            int[] pegDisks = pegs[p];
            if (pegDisks != null) {
                for (int level = 0; level < pegDisks.length; level++) {
                    int diskSize = pegDisks[level];
                    if (diskSize <= 0) continue;
                    double ratio = maxDisk <= 1 ? 1.0 : (double) (diskSize - 1) / (double) (maxDisk - 1);
                    double diskW = minDiskW + ratio * (maxDiskW - minDiskW);
                    double diskX = pegCenterX - diskW / 2.0;
                    double diskY = baseY - (level + 1) * diskH;

                    Color diskColor;
                    if (diskSize == step.getMovingDisk() && p == step.getToPeg()) {
                        diskColor = COLOR_COMPARING;
                    } else if (p == step.getToPeg()) {
                        diskColor = COLOR_SORTED;
                    } else {
                        diskColor = COLOR_DEFAULT;
                    }

                    gc.setFill(diskColor);
                    gc.fillRoundRect(diskX, diskY + 1, diskW, diskH - 2, 8, 8);
                    gc.setStroke(Color.WHITE);
                    gc.setLineWidth(1.3);
                    gc.strokeRoundRect(diskX, diskY + 1, diskW, diskH - 2, 8, 8);

                    gc.setFill(Color.web("#0F172A"));
                    gc.setFont(Font.font("System", FontWeight.BOLD, 12));
                    gc.setTextAlign(TextAlignment.CENTER);
                    gc.fillText("Disk " + diskSize, pegCenterX, diskY + diskH / 2.0 + 4);
                }
            }
        }
    }

    /**
     * Renders a split view with a comparison table at the top and array bars at the bottom.
     */
    private void renderTableAndBarsView(GraphicsContext gc, double w, double h, AlgorithmStep step, String title) {
        gc.setFill(COLOR_TEXT);
        gc.setFont(Font.font("System", FontWeight.BOLD, 14));
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText(title, 20, 26);

        double tableEndY = drawComparisonTable(gc, 20, 38, w - 40, tableHeaders, tableRows);

        if (step != null && step.getArraySnapshot() != null && step.getArraySnapshot().length > 0 && h - tableEndY > 110) {
            int[] arr = step.getArraySnapshot();
            int n = arr.length;
            int maxAbs = 1;
            for (int i = 0; i < n; i++) {
                int abs = Math.abs(arr[i]);
                if (abs > maxAbs) maxAbs = abs;
            }
            double topY = tableEndY + 28;
            double bottomY = h - 32;
            double availH = Math.max(40, bottomY - topY);
            double availW = w - 80;
            double slotW = availW / n;
            double barW = Math.max(4.0, slotW * 0.8);

            for (int i = 0; i < n; i++) {
                double barH = Math.max(12.0, ((double) Math.abs(arr[i]) / maxAbs) * (availH - 18));
                double x = 40 + i * slotW;
                double y = bottomY - barH;
                Color c = resolveIndexColor(i, step);
                gc.setFill(c);
                gc.fillRoundRect(x, y, barW, barH, 4, 4);
                gc.setFill(COLOR_TEXT);
                gc.setFont(Font.font("Consolas", FontWeight.BOLD, n > 20 ? 9 : 11));
                gc.setTextAlign(TextAlignment.CENTER);
                gc.fillText(String.valueOf(arr[i]), x + barW / 2.0, y - 4);
            }
        }
    }

    /**
     * Renders Module D Experimental Time Complexity Benchmark Table AND two side-by-side line charts:
     * Chart 1: O(n^2) Sorts (Bubble, Selection, Insertion)
     * Chart 2: O(n log n) Sorts (Merge Sort, QuickSort First-Pivot, QuickSort Random-Pivot)
     */
    private void renderBenchmarkPlotsAndTable(GraphicsContext gc, double w, double h) {
        gc.setFill(COLOR_TEXT);
        gc.setFont(Font.font("System", FontWeight.BOLD, 14));
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText("Module D: Experimental Time Complexity Analysis (Average of 3 Runs per Input Size n)", 20, 25);

        double tableEndY = drawComparisonTable(gc, 20, 36, w - 40, tableHeaders, tableRows);

        double plotTop = tableEndY + 18;
        double plotH = h - plotTop - 16;
        if (plotH < 95 || tableRows == null || tableRows.length < 2) {
            return;
        }

        double chartW = (w - 60) / 2.0;
        drawGrowthChart(gc, 20, plotTop, chartW, plotH,
                "Chart 1: O(n^2) Sorts Running Time vs n (ms)",
                new int[]{1, 2, 3},
                new String[]{"Bubble", "Selection", "Insertion"},
                new Color[]{Color.web("#EF4444"), Color.web("#F59E0B"), Color.web("#10B981")},
                6);

        drawGrowthChart(gc, 40 + chartW, plotTop, chartW, plotH,
                "Chart 2: O(n log n) Sorts Running Time vs n (ms)",
                new int[]{4, 5, 6},
                new String[]{"Merge", "QS (1st)", "QS (Rand)"},
                new Color[]{Color.web("#38BDF8"), Color.web("#A855F7"), Color.web("#FACC15")},
                tableRows.length);
    }

    private void drawGrowthChart(
            GraphicsContext gc,
            double x,
            double y,
            double w,
            double h,
            String title,
            int[] colIndices,
            String[] seriesNames,
            Color[] colors,
            int maxRowsToPlot) {
        gc.setFill(Color.web("#1E293B"));
        gc.fillRoundRect(x, y, w, h, 8, 8);
        gc.setStroke(Color.web("#334155"));
        gc.strokeRoundRect(x, y, w, h, 8, 8);

        gc.setFill(COLOR_TEXT);
        gc.setFont(Font.font("System", FontWeight.BOLD, 11.5));
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText(title, x + 12, y + 18);

        // Legend
        for (int s = 0; s < seriesNames.length; s++) {
            double lx = x + w - (seriesNames.length - s) * 82;
            gc.setFill(colors[s]);
            gc.fillOval(lx, y + 9, 8, 8);
            gc.setFill(COLOR_SUBTEXT);
            gc.setFont(Font.font("System", FontWeight.SEMI_BOLD, 10));
            gc.fillText(seriesNames[s], lx + 12, y + 17);
        }

        int rowsCount = Math.min(maxRowsToPlot, tableRows.length);
        double maxVal = 0.001;
        for (int r = 0; r < rowsCount; r++) {
            for (int c = 0; c < colIndices.length; c++) {
                double v = parseDoubleSafe(tableRows[r][colIndices[c]]);
                if (v > maxVal) maxVal = v;
            }
        }

        double padL = 44;
        double padR = 18;
        double padT = 28;
        double padB = 22;
        double plotW = w - padL - padR;
        double plotH = h - padT - padB;
        double originX = x + padL;
        double originY = y + padT + plotH;

        gc.setStroke(Color.web("#475569"));
        gc.setLineWidth(1.2);
        gc.strokeLine(originX, y + padT, originX, originY);
        gc.strokeLine(originX, originY, originX + plotW, originY);

        for (int s = 0; s < colIndices.length; s++) {
            gc.setStroke(colors[s]);
            gc.setFill(colors[s]);
            gc.setLineWidth(2.0);
            double prevX = -1;
            double prevY = -1;
            for (int r = 0; r < rowsCount; r++) {
                double val = parseDoubleSafe(tableRows[r][colIndices[s]]);
                if (val < 0) continue;
                double px = originX + (rowsCount <= 1 ? 0 : (r * plotW / (rowsCount - 1)));
                double py = originY - (val / maxVal) * (plotH - 6);
                gc.fillOval(px - 3, py - 3, 6, 6);
                if (prevX >= 0) {
                    gc.strokeLine(prevX, prevY, px, py);
                }
                prevX = px;
                prevY = py;
            }
        }
    }

    private double parseDoubleSafe(String s) {
        if (s == null) return -1;
        try {
            return Double.parseDouble(s.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private double drawComparisonTable(
            GraphicsContext gc,
            double startX,
            double startY,
            double tableW,
            String[] headers,
            String[][] rows) {
        if (headers == null || rows == null) {
            return startY;
        }
        int cols = headers.length;
        double colW = tableW / cols;
        double rowH = 24.0;

        // Header row
        gc.setFill(Color.web("#1E3A8A"));
        gc.fillRoundRect(startX, startY, tableW, rowH, 6, 6);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("System", FontWeight.BOLD, 11.5));
        gc.setTextAlign(TextAlignment.LEFT);
        for (int c = 0; c < cols; c++) {
            gc.fillText(headers[c], startX + c * colW + 8, startY + 16);
        }

        double y = startY + rowH;
        for (int r = 0; r < rows.length; r++) {
            gc.setFill(r % 2 == 0 ? Color.web("#1E293B") : Color.web("#0F172A"));
            gc.fillRect(startX, y, tableW, rowH);
            gc.setStroke(Color.web("#334155"));
            gc.setLineWidth(0.6);
            gc.strokeRect(startX, y, tableW, rowH);

            gc.setFill(COLOR_TEXT);
            gc.setFont(Font.font("Consolas", FontWeight.NORMAL, 11));
            for (int c = 0; c < cols && c < rows[r].length; c++) {
                String cell = rows[r][c] != null ? rows[r][c] : "";
                int maxChars = Math.max(12, (int) (colW / 6.5));
                if (cell.length() > maxChars) {
                    cell = cell.substring(0, maxChars - 2) + "..";
                }
                gc.fillText(cell, startX + c * colW + 8, y + 16);
            }
            y += rowH;
        }
        return y;
    }

    private Color resolveIndexColor(int index, AlgorithmStep step) {
        if (ArrayUtils.contains(step.getSwapIndices(), index)) {
            return COLOR_SWAPPING;
        }
        if (ArrayUtils.contains(step.getActiveIndices(), index)) {
            return COLOR_COMPARING;
        }
        if (ArrayUtils.contains(step.getSortedIndices(), index)) {
            return COLOR_SORTED;
        }
        return COLOR_DEFAULT;
    }

    private void drawPointerBadge(GraphicsContext gc, double centerX, double centerY, String text) {
        double padX = 6;
        double approxW = Math.max(36, text.length() * 6.8 + padX * 2);
        double badgeH = 19;
        gc.setFill(COLOR_POINTER);
        gc.fillRoundRect(centerX - approxW / 2.0, centerY - badgeH + 4, approxW, badgeH, 6, 6);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 10.5));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(text, centerX, centerY);
    }

    private void drawHorizontalArrow(GraphicsContext gc, double x1, double y, double x2, Color color) {
        gc.setStroke(color);
        gc.setFill(color);
        gc.setLineWidth(2.0);
        gc.strokeLine(x1, y, x2, y);
        double[] xs = new double[]{x2, x2 - 7, x2 - 7};
        double[] ys = new double[]{y, y - 4.5, y + 4.5};
        gc.fillPolygon(xs, ys, 3);
    }

    private void drawLeftArrow(GraphicsContext gc, double x1, double y, double x2, Color color) {
        gc.setStroke(color);
        gc.setFill(color);
        gc.setLineWidth(2.0);
        gc.strokeLine(x1, y, x2, y);
        double[] xs = new double[]{x2, x2 + 7, x2 + 7};
        double[] ys = new double[]{y, y - 4.5, y + 4.5};
        gc.fillPolygon(xs, ys, 3);
    }
}
