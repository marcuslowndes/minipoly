package view;


import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import controller.Controller;
import controller.Controller.posnState;
import model.*;


/**
 * The View Class for the GUI version of the Minipoly Game, extending the
 * Observer class to follow the Model-View-Controller design pattern.
 * <p>
 * This uses the JavaFX Framework to create a desktop application version of
 * the Minipoly game with Graphical User Interface representing a board with
 * player counters. The user can control the game by clicking buttons, or
 * traversing between buttons using the arrow keys and pressing the Enter key.
 * <p>
 * Written according to specification provided by Dr. Ian Bailey
 * for Oxford Brookes Computer Science BSc
 * <p> Module COMP6018: Advanced Object Oriented Programming
 *
 * @author Marcus Lowndes
 */
public class View extends Application implements Observer {

    private Model model;
    private Controller controller;
    private boolean cheatMode;

    private final Label messageLabel = new Label();
    private final Label posnInspectLabel = new Label();
    private final Label playerOneMoneyLabel = new Label();
    private final Label playerTwoMoneyLabel = new Label();
    private final Label diceRollLabel = new Label();
    private final Label winnerLabel = makeInfoLabel("    ", null, 1.6);

    private final Pane currentPlayerPane = new Pane();
    private ComboBox<String> cheatOptions;

    private final Button nextTurnBtn = new Button();
    private final Button interactBtn = new Button();

    private final ArrayList<Pane> propertiesPanes = new ArrayList<>();
    private final ArrayList<HBox> houseIcons = new ArrayList<>();
    private final ArrayList<HBox> playerCounterPanes = new ArrayList<>();

    private final double height = 900, width = height / 0.8;


    @Override
    public void start(Stage stage) throws Exception {
        model = new Model();
        controller = new Controller(model);
        controller.setView(this);
        makeCheatDialog();

        HBox root = new HBox();
        root.setPadding(new Insets(10, 10, 10, 10));
        root.getChildren().addAll(
                makeGameBoard(),
                makeControlPanel()
        );

        stage.setResizable(false);
        stage.setTitle("Minipoly");
        stage.setScene(new Scene(root, width, height+7));
        stage.show();

        model.addObserver(this);
        update(null, null);
    }


    /**
     * Creates an Alert that queries to the user if they want to enable
     * Cheat Mode. If they click 'Yes', then Cheat Mode is enabled,
     * otherwise, cheat mode is disabled.
     *
     * @return Dialog window that enables Cheat Mode if Yes is clicked
     */
    private Alert makeCheatDialog(){
        Alert cheatDialog = new Alert(Alert.AlertType.CONFIRMATION);
        cheatDialog.setTitle("Enable Cheats?");
        cheatDialog.setHeaderText("Enable Cheat Mode");
        cheatDialog.setContentText("Clicking Yes will enable Cheat Mode.\n"
                + "\nThis mode allows you to choose what the next Player"
                + " rolls at the start of their turn. This is intended for"
                + " testing purposes only.\n\n");

        ButtonType yesBtn = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType noBtn = new ButtonType("No", ButtonBar.ButtonData.NO);
        cheatDialog.getButtonTypes().setAll(yesBtn, noBtn);

        Optional<ButtonType> result = cheatDialog.showAndWait();
        cheatMode = (result.get() == yesBtn);

        return cheatDialog;
    }


    /**
     * Creates the GUI for the board of the Minipoly game. This includes every
     * position in the game, displayed as rectangles and squares that line the
     * edges and corners of the board.
     *
     * @return GridPane containing the entire game board including each position
     */
    private GridPane makeGameBoard(){
        GridPane boardGrid = new GridPane();
        int num = 1;

        // bottom-right corner (Go)
        boardGrid.add(makeBoardPosn(2, 2, num, false), 11, 11, 2, 2);

        // bottom row
        for (int i=10;i>1;i--) {
            num++;
            boardGrid.add(makeBoardPosn(1, 2, num, false), i, 11, 1, 2);
        }

        // bottom-left corner
        num++;
        boardGrid.add(makeBoardPosn(2, 2, num, false), 0, 11, 2, 2);

        // right side
        for (int i=10;i>1;i--) {
            num++;
            boardGrid.add(makeBoardPosn(2, 1, num, true), 0, i, 2, 1);
        }

        // top-left corner (Jail)
        num++;
        boardGrid.add(makeBoardPosn(2, 2, num, false), 0, 0, 2, 2);

        // top row
        for (int i=2;i<11;i++) {
            num++;
            boardGrid.add(makeBoardPosn(1, 2, num, false), i, 0, 1, 2);
        }

        // top-right corner
        num++;
        boardGrid.add(makeBoardPosn(2, 2, num, false), 11, 0, 2, 2);

        // left side
        for (int i=2;i<11;i++) {
            num++;
            boardGrid.add(makeBoardPosn(2, 1, num, true), 11, i, 2, 1);
        }

        // center
        Pane center = makeBoardPosn(9, 9, 0, true);
        double boxsize = center.getMinWidth();
        center.setMinSize(boxsize+1, boxsize+1);
        center.getChildren().clear();
        center.setOnMouseClicked(null);
        boardGrid.add(center, 2, 2, 9, 9);

        boardGrid.setBorder(makeBorder());
        return boardGrid;
    }


    /**
     * Creates a single GUI element representing a Minipoly game board position.
     * The position's shape is defined based on its place on the board and can be
     * oriented either horizontally or vertically.
     *
     * @param   w       the width multiplier
     * @param   h       the height multiplier
     * @param   i       the index of the position on the board
     * @param   isHBox  true if the board position is on the side of the board
     * @return  a Pane containing a single board position
     */
    private Pane makeBoardPosn(int w, int h, int i, boolean isHBox){
        Pane posn = isHBox ? new HBox() : new VBox();

        // format box
        double boxsize = ((double)height - 5.0) / 13.0;
        posn.setMinSize(w*boxsize, h*boxsize);
        posn.setMaxSize(w*boxsize, h*boxsize);
        posn.setBorder(makeBorder());
        posn.setPadding(new Insets(5, 6, 5, 6));

        // create contents
        VBox nameBox = makePosnNameBox(w, i, boxsize, isHBox);
        VBox infoBox = makePosnInfoBox(i, boxsize, isHBox, (w == 2 && h == 2));
        posn.getChildren().addAll(nameBox, infoBox);

        // make each property position inspectable
        if (i != 0 && controller.getPosition(i).isProperty()){
            posn.setOnMouseClicked((MouseEvent e) -> {
                String[] s = controller.getPosition(i).toString().split("\t");
                posnInspectLabel.setText("\t  Inspect A Property\n"
                        + "Position Number:\t" + s[0].replace(":", "")
                        + "\nProperty Name:\t" + s[1].replace(" ", "")
                        + "\nProperty Price:\t\t" + s[2]
                        + "\nProperty Owner:\t" + s[3]
                        + "\nImprovements:\t" + s[4].replace(".0", "")
                );
            });

            // allow posn to be accessible to set bg colour in future
            posn.setId(String.valueOf(i));
            propertiesPanes.add(posn);
        }

        return posn;
    }


    /**
     * Creates a small Pane containing the name of the position.
     *
     * @param   w       the width multiplier of the parent box
     * @param   i       the index of the position on the board
     * @param   boxsize the default size of the parent box
     * @param   isHBox  true if the board position is on the side of the board
     * @return  a VBox containing the name of the board position
     */
    private VBox makePosnNameBox(int w, int i, double boxsize, boolean isHBox){
        VBox posnNameBox = new VBox();
        posnNameBox.setMinSize(boxsize*0.5, boxsize*0.5);
        posnNameBox.setPadding(isHBox ? new Insets(17, 10, 10, 17)
                                      : new Insets(10, 10, 10, 10));

        // find posn in the model to display correct info
        for (Position p : model.getBoard())
            if (i == p.getNumber()){
                // display a name if the position is a property or Go or Jail
                Label roadLabel = new Label(
                        (p.isProperty() || p.getNumber() == 1
                                        || p.getNumber() == 21) ?
                        p.nameToString() : ""
                );

                // format box
                roadLabel.setMaxWidth(w*boxsize - 14);
                roadLabel.setAlignment(Pos.BOTTOM_CENTER);
                roadLabel.setScaleX(2);
                roadLabel.setScaleY(2);
                posnNameBox.getChildren().add(roadLabel);
            }

        return posnNameBox;
    }


    /**
     * Creates a small Pane containing any information that could be displayed
     * to the user, some of which will be updated frequently. This information
     * includes the price of, and any improvements on, the position (if it's a
     * property) and any players that may have landed on it.
     *
     * @param   i           the index of the position on the board
     * @param   boxsize     the default size of the parent box
     * @param   isHBox      true if the board position is on the side of the board
     * @param   isCorner    true if the board position is in the corner of the board
     * @return  a VBox containing the name of the board position
     */
    private VBox makePosnInfoBox(int i, double boxsize,
                                 boolean isHBox, boolean isCorner){
        VBox posnInfoBox = new VBox();
        posnInfoBox.setMinSize(boxsize*0.5, boxsize*0.5);
        posnInfoBox.setPadding(isHBox ? new Insets(0, 0, 0, 15)
                                      : new Insets(5, 0, 5, 0 ));

        // find posn in the model to display correct info
        for (Position p : model.getBoard())
            if (i == p.getNumber()) {

                // display property information
                if (p.isProperty()) {
                    posnInfoBox.getChildren().add(new Label(
                            "£" + String.valueOf(p.getPrice()) + "0"
                    ));

                    HBox housesPane = new HBox(1);
                    housesPane.setPadding(isHBox ? new Insets(2,0,2,0)
                                                : new Insets(6,0,6,0));
                    housesPane.setId(String.valueOf(i));
                    houseIcons.add(housesPane);
                    posnInfoBox.getChildren().add(housesPane);
                } else {
                    posnInfoBox.getChildren().add(new Label("  "));
                }

                // pane to display player(s) at position
                HBox playersPane = new HBox();
                playersPane.setId(String.valueOf(i));
                if (isCorner)
                    playersPane.setTranslateX(35);
                playerCounterPanes.add(playersPane);
                posnInfoBox.getChildren().add(playersPane);
            }

        return posnInfoBox;
    }


    /**
     * Creates the GUI for the controls of the Minipoly game. This includes any
     * buttons and additional information to be displayed to the user.
     *
     * @return  a VBox containing the controls and additional information that
     *          does not fit on the board
     */
    private VBox makeControlPanel(){
        VBox controlPanel = new VBox(15);
        controlPanel.setPadding(new Insets(10, 0, 0, 8));
        double panelWidth = (width-80) * 0.2;
        Border panelBorder = new Border(new BorderStroke(
                Color.BLACK,         BorderStrokeStyle.SOLID,
                new CornerRadii(5), BorderWidths.DEFAULT
        ));

        // set up title label
        Label title = new Label();
        title.setText("MINIPOLY");
        title.setScaleX(3); title.setScaleY(3);
        title.setTranslateX(70);

        // set up message pane
        messageLabel.setMinHeight(height * 0.2);
        messageLabel.setScaleX(1.1); messageLabel.setScaleY(1.1);
        TextFlow messagePanel = makeTextPanel(messageLabel, panelBorder, "Welcome"
                + " to Minipoly!\nGame by Marcus Lowndes\n\nClick a position to "
                + "inspect it.", new Insets(14, 17, 14, 17), panelWidth);

        // set up inspector pane
        Pane inspectorPanel = makeTextPanel(posnInspectLabel, panelBorder,
                "\t  Inspect A Property\nPosition Number:\nProperty Name:\n"
                        + "Property Price:\nProperty Owner:\nImprovements:",
                new Insets(7, 7, 7, 7), panelWidth);

        // display both players' money
        GridPane moneyPane = makeInfoPane(panelWidth);
        moneyPane.add(makeInfoLabel("Player 1: ", playerOneMoneyLabel, 1.3), 0, 0);
        moneyPane.add(playerOneMoneyLabel, 1, 0);
        moneyPane.add(makeInfoLabel("Player 2: ", playerTwoMoneyLabel, 1.3), 0, 1);
        moneyPane.add(playerTwoMoneyLabel, 1, 1);

        // display current player, dice roll, and cheat roll options
        GridPane gameInfoPane = makeInfoPane(panelWidth);
        gameInfoPane.add(makeInfoLabel(" Current Player:", null, 1.3), 0, 0);
        gameInfoPane.add(currentPlayerPane, 1, 0);
        gameInfoPane.add(makeInfoLabel("Dice Roll: ", diceRollLabel, 1.3), 0, 1);
        gameInfoPane.add(diceRollLabel, 1, 1);
        if (cheatMode) {
            gameInfoPane.setHgap(25);
            gameInfoPane.add(makeInfoLabel("Cheat Roll:", null, 1.3), 0, 2);
            gameInfoPane.add(makeCheatOptions(), 1, 2);
        }

        // display the winner of the game
        GridPane winnerPane = makeInfoPane(panelWidth);
        winnerPane.add(new Label(" "), 0, 1, 3, 1);
        winnerPane.add(winnerLabel, 1, 0);

        // set up buttons
        nextTurnBtn.setText("Next Turn");
        nextTurnBtn.setMinSize(panelWidth, 40);
        nextTurnBtn.setOnAction((ActionEvent e) -> {
            messageLabel.setText(controller.nextTurn());
            update(null, null);
        });
        nextTurnBtn.defaultButtonProperty().bind(nextTurnBtn.focusedProperty());

        interactBtn.setMinSize(panelWidth, 40);
        interactBtn.setOnAction((ActionEvent e) -> {
            messageLabel.setText(controller.interact());
            update(null, null);
        });
        interactBtn.defaultButtonProperty().bind(interactBtn.focusedProperty());

        // add all to panel
        controlPanel.getChildren().addAll(
                title, messagePanel, inspectorPanel, moneyPane,
                makeSpace(panelWidth), gameInfoPane, makeSpace(panelWidth),
                winnerPane, makeSpace(panelWidth), nextTurnBtn, interactBtn
        );
        return controlPanel;
    }


    /**
     * @param   contents    the Label that this panel will wrap around
     * @param   border      the border of the panel
     * @param   text        the default text of the <code>contents</code> Label
     * @param   padding     the padding used to space this node from others
     * @param   textWidth   the maximum width for which text can be displayed
     * @return  a panel for displaying text results of actions made in the game
     */
    private TextFlow makeTextPanel(Label contents, Border border, String text,
                                   Insets padding, double textWidth){
        TextFlow panel = new TextFlow();
        panel.setBorder(border);
        contents.setText(text);
        contents.setPadding(padding);
        contents.setAlignment(Pos.TOP_LEFT);
        contents.setMaxWidth(textWidth);
        contents.setWrapText(true);
        panel.getChildren().add(contents);
        return panel;
    }


    /**
     * @param   width   the width of the pane
     * @return  a GridPane for displaying information from the game
     */
    private GridPane makeInfoPane(double width){
        GridPane infoPane = new GridPane();
        infoPane.setPadding(new Insets(7, 0, 0, 17));
        infoPane.setMinWidth(width);
        infoPane.setHgap(40); infoPane.setVgap(10);
        return infoPane;
    }


    /**
     * @param   text        the Label's text
     * @param   resultLabel a separate Label that can be associated with it
     * @param   scale       scales the size of the Label
     * @return  a Label that describes information from the game
     */
    private Label makeInfoLabel(String text, Label resultLabel, double scale){
        Label infoLabel = new Label(text);
        infoLabel.setScaleX(scale);
        infoLabel.setScaleY(scale);
        if (resultLabel != null) {
            resultLabel.setScaleX(scale);
            resultLabel.setScaleY(scale);
        }
        return infoLabel;
    }


    /** @return a drop-down menu for use in Cheat Mode, to allow the user to
     *          choose the next dice roll */
    private ComboBox<String> makeCheatOptions() {
        assert (cheatMode) : "The Cheat Roll Menu can only be made if"
                                + " Cheat Mode is enabled.";

        String nums[] = {"1","2","3","4","5","6","7","8","9","10","11","12"};
        cheatOptions = new ComboBox<>(
                FXCollections.observableArrayList(nums)
        );

        cheatOptions.setOnAction((ActionEvent e) -> {
            messageLabel.setText(controller.cheatNextTurn(
                    cheatOptions.getValue()
            ));
            update(null, null);
        });
        return cheatOptions;
    }


    /**
     * @param   width   the width of the pane
     * @return  a Pane that fills empty space in the control panel
     */
    private Pane makeSpace(double width){
        Pane space = new Pane();
        space.setMaxWidth(width);
        space.setMinHeight( cheatMode ? 50 : 60 );
        return space;
    }


    @Override
    public void update(Observable o, Object arg) {
        // update info pane
        playerOneMoneyLabel.setText("£" + String.valueOf(
                model.getPlayerOne().getMoney()
        ) + "0");
        playerTwoMoneyLabel.setText("£" + String.valueOf(
                model.getPlayerTwo().getMoney()
        ) + "0");
        diceRollLabel.setText(String.valueOf(model.getDiceRoll()));
        currentPlayerPane.getChildren().clear();
        currentPlayerPane.getChildren().add(
                makePlayerCounter(model.getCurrentPlayer().isPlayerOne())
        );

        // display player counters
        for (HBox p : playerCounterPanes)
            updatePlayers(p);

        // enable/disable interact button
        updateButton(controller.getCurrentPosnState());

        // for each position on the board...
        for (Position p : model.getBoard()) {

            // ...display number of improvements
            for (HBox b : houseIcons)
                if (controller.isPosn(b.getId(), p.getNumber()))
                    updateHouses(p, b);

            // ...colour code the positions, based on ownership
            for (Pane b : propertiesPanes)
                if (controller.isPosn(b.getId(), p.getNumber()))
                    updatePosnBackgrounds(p, b);
        }

        // end game
        if (model.isGameOver()){
            nextTurnBtn.setDisable(true);
            interactBtn.setDisable(true);
            if (cheatMode)
                cheatOptions.setDisable(true);

            messageLabel.setText(messageLabel.getText() + "\n\n\t   GAME OVER");
            String w = (model.getPlayerOne().getMoney() <= 0) ? "2" : "1";
            winnerLabel.setText("Player " + w + " Wins!");
        }

    }


    /**
     * Update the positions on the board where the players are present.
     *
     * @param   playersPane a Pane in which to create player counters
     *                      if a player is on the parent position
     */
    private void updatePlayers(HBox playersPane){
        playersPane.getChildren().clear();
        String id = playersPane.getId();

        if (controller.isPlayerOnPosn(id, true))
            playersPane.getChildren().addAll(
                    makePlayerCounter(true),
                    new Label("  ")
            );

        if (controller.isPlayerOnPosn(id, false))
            playersPane.getChildren().add(
                    makePlayerCounter(false)
            );
    }


    /**
     * Enable/disable the interact button based on the state of the
     * current player's position.
     *
     * @pre     the state must be initialised and cannot be null
     * @param   state   the state of the current player's position
     */
    private void updateButton(posnState state){
        assert (state != null) : "The state of the position"
                                    + " must be initialised.";
        interactBtn.setDisable(false);
        switch (state) {
            case BUYABLE:
                interactBtn.setText("Buy");
                break;

            case IMPROVABLE:
                interactBtn.setText("Improve");
                break;

            case NONE:
                interactBtn.setDisable(true);
                interactBtn.setText("Buy or Improve");
                break;
        }
    }


    /**
     * Display on the board any improvements made to properties.
     * For every improvement made to the property, a house icon is displayed,
     * however, if a hotel has been built on the property, the word "Hotel"
     * is displayed instead.
     *
     * @param   p   the position on the board for which improvements
     *              may have been made
     * @param   b   the Pane on the GUI for which house icons will be made
     *              if there are improvements on the property
     * @pre     the position must be a property
     */
    private void updateHouses(Position p, HBox b){
        assert (p.isProperty()) : "The position is not a property and therefore"
                                  + " cannot have improvements.";
        if (!p.isHotel()){
            b.getChildren().clear();
            for (int i=0; i<p.getImprovements(); i++)
                b.getChildren().add(makeHouseIcon());
        } else {
            b.getChildren().clear();
            b.getChildren().add(new Label("Hotel"));
            b.setPadding(new Insets(-2,0,-1,0));
        }
    }


    /**
     * Colour codes the property positions, based on their owner. If a position
     * has an owner, that player's colour is displayed in the background of the
     * position. If there is no owner, the background is left transparent.
     * <p>
     * For aesthetic reasons, and the backgrounds also change colour if the
     * mouse cursor is hovering over the position, or is the user is clicking
     * the position. This will most likely occur when the user wants to inspect
     * all the information about a property position.
     *
     * @param   p   the position on the board
     * @param   b   the position Pane in the GUI for which the background
     *              may be updated
     */
    private void updatePosnBackgrounds(Position p, Pane b){
        Color bgColour, bgColourHover, bgColourClick;

        if (p.getOwner() != null) {
            if (!p.getOwner().isPlayerOne()) {
                bgColour      = Color.LIGHTBLUE;
                bgColourHover = Color.SKYBLUE;
                bgColourClick = Color.DEEPSKYBLUE;
            } else {
                bgColour      = Color.PINK;
                bgColourHover = Color.LIGHTPINK;
                bgColourClick = Color.TOMATO;
            }
        } else {
            bgColour      = Color.TRANSPARENT;
            bgColourHover = Color.LIGHTGREY;
            bgColourClick = Color.DARKGREY;
        }

        Background bg = makePosnBackground(bgColour);
        b.setBackground(bg);
        b.setOnMouseEntered((MouseEvent e) -> {
            b.setBackground(makePosnBackground(bgColourHover));
        });
        b.setOnMousePressed((MouseEvent e) -> {
            b.setBackground(makePosnBackground(bgColourClick));
        });
        b.setOnMouseReleased((MouseEvent e) -> {
            b.setBackground(makePosnBackground(bgColourHover));
        });
        b.setOnMouseExited((MouseEvent e) -> {
            b.setBackground(bg);
        });
    }


    /**
     * @param   c   the colour of the background
     * @return  a background with no corner radii or insets
     */
    private Background makePosnBackground(Color c){
        return new Background(new BackgroundFill(
                c, CornerRadii.EMPTY, Insets.EMPTY
        ));
    }


    /** @return a grey border for the game board */
    private Border makeBorder(){
        return new Border(new BorderStroke(
                Color.GREY,         BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY,  BorderWidths.DEFAULT
        ));
    }


    /**
     * Creates a small icon to represent an improvement on the board.
     *
     * @return a house-shaped Polygon to represent an improvement to a property
     */
    private Polygon makeHouseIcon(){
        Polygon p = new Polygon();
        p.getPoints().addAll(new Double[]{
            1.0, 8.0,       1.0, 4.0,
            0.0, 4.0,       4.5, 0.0,
            9.0, 4.0,       8.0, 4.0,
            8.0, 8.0
        });

        p.setScaleX(1.1);
        p.setScaleY(1.3);
        p.setFill(Color.TRANSPARENT);
        p.setStroke(Color.BLACK);
        return p;
    }


    /**
     * Creates a small icon to represent a player on the board.
     *
     * @param   isPlayerOne true if the player is Player One
     * @return  a circle-shaped StackPane with the colour and name of the player
     */
    private StackPane makePlayerCounter(boolean isPlayerOne){
        Circle circle = new Circle();
        circle.setRadius(12);
        //circle.setStroke(Color.WHITE);
        circle.setFill(isPlayerOne ? Color.CRIMSON : Color.CORNFLOWERBLUE);

        Label playerName = new Label();
        playerName.setText(isPlayerOne ? "P1" : "P2");
        playerName.setTextFill(Color.WHITE);

        StackPane playerCounter = new StackPane();
        playerCounter.getChildren().addAll(circle, playerName);
        return playerCounter;
    }


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
