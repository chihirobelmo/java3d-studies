package com.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    
    public void render(Stage primaryStage) {
        // 3D??????????????
        Box box = new Box(100, 100, 100); // ????????
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(Color.BLUE); // ???
        material.setSpecularColor(Color.LIGHTBLUE); // ???
        box.setMaterial(material);

        // ??????
        box.getTransforms().add(new Rotate(30, Rotate.X_AXIS));
        box.getTransforms().add(new Rotate(30, Rotate.Y_AXIS));

        // ???????
        Group root = new Group();
        root.getChildren().add(box);

        // ??????
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-500); // ?????????

        // ??????
        camera.setNearClip(0.1); // ???????????
        camera.setFarClip(1000.0); // ????????????

        // ??????
        Scene scene = new Scene(root, 800, 600, true);
        scene.setFill(Color.GRAY); // ???
        scene.setCamera(camera);

        // ???????
        primaryStage.setTitle("JavaFX 3D Example");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void start(Stage stage) throws IOException {
        // scene = new Scene(loadFXML("primary"), 640, 480);
        // stage.setScene(scene);
        // stage.show();
        render(stage); // 3D?????????
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}