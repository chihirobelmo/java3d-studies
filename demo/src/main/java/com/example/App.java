package com.example;

import javafx.application.Application;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {
    
    public void render(Stage primaryStage) {
        // Create a 3D box
        Box box = new Box(100, 100, 100); // Define dimensions of the box
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(Color.BLUE); // Set the diffuse color of the material
        material.setSpecularColor(Color.LIGHTBLUE); // Set the specular color of the material
        box.setMaterial(material);

        Affine affine = new Affine();
        affine.appendRotation(30, new Point3D(0.0, 0.0, 0.0), Rotate.X_AXIS); // Rotate 30 degrees around the X-axis
        affine.appendRotation(30, new Point3D(0.0, 0.0, 0.0), Rotate.Y_AXIS); // Rotate 30 degrees around the Y-axis
        box.getTransforms().add(affine); // Add the affine transformation to the box

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

    public static void main(String[] args) {
        launch();
    }

}