package com.example;

import javafx.application.Application;
import javafx.application.Platform;
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
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * JavaFX App
 */
public class App extends Application {

    /**
     * 指定された直行ベクトルからアフィン変換を作成
     *
     * @param right 右ベクトル (Point3D)
     * @param up 上ベクトル (Point3D)
     * @param forward 前方ベクトル (Point3D)
     * @return アフィン変換 (Affine)
     */
    public static Affine createAffineFromOrthogonalVectors(Point3D right, Point3D up, Point3D forward) {
        // アフィン変換を作成
        Affine affine = new Affine();
    
        // 直行ベクトルを基に回転行列を設定
        affine.setMxx(right.getX());
        affine.setMxy(up.getX());
        affine.setMxz(forward.getX());
    
        affine.setMyx(right.getY());
        affine.setMyy(up.getY());
        affine.setMyz(forward.getY());
    
        affine.setMzx(right.getZ());
        affine.setMzy(up.getZ());
        affine.setMzz(forward.getZ());
    
        return affine;
    }

    /**
     * 指定された方向を向くアフィン変換を作成
     *
     * @param direction 向きベクトル (Point3D)
     * @return アフィン変換 (Affine)
     */
    public static Affine createAffineToFaceDirection(Point3D direction) {

        Point3D right = direction.crossProduct(Rotate.Y_AXIS).normalize();
        Point3D up = right.crossProduct(direction).normalize();
        Point3D forward = direction.normalize();

        return createAffineFromOrthogonalVectors(right, up, forward);
    }

    /**
     * 方位角、仰角、距離を指定して3D座標を計算
     *
     * @param azimuth 方位角 (度単位, 0°は+X軸方向、時計回り)
     * @param elevation 仰角 (度単位, 0°はXY平面、+90°は+Z軸方向)
     * @param radius 距離
     * @return 計算された3D座標 (Point3D)
     */
    public static Point3D calculatePoint(double azimuth, double elevation, double radius) {
        // 度をラジアンに変換
        double azimuthRad = Math.toRadians(azimuth);
        double elevationRad = Math.toRadians(elevation);

        // 球面座標からデカルト座標を計算
        double x = radius * Math.cos(elevationRad) * Math.cos(azimuthRad);
        double z = radius * Math.cos(elevationRad) * Math.sin(azimuthRad);
        double y = radius * Math.sin(elevationRad);

        return new Point3D(x, y, z);
    }

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
            3, 1, 0, 2, 4, 0, // 面4: 頂点4, 頂点1, 頂点5
            0, 1, 3, 2, 1, 0, // 面5: 頂点1, 頂点2, 頂点3
            3, 1, 2, 2, 1, 0  // 面6: 頂点1, 頂点3, 頂点4
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

    private double cameraPosAzimuth = 270.0; // カメラの方位角
    private double cameraPosElevation = 0.0; // カメラの仰角
    private double cameraPosRadius = 300.0; // カメラの距離
    private Point3D cameraPos; // カメラの位置

    private double lastMousePosX = 0.0; // マウスのX座標
    private double lastMousePosY = 0.0; // マウスのY座標

    private ScheduledExecutorService executorService; // スケジュール実行サービス
    private boolean isMousePressed = false; // マウスが押されているかどうか
    
    public void render(Stage primaryStage) {
        // Create a 3D box
        MeshView pyramid = createCustomMesh(); // Define dimensions of the box
        pyramid.setTranslateY(25);

        // Create a group to hold the 3D objects
        Group root = new Group();
        root.getChildren().add(pyramid);

        // Directional Light
        PointLight light = new PointLight(Color.WHITE);
        light.setTranslateX(-65535); // Set the light position
        light.setTranslateY(-65535); // Set the light position
        light.setTranslateZ(-65535); // Set the light position
        root.getChildren().add(light); // Add the light to the group

        // Create a perspective camera
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-300); // Position the camera
        camera.setNearClip(0.1); // Set the near clipping plane
        camera.setFarClip(1000.0); // Set the far clipping plane

        Runnable setCameraPosition = () -> {
            // Calculate the camera position based on azimuth, elevation, and radius
            cameraPos = calculatePoint(cameraPosAzimuth, cameraPosElevation, cameraPosRadius);
            camera.setTranslateX(cameraPos.getX()); // Set new camera position
            camera.setTranslateY(cameraPos.getY()); // Set new camera position
            camera.setTranslateZ(cameraPos.getZ()); // Set new camera position
            camera.getTransforms().clear(); // Clear previous transforms
            Affine affineToCamera = createAffineToFaceDirection(cameraPos); 
            affineToCamera.append(new Scale(1.0, 1.0, -1.0)); // Scale the camera
            camera.getTransforms().add(affineToCamera); // Add new transform to the camera
        };
        setCameraPosition.run(); // Set the initial camera position

        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> {
            Platform.runLater(() -> {
                setCameraPosition.run();
            });
        }, 0, 16, java.util.concurrent.TimeUnit.MILLISECONDS); // Schedule the task

        // Create a scene with 3D support
        Scene scene = new Scene(root, 800, 600, true);
        scene.setFill(Color.GRAY); // Set the background color of the scene
        scene.setCamera(camera);

        scene.setOnMouseDragged(e -> {
            // Update camera position based on mouse drag
            if (isMousePressed) {
                cameraPosAzimuth += (lastMousePosX - e.getSceneX()); // Update azimuth based on mouse movement
                cameraPosElevation += (lastMousePosY - e.getSceneY()); // Update elevation based on mouse movement
                lastMousePosX = e.getSceneX(); // Store the last mouse X position
                lastMousePosY = e.getSceneY(); // Store the last mouse Y position
            }
            else {
                lastMousePosX = e.getSceneX(); // Store the last mouse X position
                lastMousePosY = e.getSceneY(); // Store the last mouse Y position
                isMousePressed = true; // Set mouse pressed state
            }
        });

        scene.setOnMouseReleased(e -> {
            // Reset mouse pressed state when released
            isMousePressed = false; // Reset mouse pressed state
        });

        scene.setOnScroll(e -> {
            // Update camera radius based on scroll
            cameraPosRadius += e.getDeltaY() / 10; // Adjust the camera radius based on scroll
        });

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

    @Override
    public void stop() throws Exception {
        // Cleanup resources if needed
        super.stop(); // Call the superclass method
        executorService.shutdown(); // Shutdown the executor service
    }

    public static void main(String[] args) {
        launch();
    }

}