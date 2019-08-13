import controller.PleaseProvideController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.CustomJFXDecorator;
import util.SqliteUtil;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Hello world!
 */
public class App extends Application {


    private Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) throws IOException, InterruptedException {

        if (App.class.getResource("backup/letv.db") == null) {
            getLoadStage(primaryStage).show();
            Platform.runLater(() -> {
                try {
                    SqliteUtil.createDatabases();
                    logger.info("数据库创建成功!");
                    primaryStage.close();
                    getMainStage(new Stage()).show();
                } catch (SQLException | IOException e) {
                    e.printStackTrace();
                }
            });
        } else {
            getMainStage(primaryStage).show();
        }
    }


    private Stage getLoadStage(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Loading.fxml"));
        AnchorPane pane = loader.load();
        Scene scene = new Scene(pane, 500, 300);
        PleaseProvideController loadController = loader.getController();
        loadController.setInfo("正在创建数据库……");
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.getIcons().addAll(new Image("file:/resource/logo/Letv-0.0.1-SNAPSHOT.ico"));
        return primaryStage;
    }


    private Stage getMainStage(Stage primaryStage) throws IOException {
        BorderPane root = FXMLLoader.load(getClass().getResource("/fxml/MainPane.fxml"));
        CustomJFXDecorator decorator = new CustomJFXDecorator(primaryStage, root);
        Scene scene = new Scene(decorator, 600, 400);
        primaryStage.setTitle("Letv");
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image("/image/logo.png"));
        primaryStage.setMinWidth(500);
        primaryStage.setMinHeight(400);
        return primaryStage;
    }
}
