package puzzles.hoppers.gui;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import puzzles.common.Observer;
import puzzles.hoppers.model.HoppersConfig;
import puzzles.hoppers.model.HoppersModel;
import javafx.application.Application;
import javafx.stage.Stage;
import java.io.File;
import java.util.Objects;

/**
 * GUI for hopper game
 * @author Kilian Jakstis
 */
public class HoppersGUI extends Application implements Observer<HoppersModel, String> {

    /** The resources directory is located directly underneath the gui package */
    private final static String RESOURCES_DIR = "resources/";
    /** Frog pics (0_0) */
    private final Image redFrog = new Image(Objects.requireNonNull(getClass().getResourceAsStream(RESOURCES_DIR + "red_frog.png")));
    private final Image greenFrog = new Image(Objects.requireNonNull(getClass().getResourceAsStream(RESOURCES_DIR + "green_frog.png")));
    private final Image lilyPad = new Image(Objects.requireNonNull(getClass().getResourceAsStream(RESOURCES_DIR + "lily_pad.png")));
    private final Image water = new Image(Objects.requireNonNull(getClass().getResourceAsStream(RESOURCES_DIR + "water.png")));
    /** Fields for the model and some key GUI element references */
    private Stage stage;
    private HoppersModel model;
    private HBox notification;
    private GridPane display;
    private HBox buttons;
    private VBox main;
    private Label note;

    /**
     * Sets up the model
     */
    public void init() {
        String filename = getParameters().getRaw().get(0);
        this.model = new HoppersModel();
        this.model.load(filename);
        this.model.addObserver(this);
    }

    /**
     * Starts the application - makes all GUI elements
     * @param stage - the state
     * @throws Exception because sometimes things go wrong even though we tried our best
     */
    @Override
    public void start(Stage stage) throws Exception {
        VBox main = new VBox();
        Label note = new Label("Get the red frog alone by jumping frogs over one another!");
        HBox notification = new HBox();
        notification.getChildren().add(note);
        note.setAlignment(Pos.CENTER);
        note.setTextAlignment(TextAlignment.CENTER);
        this.note = note;
        notification.setMinSize(100, 20);
        this.notification = notification;
        HBox buttons = new HBox();
        Button load = new Button("Load");
        load.setMinSize(60, 40);
        load.setOnAction(e -> this.load());
        Button reset = new Button("Reset");
        reset.setMinSize(60, 40);
        reset.setOnAction(e -> this.model.reset());
        Button hint = new Button("Hint");
        hint.setMinSize(60, 40);
        hint.setOnAction(e -> this.model.hint());
        buttons.getChildren().addAll(load, reset, hint);
        this.buttons = buttons;
        this.display = makeDisplay(this.model.currentConfig.cols, this.model.currentConfig.rows);
        this.updateDisplay();
        main.getChildren().addAll(this.notification, this.display, buttons);
        this.main = main;
        buttons.setAlignment(Pos.CENTER);
        this.notification.setAlignment(Pos.CENTER);
        display.setAlignment(Pos.CENTER);
        main.setMinSize(400, 400);
        Scene scene = new Scene(main);
        this.stage = stage;
        stage.setTitle("Hoppers!");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Displays updates from the model to the user
     * Updates the gridPane board based on any hint or user movements
     * Also resets the GUI when loading in new puzzle files
     * @param hoppersModel - the model
     * @param msg - the message to be displayed
     */
    @Override
    public void update(HoppersModel hoppersModel, String msg) {
        if (msg.split("\s++")[0].equals("Loaded:")){
            GridPane newDisplay = makeDisplay(this.model.currentConfig.cols, this.model.currentConfig.rows);
            this.main.getChildren().clear();
            this.note.setText(msg.split("\n")[0]);
            this.display = newDisplay;
            this.main.getChildren().addAll(this.notification, newDisplay, this.buttons);
            this.stage.setHeight(this.model.currentConfig.rows * 170);
            this.stage.setWidth(this.model.currentConfig.cols * 110);
            this.updateDisplay();
        }
        // put update msg in notification label
        if (hoppersModel.currentConfig.isSolution()){
            this.note.setText("You win! (0_0)");
        } else {
            this.note.setText(msg.split("\n")[0]);
        }
        // update the board grid
        this.updateDisplay();
    }

    /**
     * Opens a fileChooser to pick the next puzzle file and passes that file location along to the model
     * for building the puzzle
     */
    private void load(){
        FileChooser fc = new FileChooser();
        fc.setTitle("Get Resource File");
        File selectedFile = fc.showOpenDialog(null);
        if (selectedFile != null) {
            this.model.load(selectedFile.getAbsolutePath());
        } else {
            this.model.load("data/hoppers/hoppers-4.txt");
        }
    }

    /**
     * For a selected display button, relays the coordinates to the model to see if they are a valid selection
     * @param b - the button clicked
     */
    private void select(Button b){
        String c = GridPane.getColumnIndex(b).toString();
        String r = GridPane.getRowIndex(b).toString();
        this.model.select(r, c);
    }

    /**
     * Builds the central gridPane board
     * @param cols - number of cols
     * @param rows - number of rows
     * @return - this gridPane
     */
    private GridPane makeDisplay(int cols, int rows){
        GridPane display = new GridPane();
        for (int x = 0; x < cols; x++){
            for (int y = 0; y < rows; y++){
                Button b = new Button();
                display.add(b, x, y);
                b.setMinSize(60, 60);
                b.setStyle("-fx-border-color: #000000; -fx-border-width: 0px;");
                b.setOnAction(e -> this.select(b));
            }
        }
        display.setAlignment(Pos.CENTER);
        return display;
    }

    /**
     * Loops through all the buttons in the display and updates their picture based on the model's current
     * configuration's position
     */
    private void updateDisplay(){
        for (int x = 0; x < this.model.currentConfig.cols; x++){
            for (int y = 0; y < this.model.currentConfig.rows; y++){
                char c = this.model.currentConfig.board[x][y];
                if (c == HoppersConfig.VALID){
                    Objects.requireNonNull(this.getNodeFromGridPane(this.display, x, y)).setGraphic(new ImageView(this.lilyPad));
                } else if (c == HoppersConfig.INVALID){
                    Objects.requireNonNull(this.getNodeFromGridPane(this.display, x, y)).setGraphic(new ImageView(this.water));
                } else if (c == HoppersConfig.GREEN){
                    Objects.requireNonNull(this.getNodeFromGridPane(this.display, x, y)).setGraphic(new ImageView(this.greenFrog));
                } else {
                    Objects.requireNonNull(this.getNodeFromGridPane(this.display, x, y)).setGraphic(new ImageView(this.redFrog));
                }
            }
        }
    }

    /**
     * Get access a specific button
     * @param gridPane - the gridPane from which the button is taken
     * @param col - column of desired button
     * @param row - row of desired button
     * @return that button
     */
    private Button getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (Node b : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(b) == col && GridPane.getRowIndex(b) == row) {
                return (Button) b;
            }
        }
        return null;
    }

    /**
     * Launches the GUI with the file specified on the command line
     * @param args - the file
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java HoppersPTUI filename");
        } else {
            Application.launch(args);
        }
    }
}
