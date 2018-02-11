package code;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("game.fxml"));
        primaryStage.setTitle("Сапер");
        Complexity currentComplexity = ComplexityManager.getInstance().getCurrentComplexity();
        primaryStage.setScene(new Scene(root, currentComplexity.getColumns() * Cell.SIZE - 10,
                currentComplexity.getRows() * Cell.SIZE + 82));
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
