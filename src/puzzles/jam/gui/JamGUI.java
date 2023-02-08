package puzzles.jam.gui;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import puzzles.common.Observer;
import puzzles.jam.model.JamModel;
import java.io.File;
import java.util.HashMap;
import java.util.Objects;

/**
 * JavaFX application class for Jam GUI
 * @author Kilian Jakstis
 */
public class JamGUI extends Application  implements Observer<JamModel, String> {

    private JamModel model; // hold the model for the game
    private HBox notification; // the box node displaying
    private GridPane display; // the button grid representing the board
    private HBox buttons; // Load, reset, hint buttons
    private VBox main;
    private Label note; // label for showing notifications to user
    private HashMap<Character, String> COLORS; // map for more easily assigning colors to cars
    private final static int BUTTON_FONT_SIZE = 20; // font size
    private final static int ICON_SIZE = 75; // icon size

    /**
     * Initial set up for application. Get command line arguments, instantiate Jam model, load the initial Jam file,
     * and add this GUI class as an observer of the model.
     */
    public void init() {
        String filename = getParameters().getRaw().get(0);
        this.model = new JamModel();
        this.model.load(filename);
        this.model.addObserver(this);
    }

    /**
     * Start the app, build the GIU elements.
     * @param stage JavaFX root stage
     * @throws Exception could crash, hopefully not
     */
    @Override
    public void start(Stage stage) throws Exception {
        this.COLORS = new HashMap<>();
        this.stockColors();
        VBox main = new VBox();
        Label note = new Label("Get the red 'X' car to the right side of the screen!");
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
        this.display = makeDisplay(this.model.currentConfig.getCols(), this.model.currentConfig.getRows());
        this.updateDisplay();
        main.getChildren().addAll(this.notification, this.display, buttons);
        this.main = main;
        buttons.setAlignment(Pos.CENTER);
        this.notification.setAlignment(Pos.CENTER);
        display.setAlignment(Pos.CENTER);
        main.setMinSize(400, 400);
        Scene scene = new Scene(main);
        stage.setTitle("Traffic Jam!");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Called by the Model field when the state of the game changes.
     * Calls the updateDisplay method so that the GUI display reflects the change in game state.
     * @param jamModel the model
     * @param msg the message describing the state change that took place
     */
    @Override
    public void update(JamModel jamModel, String msg) {
        if (msg.split("\s++")[0].equals("Loaded:")){
            // load the new file and reset the board accordingly
            GridPane newDisplay = makeDisplay(this.model.currentConfig.getCols(), this.model.currentConfig.getRows());
            this.main.getChildren().clear();
            this.note.setText(msg.split("\n")[0]);
            this.display = newDisplay;
            this.main.getChildren().addAll(this.notification, newDisplay, this.buttons);
            this.updateDisplay();
        }
        // put the appropriate update message in the notification box
        if (jamModel.currentConfig.isSolution()){
            this.note.setText("You win!");
        } else {
            this.note.setText(msg.split("\n")[0]);
        }
        // update the GIU
        this.updateDisplay();
    }

    /**
     * Update the grid board section of the GUI
     */
    private void updateDisplay(){
        // for every button in the board grid, check the model's board and adjust the visual state
        // as needed
        for (int x = 0; x < this.model.currentConfig.getCols(); x++){
            for (int y = 0; y < this.model.currentConfig.getRows(); y++){
                char c = this.model.currentConfig.boardChars[x][y];
                Button b = Objects.requireNonNull(this.getNodeFromGridPane(this.display, x, y));
                b.setStyle("-fx-font-size: " + BUTTON_FONT_SIZE + ";" +
                        "-fx-background-color: " + this.COLORS.get(c) + ";" +
                        "-fx-font-weight: bold; ");
                if (c != '.') {
                    b.setText(String.valueOf(c));
                } else {
                    b.setText(" ");
                }
            }
        }
    }

    /**
     * When a button is clicked, tell the model which one was selected
     * @param b the button that was clicked
     */
    private void select(Button b){
        int x = GridPane.getColumnIndex(b);
        int y = GridPane.getRowIndex(b);
        this.model.select(x, y);
    }

    /**
     * Make the board grid display for the GUI
     * @param cols number of cols
     * @param rows number of rows
     * @return the GridPane object
     */
    private GridPane makeDisplay(int cols, int rows){
        GridPane display = new GridPane();
        for (int x = 0; x < cols; x++){
            for (int y = 0; y < rows; y++){
                Button b = new Button();
                display.add(b, x, y);
                b.setStyle("-fx-font-size: " + BUTTON_FONT_SIZE + ";" +
                        "-fx-background-color: " + this.COLORS.get('.') + ";" +
                        "-fx-font-weight: bold;");
                b.setMinSize(ICON_SIZE, ICON_SIZE);
                b.setMaxSize(ICON_SIZE, ICON_SIZE);
                b.setOnAction(e -> this.select(b));
            }
        }
        display.setAlignment(Pos.CENTER);
        return display;
    }

    /**
     * Open a FileChooser so that a different puzzle file can be selected by the user and played
     * Tell the model to load the file and change the game state
     */
    private void load(){
        FileChooser fc = new FileChooser();
        fc.setTitle("Get Resource File");
        File selectedFile = fc.showOpenDialog(null);
        if (selectedFile != null) {
            this.model.load(selectedFile.getAbsolutePath());
        } else {
            this.model.load("data/jam/jam-4.txt");
        }
    }

    /**
     * Helper for accessing specific buttons
     * @param gridPane the board display grid
     * @param col column selected
     * @param row row selected
     * @return the button that corresponds to (col, row)
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
     * Put colors into the color hashmap for every letter in the alphabet
     * To get the car's color, just hash its ID and check the color map
     */
    private void stockColors(){
        this.COLORS.put('.', "#Ffffff");
        this.COLORS.put('A', "#0795ff");
        this.COLORS.put('B', "#00477d");
        this.COLORS.put('C', "#4176a0");
        this.COLORS.put('D', "#6a8498");
        this.COLORS.put('E', "#55585c");
        this.COLORS.put('F', "#4d09f7");
        this.COLORS.put('G', "#200369");
        this.COLORS.put('H', "#55399c");
        this.COLORS.put('I', "#9d7ff1");
        this.COLORS.put('J', "#C5b5f3");
        this.COLORS.put('K', "#F70dd8");
        this.COLORS.put('L', "#5a044f");
        this.COLORS.put('M', "#D87784");
        this.COLORS.put('N', "#60383e");
        this.COLORS.put('O', "#00ffe6");
        this.COLORS.put('P', "#05564e");
        this.COLORS.put('Q', "#A9ece6");
        this.COLORS.put('R', "#016b31");
        this.COLORS.put('S', "#05ff76");
        this.COLORS.put('T', "#5f9477");
        this.COLORS.put('U', "#38b91d");
        this.COLORS.put('V', "#F2ff03");
        this.COLORS.put('W', "#75790c");
        this.COLORS.put('X', "#DF0101");
        this.COLORS.put('Y', "#1c1102");
        this.COLORS.put('Z', "#C9a77a");
    }

    /**
     * Launch the app
     * @param args any cmd line arguments
     */
    public static void main(String[] args) {
        Application.launch(args);
    }
}
