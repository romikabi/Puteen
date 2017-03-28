package main;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import gameres.Game;
import gameres.ShatteredGridPane;

import java.io.File;

public class MainpageController {
    public ShatteredGridPane shatteredGridPane;
    public GridPane root;
    public Label scoreLabel;
    public ToolBar toolBar;
    public Label winLabel;

    private Image image;

    private Game game;

    public void openHandler(ActionEvent actionEvent) {
        FileChooser chooser = new FileChooser();

        chooser.setSelectedExtensionFilter(
                new FileChooser.ExtensionFilter(
                        "Images",
                        "*.bmp", "*.gif", "*.jpg", "*.jpeg", "*.png"));

        File im = chooser.showOpenDialog(Main.getPrimaryStage());
        if (im == null) return;

        image = new Image("file:" + im.getAbsolutePath(),
                root.getWidth(),
                root.getWidth(),
                false,
                true);
        shatteredGridPane.initialize(image, 4);

        game = new Game(shatteredGridPane, scoreLabel.textProperty(), winLabel.textProperty());
    }

    public void shuffleHandler(ActionEvent actionEvent) {
        game.resetScore();
        shatteredGridPane.initialize(image, 4);
    }

    public void exitHandler(ActionEvent actionEvent) {
        Main.getPrimaryStage().close();
    }
}
