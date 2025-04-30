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
        // Create a 3D box
        Box box = new Box(100, 100, 100); // Define dimensions of the box
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(Color.BLUE); // Set the diffuse color of the material
        material.setSpecularColor(Color.LIGHTBLUE); // Set the specular color of the material
        box.setMaterial(material);

        // Apply rotations to the box
        box.getTransforms().add(new Rotate(30, Rotate.X_AXIS));
        box.getTransforms().add(new Rotate(30, Rotate.Y_AXIS));

        // Create a group to hold the 3D objects
        Group root = new Group();
        root.getChildren().add(box);

        // Create a perspective camera
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-500); // Position the camera

        // Set the near and far clipping planes
        camera.setNearClip(0.1); // Set the near clipping plane
        camera.setFarClip(1000.0); // Set the far clipping plane

        // Create a scene with 3D support
        Scene scene = new Scene(root, 800, 600, true);
        scene.setFill(Color.GRAY); // Set the background color of the scene
        scene.setCamera(camera);

        // Set up the stage
        primaryStage.setTitle("JavaFX 3D Example");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void start(Stage stage) throws IOException {
        // Initialize the 3D rendering
        render(stage); // Render the 3D scene
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