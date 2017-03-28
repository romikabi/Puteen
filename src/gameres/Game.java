package gameres;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Observer;

/**
 * Created by romanabuzyarov on 14.02.17.
 */
public class Game {

    private ShatteredGridPane pane;
    private int score;
    private IntegerProperty ip = new SimpleIntegerProperty(score);
    private StringProperty winp = new SimpleStringProperty();

    public void resetScore(){
        score=0;
        ip.setValue(score);
        winp.setValue("");
    }

    public Game(ShatteredGridPane pane, StringProperty scoreprop, StringProperty winprop){
        this.pane=pane;

        scoreprop.bind(ip.asString());
        winprop.bind(winp);
        winp.setValue("");

        Observer moveObserver = (o, arg) -> {
            score++;
            ip.setValue(score);
        };
        pane.addMoveObserver(moveObserver);

        Observer winObserver = (o, arg) -> {
            winp.setValue("Win!");
            pane.clearMoveHandler();
        };
        pane.addWinObserver(winObserver);
    }
}
