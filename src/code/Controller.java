package code;

import code.enums.GameStatus;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;


public class Controller implements EventHandler<ActionEvent> {
    @FXML private Label bombLabel;
    @FXML private AnchorPane pane;
    @FXML private Canvas canvas;
    @FXML private MenuItem beginnerMenuItem;
    @FXML private MenuItem amateurMenuItem;
    @FXML private MenuItem professionalMenuItem;

    private GameField field;
    private Complexity complexity;
    private Alert alert;

    @FXML
    public void initialize() {
        complexity = ComplexityManager.getInstance().getCurrentComplexity();
        field = new GameField(complexity.getRows(), complexity.getColumns(), complexity.getBombs());
        alert = new Alert(Alert.AlertType.CONFIRMATION);

        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());
        canvas.widthProperty().addListener(e->paint());
        canvas.heightProperty().addListener(e->paint());

        bombLabel.setText(String.valueOf(complexity.getBombs()));

        alert.setContentText("Нажмите ОК для того, чтоб начать сначала.");

        pane.setOnMouseClicked(event -> {
            int x = (int) (event.getX()/Cell.SIZE);
            int y = (int) (event.getY()/Cell.SIZE);

            GameStatus status = GameStatus.CREATED;

            if(event.getButton()== MouseButton.PRIMARY){
                status = field.open(x,y);
            } else  if(event.getButton()== MouseButton.SECONDARY){
                field.marked(x,y);
                bombLabel.setText(String.valueOf(complexity.getBombs() - field.getMarkedAmount()));
            } else if(event.getButton() == MouseButton.MIDDLE){
                status = field.smartOpen(x,y);
            }

            if(status == GameStatus.LOSED){
                field.drawLose(x,y, canvas);
            } else {
               paint();
            }
            handleResult(status, complexity);
        });

        professionalMenuItem.setOnAction(this);
        amateurMenuItem.setOnAction(this);
        beginnerMenuItem.setOnAction(this);

    }

    private void paint(){
        field.draw(canvas);
    }

    @Override
    public void handle(ActionEvent event) {
        int indexComplexity;
        if (event.getSource() == beginnerMenuItem) {
            indexComplexity = 0;
        } else if (event.getSource() == amateurMenuItem) {
            indexComplexity = 1;
        } else {
            indexComplexity = 2;
        }
        ComplexityManager.getInstance().setCurrentComplexity(indexComplexity);

        try {
            reloadGameStage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void reloadGameStage() throws IOException {
        Stage stage = (Stage)  pane.getScene().getWindow();
        stage.hide();
        Parent root = FXMLLoader.load(getClass().getResource("game.fxml"));
        Complexity complexity = ComplexityManager.getInstance().getCurrentComplexity();
        stage.setScene(new Scene(root, complexity.getColumns()*Cell.SIZE-10,
                complexity.getRows()* Cell.SIZE+82));
        stage.show();
    }

    private void handleResult(GameStatus status, Complexity complexity) {
        if(status == GameStatus.LOSED){
            alert.setTitle("Поражение");
            alert.setHeaderText("Вы не справились на уровне \"" + complexity.getTitle() + "\".");
        } else if(status == GameStatus.WON){
            alert.setTitle("Победа");
            alert.setHeaderText("Вы прошли игру на уровне \"" + complexity.getTitle() + "\".");
        } else return;
        if(alert.showAndWait().get() == ButtonType.OK) {
            field.prepareGame();
            paint();
            bombLabel.setText(String.valueOf(complexity.getBombs()));
        } else pane.setOnMouseClicked(null);
    }



}
