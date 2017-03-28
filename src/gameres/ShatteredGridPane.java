package gameres;

import javafx.animation.PathTransition;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by romanabuzyarov on 11.02.17.
 */
public class ShatteredGridPane extends GridPane {

    public class MyObservable extends Observable {
        public void setCh() {
            setChanged();
        }
    }

    private MyObservable moveObs = new MyObservable();
    private MyObservable winObs = new MyObservable();

    public void addMoveObserver(Observer obs) {
        moveObs.addObserver(obs);
    }

    public void addWinObserver(Observer obs) {
        winObs.addObserver(obs);
    }

    public ShatteredGridPane() {
    }

    public ShatteredGridPane(Image source) {
        this(source, 4);
    }

    public void initialize(Image source, int side) {
        this.getChildren().clear();
        this.getRowConstraints().clear();
        this.getColumnConstraints().clear();

        this.setHgap(1);
        this.setVgap(1);

        PixelReader bmap = source.getPixelReader();
        double sideLength = source.getHeight() / side;

        for (int i = 0; i < side; i++) {
            this.addRow(i);
            this.addColumn(i);
            for (int j = 0; j < side; j++) {
                WritableImage wi = new WritableImage(
                        bmap,
                        (int) sideLength * j,
                        (int) sideLength * i,
                        (int) sideLength,
                        (int) sideLength);

                ImageView imageView = new ImageView(wi);
                imageView.setPreserveRatio(true);
                if (i == side - 1 && j == side - 1) {
                    imageView = new ImageView();
                    imageView.setPreserveRatio(true);
                    imageView.setFitHeight(sideLength - 1);
                    imageView.setId("empty");
                } else
                    imageView.setId(Integer.toString(i) + " " + Integer.toString(j));

                imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onImageViewClick);
                GridPane.setConstraints(imageView, j, i);
                this.getChildren().add(imageView);
            }
        }
        shuffle();
    }

    // consume clicks after winning
    public void clearMoveHandler(){
        getChildren().forEach(node->node.addEventFilter(MouseEvent.MOUSE_CLICKED, Event::consume));
    }

    public ShatteredGridPane(Image source, int side) {
        initialize(source, side);
    }

    private void onImageViewClick(MouseEvent e) {
        Node view = (Node) e.getSource();

        int aRow = GridPane.getRowIndex(view);
        int aCol = GridPane.getColumnIndex(view);

        Node other = null;
        for (int row = -1; row <= 1; row++) {
            for (int col = -1; col <= 1; col++) {
                if (Math.abs(row - col) != 1) continue;

                Node temp = findImageView(aRow + row, aCol + col);

                if (temp != null && temp.getId().equals("empty")) {
                    other = temp;
                    break;
                }
            }
            if (other != null) break;
        }

        if (other != null) {
            swapIdentities((ImageView) view, (ImageView) other); //поменено
            moveObs.setCh();
            moveObs.notifyObservers();
            if (checkWin()) { //прочекано
                winObs.setCh();
                winObs.notifyObservers();
            }
        }
    }

    private Node findImageView(int row, int column) {
        ArrayList<String> temp = new ArrayList<>();
        for (Node node : this.getChildren()) {
            temp.add(GridPane.getRowIndex(node) + " " + GridPane.getColumnIndex(node));
            if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column)
                return node;
        }

        return null;
    }

    // animated replacing of imageviews
    private void swapIdentities(ImageView a, ImageView b) {
        int aR = GridPane.getRowIndex(a);
        int aC = GridPane.getColumnIndex(a);

        int bR = GridPane.getRowIndex(b);
        int bC = GridPane.getColumnIndex(b);

        Path aToB = new Path();
        double shift = a.getImage().getWidth()/2;

        ImageView aCopy = new ImageView(a.getImage());
        aCopy.setX((a.getImage().getWidth()+getVgap())*aC);
        aCopy.setY((a.getImage().getHeight()+getHgap())*aR);

        aCopy.setVisible(true);

        ((AnchorPane)getParent()).getChildren().add(aCopy);

        double aX = aCopy.getX()+shift;
        double aY = aCopy.getY()+shift;

        double bX = aX+(bC-aC)*(shift*2+getVgap());
        double bY = aY+(bR-aR)*(shift*2+getHgap());

        aToB.getElements().add(new MoveTo(aX, aY));
        aToB.getElements().add(new LineTo(bX, bY));

        a.setVisible(false);

        PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.millis(100));
        pathTransition.setPath(aToB);
        pathTransition.setNode(aCopy);
        pathTransition.setCycleCount(1);

        pathTransition.setOnFinished(e->{
            a.setVisible(true);
            ((AnchorPane)getParent()).getChildren().remove(1);
        });

        pathTransition.play();
        GridPane.setConstraints(a, bC, bR);
        GridPane.setConstraints(b, aC, aR);

    }

    private boolean checkWin() {
        for (Node node : this.getChildren()) {
            String[] strnums = node.getId().split(" ");
            int targetRow = -1;
            int targetCol = -1;
            if (!node.getId().equals("empty")) {
                targetRow = Integer.parseInt(strnums[0]);
                targetCol = Integer.parseInt(strnums[1]);
            } else {
                targetRow = targetCol = (int) Math.sqrt(getChildren().size()) - 1;
            }

            if (GridPane.getRowIndex(node) != targetRow || GridPane.getColumnIndex(node) != targetCol)
                return false;
        }

        return true;
    }

    public void shuffle() {
        class Pair {
            public int row;
            public int col;

            public Pair(int r, int c) {
                row = r;
                col = c;
            }
        }
        int side = (int) Math.sqrt(this.getChildren().size());

        ArrayList<Pair> places = new ArrayList<Pair>();

        for (int i = 0; i < side; i++) {
            for (int j = 0; j < side; j++) {
                if (i != side - 1 || j != side - 1)
                    places.add(new Pair(i, j));
            }
        }

        Collections.shuffle(places);

        for (int i = 0; i < getChildren().size(); i++) {
            if (!getChildren().get(i).getId().equals("empty")) {
                GridPane.setConstraints(getChildren().get(i), places.get(0).col, places.get(0).row);
                places.remove(0);
            } else GridPane.setConstraints(getChildren().get(i), side - 1, side - 1);
        }
    }
}
