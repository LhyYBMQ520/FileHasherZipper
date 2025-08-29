import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import java.util.Optional;
import java.io.File;

public class FileHasherZipperGUI extends Application {

    private TextArea logArea;
    private TextField pathField;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("FileHasherZipper GUI");

        Label pathLabel = new Label("选择文件或文件夹：");
        // 路径框
        pathField = new TextField();
        pathField.setPrefWidth(550);

        Button browseBtn = new Button("浏览...");
        browseBtn.setOnAction(e -> chooseFileOrDirectory(primaryStage));

        HBox pathBox = new HBox(10, pathLabel, pathField, browseBtn);

        Button startBtn = new Button("开始压缩");
        startBtn.setOnAction(e -> startCompression());

        // 新建退出按钮
        Button exitBtn = new Button("退出");
        exitBtn.setOnAction(e -> Platform.exit());

        // 用 HBox 放到右下角
        HBox exitBox = new HBox(exitBtn);
        exitBox.setStyle("-fx-alignment: center-right;"); // 右对齐

        // 初始化 logArea
        logArea = new TextArea();
        // 文本区域
        logArea.setEditable(false);
        logArea.setPrefHeight(400);

        // 合并两个 VBox 定义为一个
        VBox root = new VBox(10, pathBox, startBtn, new Label("日志输出："), logArea, exitBox);
        root.setStyle("-fx-padding: 15;");

        // 窗口
        Scene scene = new Scene(root, 800, 500);



        // ===== 拖拽文件/文件夹支持 =====
        scene.setOnDragOver(event -> {
            if (event.getDragboard().hasFiles()) {
                event.acceptTransferModes(javafx.scene.input.TransferMode.COPY);
            }
            event.consume();
        });

        scene.setOnDragDropped(event -> {
            var db = event.getDragboard();
            if (db.hasFiles()) {
                File file = db.getFiles().get(0);
                if (file.isFile()) {
                    logArea.appendText("拖入的是文件: " + file.getAbsolutePath() + "\n");
                } else if (file.isDirectory()) {
                    logArea.appendText("拖入的是文件夹: " + file.getAbsolutePath() + "\n");
                }
                pathField.setText(file.getAbsolutePath());
            }
            event.setDropCompleted(true);
            event.consume();
        });

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // 浏览按钮选择逻辑
    private void chooseFileOrDirectory(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        DirectoryChooser dirChooser = new DirectoryChooser();

        ButtonType fileBtn = new ButtonType("文件");
        ButtonType dirBtn = new ButtonType("文件夹");
        ButtonType cancelBtn = new ButtonType("取消", ButtonBar.ButtonData.CANCEL_CLOSE);

        Alert choiceAlert = new Alert(Alert.AlertType.CONFIRMATION,
                "请选择要压缩的对象类型：", fileBtn, dirBtn, cancelBtn);
        choiceAlert.setHeaderText("文件 OR 文件夹");

        Optional<ButtonType> result = choiceAlert.showAndWait();

        if (result.isPresent()) {
            File chosen = null;
            if (result.get() == fileBtn) {
                chosen = new FileChooser().showOpenDialog(stage);
            } else if (result.get() == dirBtn) {
                chosen = new DirectoryChooser().showDialog(stage);
            }
            if (chosen != null) {
                pathField.setText(chosen.getAbsolutePath());
            }
        }
    }
    private void startCompression() {
        String path = pathField.getText().trim();
        if (path.isEmpty()) {
            showAlert("错误", "请选择文件或文件夹路径！");
            return;
        }

        logArea.clear();

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                FileHasherZipper.run(path, msg ->
                        Platform.runLater(() -> logArea.appendText(msg + "\n"))
                );
                return null;
            }
        };

        new Thread(task).start();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
