package ui;

import algorithms.BubbleSort;
import algorithms.FactorialLab;
import algorithms.FibonacciLab;
import algorithms.InsertionSort;
import algorithms.MergeSort;
import algorithms.QuickSort;
import algorithms.RecursiveBinarySearch;
import algorithms.SelectionSort;
import algorithms.SortingComparisonLab;
import algorithms.TowerOfHanoi;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.AlgorithmCategory;
import model.AlgorithmStep;
import model.AlgorithmType;
import model.ArrayUtils;
import model.ComplexityInfo;
import model.ExecutionResult;
import structures.CustomBinarySearchTree;
import structures.CustomDoublyLinkedList;
import structures.CustomLinearQueue;
import structures.CustomQueue;
import structures.CustomSinglyLinkedList;
import structures.CustomStack;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Main JavaFX Desktop Application for CSC2107: Algorithm Explorer.
 * Integrates all four modules (Module A: Recursion Lab, Module B: Data Structures Lab,
 * Module C: Sorting Lab, Module D: Time Complexity Analysis) into one unified application.
 */
public class MainApp extends Application {

    // Persistent custom data structure instances for interactive Module B operations
    private final CustomSinglyLinkedList singlyLinkedList = new CustomSinglyLinkedList();
    private final CustomDoublyLinkedList doublyLinkedList = new CustomDoublyLinkedList();
    private final CustomStack customStack = new CustomStack(6);
    private final CustomLinearQueue linearQueue = new CustomLinearQueue(6);
    private final CustomQueue circularQueue = new CustomQueue(6);
    private final CustomBinarySearchTree binarySearchTree = new CustomBinarySearchTree();

    // Top selection controls
    private ComboBox<AlgorithmCategory> categoryCombo;
    private ComboBox<AlgorithmType> algorithmCombo;
    private FlowPane contextualActionPane;

    // Left panel input controls
    private Label manualInputLabel;
    private TextArea inputArrayArea;
    private Label targetFieldLabel;
    private TextField targetValueField;
    private Label referenceFieldLabel;
    private TextField referenceValueField;
    private ComboBox<String> hanoiSourceCombo;
    private ComboBox<String> hanoiDestCombo;
    private HBox hanoiPegBox;
    private Spinner<Integer> randomCountSpinner;
    private Label statusFileLabel;

    // Center visualization components
    private Label finalResultBanner;
    private VisualizationCanvas visualizationCanvas;
    private Label activeStepBanner;

    // Right panel metrics dashboard labels
    private TextArea finalResultDetailBox;
    private Label stepCounterLabel;
    private Label stepOpsLabel;
    private Label comparisonsLabel;
    private Label swapsMovesLabel;
    private Label totalOpsLabel;
    private Label execTimeNanosLabel;
    private Label execTimeMicrosLabel;
    private Label execTimeMillisLabel;
    private Label bestComplexityLabel;
    private Label avgComplexityLabel;
    private Label worstComplexityLabel;
    private Label spaceComplexityLabel;
    private Label stabilityLabel;
    private Label complexityNotesLabel;

    // Bottom playback & log controls
    private Button playPauseButton;
    private Slider stepScrubber;
    private Slider speedSlider;
    private Label speedValueLabel;
    private ListView<String> stepLogListView;

    // Playback state
    private ExecutionResult currentExecutionResult;
    private int currentStepIndex = 0;
    private Timeline playbackTimeline;
    private boolean isPlaying = false;
    private boolean suppressScrubberEvent = false;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("CSC2107 Algorithm Explorer — Recursion, Data Structures, Sorting & Complexity");

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0B1120;");

        root.setTop(buildTopBar());
        root.setLeft(buildLeftInputPanel(primaryStage));
        root.setCenter(buildCenterVisualizationPane());
        root.setRight(buildRightMetricsDashboard());
        root.setBottom(buildBottomPlaybackAndLogPane());

        setupPlaybackTimeline();
        onCategoryChanged();

        Scene scene = new Scene(root, 1420, 880);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1140);
        primaryStage.setMinHeight(740);
        primaryStage.show();

        executeCurrentAlgorithm("FULL_DEMO");
    }

    private VBox buildTopBar() {
        VBox topContainer = new VBox(8);
        topContainer.setPadding(new Insets(10, 16, 10, 16));
        topContainer.setStyle("-fx-background-color: #111827; -fx-border-color: #1E293B; -fx-border-width: 0 0 1 0;");

        HBox headerRow = new HBox(14);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        Label appTitle = new Label("⚡ CSC2107 Algorithm Explorer");
        appTitle.setFont(Font.font("System", FontWeight.BOLD, 17));
        appTitle.setTextFill(Color.web("#F8FAFC"));

        // Quick module tabs
        Button modABtn = createStyledButton("Module A: Recursion", "#1E293B", "#38BDF8");
        modABtn.setOnAction(e -> categoryCombo.getSelectionModel().select(AlgorithmCategory.MODULE_A_RECURSION));
        Button modBBtn = createStyledButton("Module B: Structures", "#1E293B", "#38BDF8");
        modBBtn.setOnAction(e -> categoryCombo.getSelectionModel().select(AlgorithmCategory.MODULE_B_STRUCTURES));
        Button modCBtn = createStyledButton("Module C: Sorting", "#1E293B", "#38BDF8");
        modCBtn.setOnAction(e -> categoryCombo.getSelectionModel().select(AlgorithmCategory.MODULE_C_SORTING));
        Button modDBtn = createStyledButton("Module D: Complexity", "#1E293B", "#38BDF8");
        modDBtn.setOnAction(e -> categoryCombo.getSelectionModel().select(AlgorithmCategory.MODULE_D_COMPLEXITY));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label catLabel = new Label("Module:");
        catLabel.setTextFill(Color.web("#CBD5E1"));
        catLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12.5));

        categoryCombo = new ComboBox<>();
        AlgorithmCategory[] categories = AlgorithmCategory.values();
        for (int i = 0; i < categories.length; i++) {
            categoryCombo.getItems().add(categories[i]);
        }
        categoryCombo.getSelectionModel().select(AlgorithmCategory.MODULE_C_SORTING);
        categoryCombo.setPrefWidth(220);
        categoryCombo.setOnAction(e -> onCategoryChanged());

        Label algoLabel = new Label("Task:");
        algoLabel.setTextFill(Color.web("#CBD5E1"));
        algoLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12.5));

        algorithmCombo = new ComboBox<>();
        algorithmCombo.setPrefWidth(320);
        algorithmCombo.setOnAction(e -> onAlgorithmChanged());

        headerRow.getChildren().addAll(appTitle, modABtn, modBBtn, modCBtn, modDBtn, spacer, catLabel, categoryCombo, algoLabel, algorithmCombo);

        contextualActionPane = new FlowPane(7, 6);
        contextualActionPane.setAlignment(Pos.CENTER_LEFT);

        topContainer.getChildren().addAll(headerRow, contextualActionPane);
        return topContainer;
    }

    private VBox buildLeftInputPanel(Stage stage) {
        VBox leftPanel = new VBox(9);
        leftPanel.setPadding(new Insets(12));
        leftPanel.setPrefWidth(295);
        leftPanel.setStyle("-fx-background-color: #111827; -fx-border-color: #1E293B; -fx-border-width: 0 1 0 0;");

        Label sectionTitle = new Label("USER INPUT & VALIDATION");
        sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 12.5));
        sectionTitle.setTextFill(Color.web("#38BDF8"));

        manualInputLabel = new Label("Input Numbers (Comma-Separated) / Bracket Expr:");
        manualInputLabel.setTextFill(Color.web("#E2E8F0"));
        manualInputLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 11.5));

        inputArrayArea = new TextArea("64, 34, 25, 12, 22, 11, 90, 45, 18, 77");
        inputArrayArea.setWrapText(true);
        inputArrayArea.setPrefRowCount(3);
        inputArrayArea.setStyle("-fx-control-inner-background: #1E293B; -fx-text-fill: #F8FAFC; -fx-font-family: 'Consolas'; -fx-font-size: 12.5px;");

        targetFieldLabel = new Label("Value / Target / n / Capacity:");
        targetFieldLabel.setTextFill(Color.web("#E2E8F0"));
        targetFieldLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 11.5));

        targetValueField = new TextField("10");
        targetValueField.setStyle("-fx-background-color: #1E293B; -fx-text-fill: #F8FAFC; -fx-font-family: 'Consolas'; -fx-font-size: 12.5px;");

        referenceFieldLabel = new Label("Before / After Element (Linked Lists):");
        referenceFieldLabel.setTextFill(Color.web("#94A3B8"));
        referenceFieldLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 11.5));

        referenceValueField = new TextField("25");
        referenceValueField.setStyle("-fx-background-color: #1E293B; -fx-text-fill: #F8FAFC; -fx-font-family: 'Consolas'; -fx-font-size: 12.5px;");

        // Tower of Hanoi Source & Destination Peg selectors
        Label hanoiPegLbl = new Label("Hanoi Pegs (Source -> Dest):");
        hanoiPegLbl.setTextFill(Color.web("#94A3B8"));
        hanoiPegLbl.setFont(Font.font("System", FontWeight.SEMI_BOLD, 11));

        hanoiSourceCombo = new ComboBox<>();
        hanoiSourceCombo.getItems().addAll("Peg A", "Peg B", "Peg C");
        hanoiSourceCombo.getSelectionModel().select(0);
        hanoiSourceCombo.setPrefWidth(92);

        hanoiDestCombo = new ComboBox<>();
        hanoiDestCombo.getItems().addAll("Peg A", "Peg B", "Peg C");
        hanoiDestCombo.getSelectionModel().select(2);
        hanoiDestCombo.setPrefWidth(92);

        hanoiPegBox = new HBox(6, hanoiPegLbl, hanoiSourceCombo, hanoiDestCombo);
        hanoiPegBox.setAlignment(Pos.CENTER_LEFT);

        Separator sep1 = new Separator();

        Label randHeader = new Label("Random Data Generator:");
        randHeader.setTextFill(Color.web("#E2E8F0"));
        randHeader.setFont(Font.font("System", FontWeight.SEMI_BOLD, 11.5));

        HBox randRow = new HBox(8);
        randRow.setAlignment(Pos.CENTER_LEFT);
        Label sizeLbl = new Label("n:");
        sizeLbl.setTextFill(Color.web("#94A3B8"));
        randomCountSpinner = new Spinner<>(3, 60, 10);
        randomCountSpinner.setPrefWidth(78);
        randomCountSpinner.setEditable(true);

        Button generateRandBtn = createStyledButton("🎲 Generate Random", "#334155", "#F8FAFC");
        HBox.setHgrow(generateRandBtn, Priority.ALWAYS);
        generateRandBtn.setMaxWidth(Double.MAX_VALUE);
        generateRandBtn.setOnAction(e -> {
            int count = randomCountSpinner.getValue() != null ? randomCountSpinner.getValue() : 10;
            int[] randomData = ArrayUtils.generateRandomArray(count, 5, 99);
            inputArrayArea.setText(ArrayUtils.arrayToCsv(randomData));
            if (randomData.length > 0) {
                int midIdx = randomData.length / 2;
                targetValueField.setText(String.valueOf(randomData[midIdx]));
                referenceValueField.setText(String.valueOf(randomData[0]));
            }
            statusFileLabel.setText("Generated n = " + count + " random numbers.");
            executeCurrentAlgorithm("FULL_DEMO");
        });
        randRow.getChildren().addAll(sizeLbl, randomCountSpinner, generateRandBtn);

        Button uploadBtn = createStyledButton("📂 Upload .txt / .csv File", "#334155", "#F8FAFC");
        uploadBtn.setMaxWidth(Double.MAX_VALUE);
        uploadBtn.setOnAction(e -> handleFileUpload(stage));

        statusFileLabel = new Label("Ready. Input is validated before execution.");
        statusFileLabel.setTextFill(Color.web("#94A3B8"));
        statusFileLabel.setWrapText(true);
        statusFileLabel.setFont(Font.font("System", 11));

        Separator sep2 = new Separator();

        Button runBtn = createStyledButton("▶ Execute / Run Task", "#2563EB", "#FFFFFF");
        runBtn.setFont(Font.font("System", FontWeight.BOLD, 13.5));
        runBtn.setPrefHeight(38);
        runBtn.setMaxWidth(Double.MAX_VALUE);
        runBtn.setOnAction(e -> executeCurrentAlgorithm("FULL_DEMO"));

        Button resetStructBtn = createStyledButton("🔄 Reset All Structures", "#475569", "#F8FAFC");
        resetStructBtn.setMaxWidth(Double.MAX_VALUE);
        resetStructBtn.setOnAction(e -> {
            singlyLinkedList.clear();
            doublyLinkedList.clear();
            customStack.clear();
            linearQueue.clear();
            circularQueue.clear();
            binarySearchTree.clear();
            executeCurrentAlgorithm("FULL_DEMO");
        });

        leftPanel.getChildren().addAll(
                sectionTitle,
                manualInputLabel,
                inputArrayArea,
                targetFieldLabel,
                targetValueField,
                referenceFieldLabel,
                referenceValueField,
                hanoiPegBox,
                sep1,
                randHeader,
                randRow,
                uploadBtn,
                statusFileLabel,
                sep2,
                runBtn,
                resetStructBtn
        );
        return leftPanel;
    }

    private VBox buildCenterVisualizationPane() {
        VBox centerBox = new VBox(8);
        centerBox.setPadding(new Insets(10));
        centerBox.setStyle("-fx-background-color: #0B1120;");

        HBox resultBannerBox = new HBox(10);
        resultBannerBox.setAlignment(Pos.CENTER_LEFT);
        resultBannerBox.setPadding(new Insets(8, 12, 8, 12));
        resultBannerBox.setStyle("-fx-background-color: #1E293B; -fx-background-radius: 8; -fx-border-color: #334155; -fx-border-radius: 8;");

        Label resultTitle = new Label("FINAL RESULT:");
        resultTitle.setFont(Font.font("System", FontWeight.BOLD, 12));
        resultTitle.setTextFill(Color.web("#38BDF8"));

        finalResultBanner = new Label("Ready...");
        finalResultBanner.setFont(Font.font("Consolas", FontWeight.BOLD, 12.5));
        finalResultBanner.setTextFill(Color.web("#F8FAFC"));
        finalResultBanner.setWrapText(true);
        HBox.setHgrow(finalResultBanner, Priority.ALWAYS);

        resultBannerBox.getChildren().addAll(resultTitle, finalResultBanner);

        HBox legendBar = new HBox(16);
        legendBar.setAlignment(Pos.CENTER_LEFT);
        legendBar.setPadding(new Insets(2, 8, 2, 8));
        legendBar.getChildren().addAll(
                createLegendItem("#3B82F6", "Blue: Default"),
                createLegendItem("#FACC15", "Yellow: Comparing / <= Pivot Zone / Wasted Slot"),
                createLegendItem("#EF4444", "Red: Swapping / Modified / Overflow"),
                createLegendItem("#10B981", "Green: Sorted / Found / > Pivot Zone"),
                createLegendItem("#A855F7", "Purple: Active Pointers (head/tail/top/front/rear/pivot)")
        );

        visualizationCanvas = new VisualizationCanvas();
        VBox.setVgrow(visualizationCanvas, Priority.ALWAYS);

        HBox stepDescBar = new HBox(10);
        stepDescBar.setAlignment(Pos.CENTER_LEFT);
        stepDescBar.setPadding(new Insets(8, 12, 8, 12));
        stepDescBar.setStyle("-fx-background-color: #111827; -fx-background-radius: 8; -fx-border-color: #1E293B; -fx-border-radius: 8;");

        Label stepBadge = new Label("STEP LOG:");
        stepBadge.setFont(Font.font("System", FontWeight.BOLD, 12));
        stepBadge.setTextFill(Color.web("#FACC15"));

        activeStepBanner = new Label("Select a module and click Execute.");
        activeStepBanner.setFont(Font.font("Consolas", FontWeight.NORMAL, 12.5));
        activeStepBanner.setTextFill(Color.web("#F8FAFC"));
        activeStepBanner.setWrapText(true);
        HBox.setHgrow(activeStepBanner, Priority.ALWAYS);

        stepDescBar.getChildren().addAll(stepBadge, activeStepBanner);

        centerBox.getChildren().addAll(resultBannerBox, legendBar, visualizationCanvas, stepDescBar);
        return centerBox;
    }

    private VBox buildRightMetricsDashboard() {
        VBox rightPanel = new VBox(10);
        rightPanel.setPadding(new Insets(12));
        rightPanel.setPrefWidth(330);
        rightPanel.setStyle("-fx-background-color: #111827; -fx-border-color: #1E293B; -fx-border-width: 0 0 0 1;");

        Label dashTitle = new Label("EMPIRICAL METRICS & BIG-O");
        dashTitle.setFont(Font.font("System", FontWeight.BOLD, 12.5));
        dashTitle.setTextFill(Color.web("#38BDF8"));

        VBox resultCard = new VBox(4);
        resultCard.setPadding(new Insets(8));
        resultCard.setStyle("-fx-background-color: #1E293B; -fx-background-radius: 8;");
        Label resHdr = new Label("1. Final Processed Result");
        resHdr.setFont(Font.font("System", FontWeight.BOLD, 11.5));
        resHdr.setTextFill(Color.web("#10B981"));

        finalResultDetailBox = new TextArea();
        finalResultDetailBox.setEditable(false);
        finalResultDetailBox.setWrapText(true);
        finalResultDetailBox.setPrefRowCount(3);
        finalResultDetailBox.setStyle("-fx-control-inner-background: #0F172A; -fx-text-fill: #F8FAFC; -fx-font-family: 'Consolas'; -fx-font-size: 11px;");
        resultCard.getChildren().addAll(resHdr, finalResultDetailBox);

        VBox metricsCard = new VBox(6);
        metricsCard.setPadding(new Insets(8));
        metricsCard.setStyle("-fx-background-color: #1E293B; -fx-background-radius: 8;");

        Label empHdr = new Label("2. Empirical Operation Counts & Timer");
        empHdr.setFont(Font.font("System", FontWeight.BOLD, 11.5));
        empHdr.setTextFill(Color.web("#FACC15"));

        GridPane metricsGrid = new GridPane();
        metricsGrid.setHgap(10);
        metricsGrid.setVgap(4);

        stepCounterLabel = createMetricValueLabel("0 / 0");
        stepOpsLabel = createMetricValueLabel("0");
        comparisonsLabel = createMetricValueLabel("0");
        swapsMovesLabel = createMetricValueLabel("0");
        totalOpsLabel = createMetricValueLabel("0");
        execTimeNanosLabel = createMetricValueLabel("0 ns");
        execTimeMicrosLabel = createMetricValueLabel("0.000 µs");
        execTimeMillisLabel = createMetricValueLabel("0.0000 ms");

        addMetricRow(metricsGrid, 0, "Active Step:", stepCounterLabel);
        addMetricRow(metricsGrid, 1, "Step Operations:", stepOpsLabel);
        addMetricRow(metricsGrid, 2, "Comparisons / Calls:", comparisonsLabel);
        addMetricRow(metricsGrid, 3, "Swaps / Moves / Loops:", swapsMovesLabel);
        addMetricRow(metricsGrid, 4, "Total Operations:", totalOpsLabel);
        addMetricRow(metricsGrid, 5, "Time (ns):", execTimeNanosLabel);
        addMetricRow(metricsGrid, 6, "Time (µs):", execTimeMicrosLabel);
        addMetricRow(metricsGrid, 7, "Time (ms):", execTimeMillisLabel);

        metricsCard.getChildren().addAll(empHdr, metricsGrid);

        VBox complexityCard = new VBox(6);
        complexityCard.setPadding(new Insets(8));
        complexityCard.setStyle("-fx-background-color: #1E293B; -fx-background-radius: 8;");
        VBox.setVgrow(complexityCard, Priority.ALWAYS);

        Label compHdr = new Label("3. Theoretical Big-O & Stability");
        compHdr.setFont(Font.font("System", FontWeight.BOLD, 11.5));
        compHdr.setTextFill(Color.web("#A855F7"));

        GridPane compGrid = new GridPane();
        compGrid.setHgap(10);
        compGrid.setVgap(4);

        bestComplexityLabel = createMetricValueLabel("-");
        avgComplexityLabel = createMetricValueLabel("-");
        worstComplexityLabel = createMetricValueLabel("-");
        spaceComplexityLabel = createMetricValueLabel("-");
        stabilityLabel = createMetricValueLabel("-");

        addMetricRow(compGrid, 0, "Best Time:", bestComplexityLabel);
        addMetricRow(compGrid, 1, "Average Time:", avgComplexityLabel);
        addMetricRow(compGrid, 2, "Worst Time:", worstComplexityLabel);
        addMetricRow(compGrid, 3, "Extra Space:", spaceComplexityLabel);
        addMetricRow(compGrid, 4, "Stability:", stabilityLabel);

        Label notesHdr = new Label("Complexity & Design Notes:");
        notesHdr.setTextFill(Color.web("#94A3B8"));
        notesHdr.setFont(Font.font("System", FontWeight.SEMI_BOLD, 11));

        complexityNotesLabel = new Label("-");
        complexityNotesLabel.setTextFill(Color.web("#CBD5E1"));
        complexityNotesLabel.setWrapText(true);
        complexityNotesLabel.setFont(Font.font("System", 11));

        complexityCard.getChildren().addAll(compHdr, compGrid, new Separator(), notesHdr, complexityNotesLabel);

        rightPanel.getChildren().addAll(dashTitle, resultCard, metricsCard, complexityCard);
        return rightPanel;
    }

    private VBox buildBottomPlaybackAndLogPane() {
        VBox bottomBox = new VBox(6);
        bottomBox.setPadding(new Insets(8, 14, 10, 14));
        bottomBox.setPrefHeight(195);
        bottomBox.setStyle("-fx-background-color: #111827; -fx-border-color: #1E293B; -fx-border-width: 1 0 0 0;");

        HBox controlsRow = new HBox(10);
        controlsRow.setAlignment(Pos.CENTER_LEFT);

        Button resetStepBtn = createStyledButton("⏮ Reset", "#334155", "#F8FAFC");
        resetStepBtn.setOnAction(e -> {
            pausePlayback();
            goToStep(0);
        });

        Button prevStepBtn = createStyledButton("⏪ Step Prev", "#334155", "#F8FAFC");
        prevStepBtn.setOnAction(e -> {
            pausePlayback();
            if (currentStepIndex > 0) {
                goToStep(currentStepIndex - 1);
            }
        });

        playPauseButton = createStyledButton("▶ Auto-Play", "#10B981", "#0F172A");
        playPauseButton.setPrefWidth(105);
        playPauseButton.setOnAction(e -> togglePlayPause());

        Button nextStepBtn = createStyledButton("Step Next ⏩", "#334155", "#F8FAFC");
        nextStepBtn.setOnAction(e -> {
            pausePlayback();
            if (currentExecutionResult != null && currentStepIndex < currentExecutionResult.getSteps().length - 1) {
                goToStep(currentStepIndex + 1);
            }
        });

        Button endStepBtn = createStyledButton("End ⏭", "#334155", "#F8FAFC");
        endStepBtn.setOnAction(e -> {
            pausePlayback();
            if (currentExecutionResult != null && currentExecutionResult.getSteps().length > 0) {
                goToStep(currentExecutionResult.getSteps().length - 1);
            }
        });

        Label scrubLbl = new Label("Step:");
        scrubLbl.setTextFill(Color.web("#CBD5E1"));

        stepScrubber = new Slider(0, 1, 0);
        stepScrubber.setPrefWidth(210);
        HBox.setHgrow(stepScrubber, Priority.ALWAYS);
        stepScrubber.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!suppressScrubberEvent && currentExecutionResult != null) {
                pausePlayback();
                goToStep(newVal.intValue());
            }
        });

        Label speedLbl = new Label("Speed:");
        speedLbl.setTextFill(Color.web("#CBD5E1"));

        speedSlider = new Slider(0.25, 8.0, 1.5);
        speedSlider.setPrefWidth(130);
        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double spd = newVal.doubleValue();
            speedValueLabel.setText(String.format("%.2fx", spd));
            if (playbackTimeline != null) {
                playbackTimeline.setRate(spd);
            }
        });

        speedValueLabel = new Label("1.50x");
        speedValueLabel.setTextFill(Color.web("#38BDF8"));
        speedValueLabel.setFont(Font.font("Consolas", FontWeight.BOLD, 12));

        controlsRow.getChildren().addAll(
                resetStepBtn, prevStepBtn, playPauseButton, nextStepBtn, endStepBtn,
                scrubLbl, stepScrubber, speedLbl, speedSlider, speedValueLabel
        );

        stepLogListView = new ListView<>();
        stepLogListView.setStyle("-fx-control-inner-background: #0F172A; -fx-font-family: 'Consolas'; -fx-font-size: 11.5px;");
        VBox.setVgrow(stepLogListView, Priority.ALWAYS);
        stepLogListView.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            int idx = newVal.intValue();
            if (idx >= 0 && idx != currentStepIndex && currentExecutionResult != null && idx < currentExecutionResult.getSteps().length) {
                pausePlayback();
                goToStep(idx);
            }
        });

        bottomBox.getChildren().addAll(controlsRow, stepLogListView);
        return bottomBox;
    }

    private void setupPlaybackTimeline() {
        playbackTimeline = new Timeline(new KeyFrame(Duration.millis(550), e -> {
            if (currentExecutionResult == null) {
                pausePlayback();
                return;
            }
            AlgorithmStep[] steps = currentExecutionResult.getSteps();
            if (currentStepIndex < steps.length - 1) {
                goToStep(currentStepIndex + 1);
            } else {
                pausePlayback();
            }
        }));
        playbackTimeline.setCycleCount(Timeline.INDEFINITE);
        playbackTimeline.setRate(speedSlider != null ? speedSlider.getValue() : 1.5);
    }

    private void togglePlayPause() {
        if (isPlaying) {
            pausePlayback();
        } else {
            if (currentExecutionResult == null || currentExecutionResult.getSteps().length == 0) {
                return;
            }
            if (currentStepIndex >= currentExecutionResult.getSteps().length - 1) {
                goToStep(0);
            }
            isPlaying = true;
            playPauseButton.setText("⏸ Pause");
            playPauseButton.setStyle("-fx-background-color: #F59E0B; -fx-text-fill: #0F172A; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand;");
            playbackTimeline.play();
        }
    }

    private void pausePlayback() {
        isPlaying = false;
        if (playbackTimeline != null) {
            playbackTimeline.pause();
        }
        if (playPauseButton != null) {
            playPauseButton.setText("▶ Auto-Play");
            playPauseButton.setStyle("-fx-background-color: #10B981; -fx-text-fill: #0F172A; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand;");
        }
    }

    private void onCategoryChanged() {
        AlgorithmCategory selectedCategory = categoryCombo.getSelectionModel().getSelectedItem();
        if (selectedCategory == null) return;
        algorithmCombo.getItems().clear();
        AlgorithmType[] types = AlgorithmType.getByCategory(selectedCategory);
        for (int i = 0; i < types.length; i++) {
            algorithmCombo.getItems().add(types[i]);
        }
        if (types.length > 0) {
            algorithmCombo.getSelectionModel().select(0);
        }
    }

    private void onAlgorithmChanged() {
        AlgorithmType selected = algorithmCombo.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        pausePlayback();
        rebuildContextualActionButtons(selected);
        updateComplexityPanel(selected.getComplexityInfo());

        if (selected == AlgorithmType.FIBONACCI_THREE_WAYS) {
            targetFieldLabel.setText("Fibonacci n (0 to 92; Naive <= 30):");
            targetValueField.setText("10");
        } else if (selected == AlgorithmType.FACTORIAL_TWO_WAYS) {
            targetFieldLabel.setText("Factorial n! (0 to 20; rejects < 0):");
            targetValueField.setText("6");
        } else if (selected == AlgorithmType.TOWER_OF_HANOI) {
            targetFieldLabel.setText("Number of Disks n (1 to 8):");
            targetValueField.setText("4");
        } else if (selected == AlgorithmType.STACK_BRACKET_CHECKER) {
            manualInputLabel.setText("Bracket Expression to Validate:");
            if (!inputArrayArea.getText().contains("(") && !inputArrayArea.getText().contains("{") && !inputArrayArea.getText().contains("[")) {
                inputArrayArea.setText("{ ( [ ] ) }");
            }
        } else {
            manualInputLabel.setText("Input Numbers (Comma-Separated):");
            if (inputArrayArea.getText().contains("{") || inputArrayArea.getText().contains("(")) {
                inputArrayArea.setText("64, 34, 25, 12, 22, 11, 90, 45");
            }
            targetFieldLabel.setText("Operation Value / Search Key / Capacity:");
        }

        if (visualizationCanvas != null) {
            executeCurrentAlgorithm("FULL_DEMO");
        }
    }

    private void rebuildContextualActionButtons(AlgorithmType type) {
        contextualActionPane.getChildren().clear();

        Label quickLabel = new Label("Controls:");
        quickLabel.setTextFill(Color.web("#94A3B8"));
        quickLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 11.5));
        contextualActionPane.getChildren().add(quickLabel);

        if (type == AlgorithmType.FIBONACCI_THREE_WAYS) {
            contextualActionPane.getChildren().addAll(
                    createActionChip("▶ Compute F(n) (All 3 Ways)", "FULL_DEMO"),
                    createPresetNChip("Test F(10)", "10"),
                    createPresetNChip("Test F(25)", "25"),
                    createPresetNChip("Test F(30) [Max Naive]", "30"),
                    createPresetNChip("Test F(45) [Shows Why Naive Fails]", "45")
            );
        } else if (type == AlgorithmType.FACTORIAL_TWO_WAYS) {
            contextualActionPane.getChildren().addAll(
                    createActionChip("▶ Compute n! (Recursive & Iterative)", "FULL_DEMO"),
                    createPresetNChip("Test 0! = 1", "0"),
                    createPresetNChip("Test 6! = 720", "6"),
                    createPresetNChip("Test 12!", "12"),
                    createPresetNChip("Test -5 (Negative Validation)", "-5")
            );
        } else if (type == AlgorithmType.TOWER_OF_HANOI) {
            contextualActionPane.getChildren().addAll(
                    createActionChip("▶ Solve Hanoi (Selected Pegs)", "FULL_DEMO"),
                    createPresetNChip("3 Disks (7 Moves)", "3"),
                    createPresetNChip("4 Disks (15 Moves)", "4"),
                    createPresetNChip("5 Disks (31 Moves)", "5")
            );
        } else if (type == AlgorithmType.CUSTOM_STACK) {
            contextualActionPane.getChildren().addAll(
                    createActionChip("Demo Stack", "FULL_DEMO"),
                    createActionChip("⬆ Push(Val)", "PUSH"),
                    createActionChip("⬇ Pop()", "POP"),
                    createActionChip("👁 Peek()", "PEEK"),
                    createActionChip("ℹ size / is_empty / is_full", "STATUS"),
                    createActionChip("⚙ Set Capacity(Val)", "SET_CAPACITY")
            );
        } else if (type == AlgorithmType.STACK_BRACKET_CHECKER) {
            contextualActionPane.getChildren().addAll(
                    createActionChip("▶ Check Expression", "FULL_DEMO"),
                    createBracketPresetChip("Balanced: { ( [ ] ) }", "{ ( [ ] ) }"),
                    createBracketPresetChip("Balanced: [()]{}{[()()]()}", "[()]{}{[()()]()}"),
                    createBracketPresetChip("Unbalanced: { ( [ ) ] }", "{ ( [ ) ] }"),
                    createBracketPresetChip("Unbalanced: ( ( [ ] )", "( ( [ ] )")
            );
        } else if (type == AlgorithmType.LINEAR_QUEUE) {
            contextualActionPane.getChildren().addAll(
                    createActionChip("⚠ Demonstrate 'False Full'", "FULL_DEMO"),
                    createActionChip("➡ Enqueue(Val)", "ENQUEUE"),
                    createActionChip("⬅ Dequeue()", "DEQUEUE"),
                    createActionChip("👁 Front()", "FRONT"),
                    createActionChip("ℹ size / is_empty / is_full", "STATUS"),
                    createActionChip("⚙ Set Capacity(Val)", "SET_CAPACITY")
            );
        } else if (type == AlgorithmType.CUSTOM_QUEUE) {
            contextualActionPane.getChildren().addAll(
                    createActionChip("🔄 Demo Circular Wrap-Around", "FULL_DEMO"),
                    createActionChip("➡ Enqueue(Val)", "ENQUEUE"),
                    createActionChip("⬅ Dequeue()", "DEQUEUE"),
                    createActionChip("👁 Front()", "PEEK"),
                    createActionChip("ℹ size / is_empty / is_full", "STATUS"),
                    createActionChip("⚙ Set Capacity(Val)", "SET_CAPACITY")
            );
        } else if (type == AlgorithmType.SINGLY_LINKED_LIST) {
            contextualActionPane.getChildren().addAll(
                    createActionChip("Build List", "FULL_DEMO"),
                    createActionChip("+ Insert Begin", "INSERT_HEAD"),
                    createActionChip("+ Insert End", "INSERT_TAIL"),
                    createActionChip("+ Insert Before", "INSERT_BEFORE"),
                    createActionChip("+ Insert After", "INSERT_AFTER"),
                    createActionChip("- Delete Begin", "DELETE_HEAD"),
                    createActionChip("- Delete End", "DELETE_TAIL"),
                    createActionChip("- Delete Before", "DELETE_BEFORE"),
                    createActionChip("- Delete After", "DELETE_AFTER"),
                    createActionChip("- Delete Value", "DELETE_VALUE"),
                    createActionChip("🔍 Search", "SEARCH"),
                    createActionChip("➔ Display Forward", "TRAVERSE")
            );
        } else if (type == AlgorithmType.DOUBLY_LINKED_LIST) {
            contextualActionPane.getChildren().addAll(
                    createActionChip("Build List", "FULL_DEMO"),
                    createActionChip("+ Insert Begin", "INSERT_HEAD"),
                    createActionChip("+ Insert End", "INSERT_TAIL"),
                    createActionChip("+ Insert Before", "INSERT_BEFORE"),
                    createActionChip("+ Insert After", "INSERT_AFTER"),
                    createActionChip("- Delete Begin", "DELETE_HEAD"),
                    createActionChip("- Delete End", "DELETE_TAIL"),
                    createActionChip("- Delete Before", "DELETE_BEFORE"),
                    createActionChip("- Delete After", "DELETE_AFTER"),
                    createActionChip("- Delete Value", "DELETE_VALUE"),
                    createActionChip("🔍 Search", "SEARCH"),
                    createActionChip("➔ Display Forward", "DISPLAY_FORWARD"),
                    createActionChip("⬅ Display Backward", "DISPLAY_BACKWARD")
            );
        } else if (type == AlgorithmType.BINARY_SEARCH_TREE) {
            contextualActionPane.getChildren().addAll(
                    createActionChip("Build & Demo All", "FULL_DEMO"),
                    createActionChip("+ Insert(Val)", "INSERT"),
                    createActionChip("- Delete(Val)", "DELETE"),
                    createActionChip("🔍 Search(Val)", "SEARCH"),
                    createActionChip("Inorder", "INORDER"),
                    createActionChip("Preorder", "PREORDER"),
                    createActionChip("Postorder", "POSTORDER")
            );
        } else if (type.getCategory() == AlgorithmCategory.MODULE_C_SORTING) {
            contextualActionPane.getChildren().addAll(
                    createActionChip("▶ Run " + type.getDisplayName(), "FULL_DEMO"),
                    createCompareAllChip()
            );
        } else if (type == AlgorithmType.MODULE_D_BENCHMARK) {
            contextualActionPane.getChildren().addAll(
                    createActionChip("📊 Run Benchmark (Random Input)", "RANDOM"),
                    createActionChip("📈 Run Benchmark (Already Sorted Input)", "SORTED"),
                    createActionChip("📉 Run Benchmark (Reverse Sorted Input)", "REVERSE")
            );
        }
    }

    private Button createActionChip(String label, String modeCode) {
        Button btn = createStyledButton(label, "#1E293B", "#38BDF8");
        btn.setFont(Font.font("System", FontWeight.SEMI_BOLD, 11));
        btn.setOnAction(e -> executeCurrentAlgorithm(modeCode));
        return btn;
    }

    private Button createPresetNChip(String label, String nVal) {
        Button btn = createStyledButton(label, "#1E293B", "#FACC15");
        btn.setFont(Font.font("System", FontWeight.SEMI_BOLD, 11));
        btn.setOnAction(e -> {
            targetValueField.setText(nVal);
            executeCurrentAlgorithm("FULL_DEMO");
        });
        return btn;
    }

    private Button createBracketPresetChip(String label, String expr) {
        Button btn = createStyledButton(label, "#1E293B", "#10B981");
        btn.setFont(Font.font("System", FontWeight.SEMI_BOLD, 11));
        btn.setOnAction(e -> {
            inputArrayArea.setText(expr);
            executeCurrentAlgorithm("FULL_DEMO");
        });
        return btn;
    }

    private Button createCompareAllChip() {
        Button btn = createStyledButton("⚡ Compare All 5 Sorting Algorithms Side-by-Side", "#047857", "#FFFFFF");
        btn.setFont(Font.font("System", FontWeight.BOLD, 11.5));
        btn.setOnAction(e -> {
            algorithmCombo.getSelectionModel().select(AlgorithmType.COMPARE_ALL_SORTS);
            executeCurrentAlgorithm("FULL_DEMO");
        });
        return btn;
    }

    /**
     * Validates user inputs and executes the selected module task in the backend.
     */
    private void executeCurrentAlgorithm(String mode) {
        AlgorithmType selected = algorithmCombo.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        pausePlayback();

        // Special handling for Bracket Checker (accepts bracket symbols, not integers)
        if (selected == AlgorithmType.STACK_BRACKET_CHECKER) {
            String expr = inputArrayArea.getText();
            if (expr == null || expr.trim().isEmpty()) {
                showAlert("Validation Error", "Please enter a bracket expression such as { ( [ ] ) }.");
                return;
            }
            ExecutionResult res = CustomStack.checkBalancedBrackets(expr);
            loadExecutionResult(res);
            return;
        }

        // Validate integer parameter field for Fibonacci / Factorial / Hanoi
        if (selected == AlgorithmType.FIBONACCI_THREE_WAYS || selected == AlgorithmType.FACTORIAL_TWO_WAYS || selected == AlgorithmType.TOWER_OF_HANOI) {
            int[] parsedN = ArrayUtils.parseIntArray(targetValueField.getText());
            if (parsedN.length == 0) {
                showAlert("Input Validation Error", "Please enter a valid integer n in the Value / n field.");
                return;
            }
            int n = parsedN[0];
            if (selected == AlgorithmType.FACTORIAL_TWO_WAYS && n < 0) {
                ExecutionResult res = new FactorialLab().execute(n);
                loadExecutionResult(res);
                showAlert("Negative Input Rejected", "Factorial n! is undefined for negative numbers (n = " + n + "). Please enter n >= 0.");
                return;
            }
            if (selected == AlgorithmType.FIBONACCI_THREE_WAYS && n < 0) {
                ExecutionResult res = new FibonacciLab().execute(n);
                loadExecutionResult(res);
                showAlert("Negative Input Rejected", "Fibonacci F(n) requires n >= 0.");
                return;
            }
            if (selected == AlgorithmType.TOWER_OF_HANOI) {
                if (n < 1 || n > 8) {
                    showAlert("Out of Range (1 to 8 Disks)", "Tower of Hanoi requires between 1 and 8 disks. Clamping n to [1..8].");
                    n = Math.max(1, Math.min(8, n));
                    targetValueField.setText(String.valueOf(n));
                }
                int srcPeg = hanoiSourceCombo.getSelectionModel().getSelectedIndex();
                int dstPeg = hanoiDestCombo.getSelectionModel().getSelectedIndex();
                if (srcPeg == dstPeg) {
                    showAlert("Peg Selection Error", "Source peg and Destination peg must be different! Defaulting to Peg A -> Peg C.");
                    hanoiSourceCombo.getSelectionModel().select(0);
                    hanoiDestCombo.getSelectionModel().select(2);
                    srcPeg = 0;
                    dstPeg = 2;
                }
                int auxPeg = 3 - srcPeg - dstPeg;
                loadExecutionResult(new TowerOfHanoi().execute(n, srcPeg, dstPeg, auxPeg));
                return;
            }
            if (selected == AlgorithmType.FIBONACCI_THREE_WAYS) {
                loadExecutionResult(new FibonacciLab().execute(n));
                return;
            }
            if (selected == AlgorithmType.FACTORIAL_TWO_WAYS) {
                loadExecutionResult(new FactorialLab().execute(n));
                return;
            }
        }

        // Module D Benchmark needs no manual array
        if (selected == AlgorithmType.MODULE_D_BENCHMARK) {
            String distMode = ("SORTED".equals(mode) || "REVERSE".equals(mode)) ? mode : "RANDOM";
            loadExecutionResult(new SortingComparisonLab().executeModuleDBenchmark(distMode));
            return;
        }

        // Validate array input for Sorting, Binary Search, and Data Structure batch demos
        String rawInput = inputArrayArea.getText();
        int[] inputValues = ArrayUtils.parseIntArray(rawInput);
        if (inputValues.length == 0 && "FULL_DEMO".equals(mode)) {
            showAlert("Input Validation", "Input list is empty or non-numeric. Please enter comma-separated numbers or click 'Generate Random'. Restoring default sample.");
            inputValues = new int[]{64, 34, 25, 12, 22, 11, 90, 45};
            inputArrayArea.setText(ArrayUtils.arrayToCsv(inputValues));
        }

        int defaultTarget = inputValues.length > 0 ? inputValues[inputValues.length / 2] : 25;
        int targetValue = parseFieldValue(targetValueField, defaultTarget);
        int secondValue = parseFieldValue(referenceValueField, inputValues.length > 0 ? inputValues[0] : 25);

        ExecutionResult result;
        switch (selected) {
            case SINGLY_LINKED_LIST:
                result = singlyLinkedList.executeFullWorkflow(inputValues, targetValue, secondValue, mode);
                break;
            case DOUBLY_LINKED_LIST:
                result = doublyLinkedList.executeFullWorkflow(inputValues, targetValue, secondValue, mode);
                break;
            case CUSTOM_STACK:
                result = customStack.executeFullWorkflow(inputValues, targetValue, mode);
                break;
            case LINEAR_QUEUE:
                result = linearQueue.executeFullWorkflow(inputValues, targetValue, mode);
                break;
            case CUSTOM_QUEUE:
                result = circularQueue.executeFullWorkflow(inputValues, targetValue, mode);
                break;
            case BINARY_SEARCH_TREE:
                result = binarySearchTree.executeFullWorkflow(inputValues, targetValue, mode);
                break;
            case BUBBLE_SORT:
                result = new BubbleSort().execute(inputValues);
                break;
            case SELECTION_SORT:
                result = new SelectionSort().execute(inputValues);
                break;
            case INSERTION_SORT:
                result = new InsertionSort().execute(inputValues);
                break;
            case MERGE_SORT:
                result = new MergeSort().execute(inputValues);
                break;
            case QUICK_SORT:
                result = new QuickSort(false).execute(inputValues);
                break;
            case QUICK_SORT_RANDOMIZED:
                result = new QuickSort(true).execute(inputValues);
                break;
            case COMPARE_ALL_SORTS:
                result = new SortingComparisonLab().executeCompareAll(inputValues);
                break;
            case RECURSIVE_BINARY_SEARCH:
                result = new RecursiveBinarySearch().execute(inputValues, targetValue);
                break;
            default:
                result = new BubbleSort().execute(inputValues);
                break;
        }

        loadExecutionResult(result);
    }

    private void loadExecutionResult(ExecutionResult result) {
        this.currentExecutionResult = result;
        AlgorithmStep[] steps = result.getSteps();

        finalResultBanner.setText(result.getFinalResult());
        finalResultDetailBox.setText(result.getFinalResult());

        comparisonsLabel.setText(String.valueOf(result.getComparisonsCount()));
        swapsMovesLabel.setText(String.valueOf(result.getSwapsOrMovesCount()));
        totalOpsLabel.setText(String.valueOf(result.getTotalOperations()));
        execTimeNanosLabel.setText(result.getExecutionTimeNanos() + " ns");
        execTimeMicrosLabel.setText(String.format("%.3f µs", result.getExecutionTimeMicros()));
        execTimeMillisLabel.setText(String.format("%.4f ms", result.getExecutionTimeMillis()));

        updateComplexityPanel(result.getComplexityInfo());

        stepLogListView.getItems().clear();
        for (int i = 0; i < steps.length; i++) {
            AlgorithmStep s = steps[i];
            String logEntry = String.format("[Step %03d | Ops: %4d] %s",
                    s.getStepNumber(), s.getOperationsCount(), s.getDescription());
            stepLogListView.getItems().add(logEntry);
        }

        suppressScrubberEvent = true;
        if (steps.length > 1) {
            stepScrubber.setDisable(false);
            stepScrubber.setMin(0);
            stepScrubber.setMax(steps.length - 1);
            stepScrubber.setValue(0);
        } else {
            stepScrubber.setDisable(true);
            stepScrubber.setMin(0);
            stepScrubber.setMax(0);
            stepScrubber.setValue(0);
        }
        suppressScrubberEvent = false;

        if (steps.length > 0) {
            goToStep(steps.length - 1); // Show completed state or first state clearly
            if (steps.length > 1 && result.getTableRows() == null) {
                goToStep(0);
            }
        } else {
            visualizationCanvas.updateState(result.getVisualizationType(), null, result.getTableHeaders(), result.getTableRows());
        }
    }

    private void goToStep(int index) {
        if (currentExecutionResult == null) return;
        AlgorithmStep[] steps = currentExecutionResult.getSteps();
        if (index < 0 || index >= steps.length) return;
        this.currentStepIndex = index;
        AlgorithmStep step = steps[index];

        visualizationCanvas.updateState(
                currentExecutionResult.getVisualizationType(),
                step,
                currentExecutionResult.getTableHeaders(),
                currentExecutionResult.getTableRows()
        );
        activeStepBanner.setText("Step " + step.getStepNumber() + " of " + steps.length + ": " + step.getDescription());
        stepCounterLabel.setText(step.getStepNumber() + " / " + steps.length);
        stepOpsLabel.setText(String.valueOf(step.getOperationsCount()));

        suppressScrubberEvent = true;
        stepScrubber.setValue(index);
        suppressScrubberEvent = false;

        stepLogListView.getSelectionModel().select(index);
        stepLogListView.scrollTo(Math.max(0, index - 2));
    }

    private void updateComplexityPanel(ComplexityInfo info) {
        if (info == null) return;
        bestComplexityLabel.setText(info.getBestTime());
        avgComplexityLabel.setText(info.getAverageTime());
        worstComplexityLabel.setText(info.getWorstTime());
        spaceComplexityLabel.setText(info.getSpaceComplexity());
        stabilityLabel.setText(info.getStability());
        complexityNotesLabel.setText(info.getExplanation());
    }

    private void handleFileUpload(Stage stage) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Input Data File (.txt or .csv)");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text & CSV Files (*.txt, *.csv)", "*.txt", "*.csv")
        );
        File file = chooser.showOpenDialog(stage);
        if (file != null) {
            try {
                String content = Files.readString(file.toPath());
                int[] parsed = ArrayUtils.parseIntArray(content);
                if (parsed.length == 0) {
                    showAlert("Validation Warning", "The selected file did not contain any valid integers.");
                    return;
                }
                inputArrayArea.setText(ArrayUtils.arrayToCsv(parsed));
                statusFileLabel.setText("Loaded " + parsed.length + " numbers from " + file.getName());
                executeCurrentAlgorithm("FULL_DEMO");
            } catch (IOException ex) {
                showAlert("File Read Error", "Could not read file: " + ex.getMessage());
            }
        }
    }

    private int parseFieldValue(TextField field, int fallback) {
        int[] parsed = ArrayUtils.parseIntArray(field.getText());
        if (parsed.length > 0) {
            return parsed[0];
        }
        field.setText(String.valueOf(fallback));
        return fallback;
    }

    private HBox createLegendItem(String hexColor, String text) {
        HBox item = new HBox(5);
        item.setAlignment(Pos.CENTER_LEFT);
        Circle dot = new Circle(5.0, Color.web(hexColor));
        Label lbl = new Label(text);
        lbl.setTextFill(Color.web("#CBD5E1"));
        lbl.setFont(Font.font("System", 11));
        item.getChildren().addAll(dot, lbl);
        return item;
    }

    private Label createMetricValueLabel(String initialText) {
        Label lbl = new Label(initialText);
        lbl.setTextFill(Color.web("#F8FAFC"));
        lbl.setFont(Font.font("Consolas", FontWeight.BOLD, 12));
        lbl.setWrapText(true);
        return lbl;
    }

    private void addMetricRow(GridPane grid, int row, String title, Label valueLabel) {
        Label keyLbl = new Label(title);
        keyLbl.setTextFill(Color.web("#94A3B8"));
        keyLbl.setFont(Font.font("System", FontWeight.SEMI_BOLD, 11.5));
        grid.add(keyLbl, 0, row);
        grid.add(valueLabel, 1, row);
    }

    private Button createStyledButton(String text, String bgHex, String fgHex) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + bgHex + "; -fx-text-fill: " + fgHex
                + "; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 5 10 5 10; -fx-cursor: hand;");
        return btn;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
