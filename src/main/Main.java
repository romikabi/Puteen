package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class Main extends Application {
    private static Stage _primaryStage;

    public static Stage getPrimaryStage() {
        return _primaryStage;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        // Setting dock icon on mac.
//        try {
//            URL iconURL = Main.class.getResource("/res/four-squares.png");
//            Image image = new ImageIcon(iconURL).getImage();
//            com.apple.eawt.Application.getApplication().setDockIconImage(image);
//        }
//        // Windows and linux isn't supported
//        catch (Exception e){}

        _primaryStage = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("mainpage.fxml"));
        primaryStage.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream("/res/four-squares.png")));

        primaryStage.setScene(new Scene(root, 600, 658));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
