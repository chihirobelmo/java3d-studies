package com.example;

import javafx.application.Application;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    public static MeshView createCustomMesh() {
        // カスタムメッシュを作成
        TriangleMesh mesh = new TriangleMesh();

        // 頂点を定義 (x, y, z)
        mesh.getPoints().addAll(
            -50, +0, -50,   // 頂点1 (底面左前)
            +50, +0, -50,   // 頂点2 (底面右前)
            +50, +0, +50,   // 頂点3 (底面右後)
            -50, +0, +50,   // 頂点4 (底面左後)
            +0, -100, +0    // 頂点5 (頂上)
        );

        // UV座標を定義 (u, v)
        mesh.getTexCoords().addAll(
            0.5f, 0.0f,      // 頂上
            0.0f, 1.0f,      // 左下
            1.0f, 1.0f       // 右下
        );

        // 面を定義 (頂点インデックス, UVインデックス)
        mesh.getFaces().addAll(
            0, 1, 1, 2, 4, 0, // 面1: 頂点1, 頂点2, 頂点5
            1, 1, 2, 2, 4, 0, // 面2: 頂点2, 頂点3, 頂点5
            2, 1, 3, 2, 4, 0, // 面3: 頂点3, 頂点4, 頂点5
            3, 1, 0, 2, 4, 0  // 面4: 頂点4, 頂点1, 頂点5
        );

        // メッシュビューを作成
        MeshView meshView = new MeshView(mesh);

        // マテリアルを設定
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseMap(new Image("file:demo/src/main/java/resources/diffuse.jpg"));
        material.setSpecularMap(new Image("file:demo/src/main/java/resources/rough.jpg"));
        material.setBumpMap(new Image("file:demo/src/main/java/resources/normal.jpg"));
        meshView.setMaterial(material);

        return meshView;
    }
    
    public void render(Stage primaryStage) {
        // Create a 3D box
        MeshView pyramid = createCustomMesh(); // Define dimensions of the box

        Affine affine = new Affine();
        affine.appendRotation(30, new Point3D(0.0, 0.0, 0.0), Rotate.X_AXIS); // Rotate 30 degrees around the X-axis
        affine.appendRotation(30, new Point3D(0.0, 0.0, 0.0), Rotate.Y_AXIS); // Rotate 30 degrees around the Y-axis
        pyramid.getTransforms().add(affine); // Add the affine transformation to the box

        // Create a group to hold the 3D objects
        Group root = new Group();
        root.getChildren().add(pyramid);

        // Directional Light
        PointLight light = new PointLight(Color.WHITE);
        light.setTranslateX(-100); // Set the light position
        light.setTranslateY(-100); // Set the light position
        light.setTranslateZ(-100); // Set the light position
        root.getChildren().add(light); // Add the light to the group

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