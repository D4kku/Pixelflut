package de.uulm.in.vs.grn.p5.imageUtils;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

public class ImageUtils extends Application {

    private Path imagePath;
    private InputStream inputStream;
    private final int sceneHight = 200;
    private final int sceneWith = 400;


    //TODO: Implement this as a Util Class
    public ImageUtils(){}
    @Override
    public void start(Stage stage){
        displayImageDebugger(stage);
    }

    public static void main(String[] args) { launch(); }

    public void getImageStream(Path path) throws IOException{
        inputStream = Files.newInputStream(path);
    }
    //there has to be a better way omg i hate java sometimes
    public Image scaleImage(int targetWidth,int targetHeight) throws IOException {
        BufferedImage bufferedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.drawImage(ImageIO.read(inputStream),0,0,targetWidth,targetHeight,null);
        graphics2D.dispose();
        //wtf am i doing TODO: think of a better way to do this omg
        return new Image(new ByteArrayInputStream(((DataBufferByte)(bufferedImage).getRaster().getDataBuffer()).getData()));

    }

    public void displayPathPopUp(Stage stage){
        FileChooser fileChooser = new FileChooser();
        this.imagePath =  fileChooser.showOpenDialog(stage).toPath();
    }
    //only used to debug what the Processed images looks like for debugging puroposes
    public void displayImageDebugger(Stage stage){
        HBox hbox = new HBox();
        VBox vBox = new VBox(hbox);
        Label imageLabel = new Label("Choose your Image: ");
        Button selectImageButton = new Button("Select Image");


        EventHandler<ActionEvent> selectImageButtonEvent =  new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                displayPathPopUp(stage);
                try {
                    getImageStream(imagePath); //i use so many global variables since this function wont work any other way
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                ImageView imageView = new ImageView();
                imageView.setFitWidth(128);
                imageView.setFitHeight(128);
                vBox.getChildren().add(imageView);
            }
        };

        selectImageButton.setOnAction(selectImageButtonEvent);
        hbox.getChildren().addAll(imageLabel, selectImageButton);
        hbox.setAlignment(Pos.TOP_CENTER);
        vBox.setAlignment(Pos.CENTER);
        Scene scene = new Scene(vBox, sceneWith,sceneHight);
        stage.setScene(scene);
        stage.show();
    }


}
