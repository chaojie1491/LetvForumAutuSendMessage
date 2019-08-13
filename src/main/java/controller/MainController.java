package controller;

import cn.hutool.core.util.StrUtil;
import com.inamik.text.tables.GridTable;
import com.inamik.text.tables.SimpleTable;
import com.inamik.text.tables.grid.Border;
import com.inamik.text.tables.grid.Util;
import com.jfoenix.controls.*;
import com.jfoenix.controls.cells.editors.TextFieldEditorBuilder;
import com.jfoenix.controls.cells.editors.base.GenericEditableTreeTableCell;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import db.LetvConfigEntity;
import db.LetvCookieEntity;
import db.LetvLinkEntity;
import db.LetvUserEntity;
import entity.LetvCookieTable;
import entity.LinkItem;
import entity.Status;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import org.controlsfx.control.StatusBar;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.*;
import util.*;

import java.io.IOException;
import java.net.URL;
import java.sql.Statement;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    public BorderPane root;

    @FXML
    public MenuItem cookieMgr;
    @FXML
    public MenuItem configEdit;
    @FXML
    public MenuItem linkMgr;
    @FXML
    public MenuItem exit;
    @FXML
    public MenuItem help;

    private static final String FX_LABEL_FLOAT_TRUE = "-fx-label-float:true;";

    private static Logger logger = LoggerFactory.getLogger(MainController.class);

    private JFXButton scanLink;

    private JFXButton scanUser;

    private JFXButton addFriend;

    private JFXTextField moduleId;

    private StatusBar statusBar;

    private JFXButton statusQuery;

    private ListView<Text> logList;

    private Session session = HibernateUtil.currentSession();

    private SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker();

    /**
     *
     */
    private Task<List<LinkItem>> scanLinkTask;

    private Stage spinnerStage;


    private void initEvent() {
        /**
         * Cookie 管理窗口
         */
        cookieMgr.setOnAction(event -> {
            BorderPane borderPane = new BorderPane();
            ObservableList<LetvCookieTable> observableCookie = FXCollections.observableArrayList();

            //column
            JFXTreeTableColumn<LetvCookieTable, String> key = new JFXTreeTableColumn<>("Key");
            key.setCellValueFactory((TreeTableColumn.CellDataFeatures<LetvCookieTable, String> param) -> {
                if (key.validateValue(param)) {
                    return param.getValue().getValue().cookieKey;
                } else {
                    return key.getComputedValue(param);
                }
            });
            JFXTreeTableColumn<LetvCookieTable, String> value = new JFXTreeTableColumn<>("Value");
            value.setCellValueFactory((TreeTableColumn.CellDataFeatures<LetvCookieTable, String> param) -> {
                if (value.validateValue(param)) {
                    return param.getValue().getValue().cookieValue;
                } else {
                    return value.getComputedValue(param);
                }
            });
            JFXTreeTableColumn<LetvCookieTable, String> options = new JFXTreeTableColumn<>("options");
            options.setCellFactory(new Callback<TreeTableColumn<LetvCookieTable, String>, TreeTableCell<LetvCookieTable, String>>() {
                @Override
                public TreeTableCell<LetvCookieTable, String> call(TreeTableColumn<LetvCookieTable, String> param) {
                    JFXButton button = new JFXButton();
                    button.setText("删除");
                    return new TreeTableCell<LetvCookieTable, String>() {
                        JFXButton delBtn = button;

                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                                setText(null);
                            } else {
                                delBtn.setOnMouseClicked(event -> {
                                    Transaction transaction = session.beginTransaction();
                                    logger.info("删除:" + getIndex());
                                    LetvCookieTable table = getTreeTableView().getTreeItem(getIndex()).getValue();
                                    session.doWork(connection -> {
                                        Statement st;
                                        logger.info("id:" + table.cookieId);
                                        String sql = "delete from letv_cookie where cookie_id = " + table.cookieId;
                                        st = connection.createStatement();
                                        st.executeUpdate(sql);
                                        st.close();
                                    });
                                    NotificationUtil.notification("信息", "删除成功", "info");
                                    transaction.commit();
                                    observableCookie.remove(getIndex());
                                });
                                setGraphic(button);
                                setText(null);
                            }
                        }
                    };
                }
            });

            key.setCellFactory((TreeTableColumn<LetvCookieTable, String> param) -> new GenericEditableTreeTableCell<>(
                    new TextFieldEditorBuilder()));
            key.setOnEditCommit((TreeTableColumn.CellEditEvent<LetvCookieTable, String> t) -> {
                LetvCookieTable table = t.getTreeTableView().getTreeItem(t.getTreeTablePosition()
                        .getRow())
                        .getValue();
                table.cookieKey.set(t.getNewValue());
                Transaction transaction = session.beginTransaction();
                Query updateLink = session.createQuery("update db.LetvCookieEntity set cookieKey = :newVal where cookieId=" + table.cookieId);
                updateLink.setParameter("newVal", t.getNewValue());
                updateLink.executeUpdate();
                transaction.commit();
                session.clear();
                NotificationUtil.notification("信息", "更新成功！", "info");

            });

            value.setCellFactory((TreeTableColumn<LetvCookieTable, String> param) -> new GenericEditableTreeTableCell<>(
                    new TextFieldEditorBuilder()));
            value.setOnEditCommit((TreeTableColumn.CellEditEvent<LetvCookieTable, String> t) -> t.getTreeTableView()
                    .getTreeItem(t.getTreeTablePosition()
                            .getRow())
                    .getValue().cookieValue.set(t.getNewValue()));


            List<LetvCookieEntity> letvCookieEntities = session.createQuery("from db.LetvCookieEntity").list();
            letvCookieEntities.forEach(cookie -> {
                LetvCookieTable tableVal = new LetvCookieTable(cookie.getCookieId(), cookie.getCookieKey(), cookie.getCookieValue());
                observableCookie.add(tableVal);
            });
            key.setStyle("-fx-alignment: CENTER;");
            value.setStyle("-fx-alignment: CENTER;");
            options.setStyle("-fx-alignment: CENTER;");
            final TreeItem<LetvCookieTable> root = new RecursiveTreeItem<>(observableCookie, RecursiveTreeObject::getChildren);
            //table
            JFXTreeTableView<LetvCookieTable> treeView = new JFXTreeTableView<>(root);
            treeView.setShowRoot(false);
            treeView.setEditable(true);
            treeView.setColumnResizePolicy(JFXTreeTableView.CONSTRAINED_RESIZE_POLICY);
            treeView.getColumns().setAll(key, value, options);
            //Table vbox
            VBox tableBox = new VBox();
            tableBox.getChildren().add(treeView);
            VBox.setVgrow(treeView, Priority.ALWAYS);
            tableBox.setPrefSize(borderPane.getPrefWidth(), borderPane.getPrefHeight());
            borderPane.setCenter(tableBox);
            //head
            HBox head = new HBox();
            JFXTextField keyText = new JFXTextField();
            keyText.setPromptText("CookieKey");
            keyText.setFocusColor(Color.BLACK);
            JFXTextField valueText = new JFXTextField();
            valueText.setPromptText("CookieValue");
            valueText.setFocusColor(Color.BLACK);
            JFXButton submit = new JFXButton();
            submit.setText("添加");
            submit.setPrefWidth(80);
            submit.getStyleClass().addAll("button-raised");
            //submit event
            submit.setOnMouseClicked(add -> {
                if (StrUtil.isEmpty(keyText.getText()) || StrUtil.isEmpty(value.getText())) {
                    NotificationUtil.notification("错误", "value is not null！", "error");
                } else {
                    Transaction transaction = session.beginTransaction();
                    LetvCookieEntity cookieEntity = new LetvCookieEntity();
                    cookieEntity.setCookieKey(keyText.getText());
                    cookieEntity.setCookieValue(valueText.getText());
                    cookieEntity.setCookieId(snowflakeIdWorker.nextId());
                    session.save(cookieEntity);
                    transaction.commit();
                    LetvCookieTable table = new LetvCookieTable(cookieEntity.getCookieId(), keyText.getText(), valueText.getText());
                    observableCookie.add(table);
                }
            });

            keyText.setPadding(new Insets(0, 10, 0, 10));
            valueText.setPadding(new Insets(0, 10, 0, 10));
            head.setPadding(new Insets(0, 10, 0, 10));
            head.setPrefHeight(100);
            head.setAlignment(Pos.CENTER);
            HBox.setHgrow(keyText, Priority.ALWAYS);
            HBox.setHgrow(valueText, Priority.ALWAYS);
            head.getChildren().addAll(keyText, valueText, submit);
            borderPane.setTop(head);
            //stage
            Stage primaryStage = new Stage();
            primaryStage.setTitle("Cookie管理");
            CustomJFXDecorator decorator = new CustomJFXDecorator(primaryStage, borderPane);
            Scene scene = new Scene(decorator, 475, 500);
            scene.getStylesheets().add(MainController.class.getResource("/css/Components.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.show();
        });

        //消息模板管理
        configEdit.setOnAction(event -> {
            BorderPane borderPane = new BorderPane();
            JFXTextArea content = new JFXTextArea();
            List<LetvConfigEntity> configEntitys = session.createQuery("from db.LetvConfigEntity").list();
            if (configEntitys.size() == 0) {
                content.setText("无内容");
            } else {
                logger.info("OldData:" + configEntitys.get(0).getConfigMessageText());
                content.setText(configEntitys.get(0).getConfigMessageText());
            }
            content.setPromptText("消息模板");
            content.setLabelFloat(true);
            content.setFocusColor(Color.BLACK);
            content.setPadding(new Insets(20, 10, 0, 10));
            //Vbox
            HBox submitBox = new HBox();
            submitBox.setAlignment(Pos.CENTER);
            submitBox.setPrefHeight(20);
            //submit
            Label subText = new Label("提交");
            subText.setTextFill(Color.WHITE);
            submitBox.getChildren().add(subText);
            borderPane.setCenter(content);
            borderPane.setBottom(submitBox);
            submitBox.getStyleClass().add("button-raised");
            submitBox.setOnMouseClicked(event1 -> {
                if (configEntitys.size() == 0) {
                    Transaction transaction = session.beginTransaction();
                    LetvConfigEntity configEntity = new LetvConfigEntity();
                    configEntity.setConfigId(snowflakeIdWorker.nextId());
                    configEntity.setConfigMessageText(content.getText());
                    session.save(configEntity);
                    transaction.commit();
                    session.clear();
                } else {
                    Transaction transaction = session.beginTransaction();
                    LetvConfigEntity entity = configEntitys.get(0);
                    Query query = session.createQuery("update db.LetvConfigEntity set configMessageText = :text where configId=" + entity.getConfigId());
                    query.setParameter("text", content.getText());
                    query.executeUpdate();
                    transaction.commit();
                    session.clear();
                }
                Stage stage = (Stage) borderPane.getScene().getWindow();
                stage.close();
            });
            Stage messageStage = new Stage();
            messageStage.setTitle("消息管理");
            messageStage.setAlwaysOnTop(true);
            CustomJFXDecorator customJFXDecorator = new CustomJFXDecorator(messageStage, borderPane);
            Scene scene = new Scene(customJFXDecorator, 500, 500);
            scene.getStylesheets().add(MainController.class.getResource("/css/Components.css").toExternalForm());
            messageStage.setScene(scene);
            messageStage.show();
        });
        //链接管理
        linkMgr.setOnAction(event -> {
            BorderPane borderPane = new BorderPane();
            Label label = new Label("暂无");
            borderPane.setCenter(label);
            Stage linkStage = new Stage();
            linkStage.setTitle("链接管理");
            linkStage.setAlwaysOnTop(true);
            CustomJFXDecorator customJFXDecorator = new CustomJFXDecorator(linkStage, borderPane);
            Scene scene = new Scene(customJFXDecorator, 500, 500);
            scene.getStylesheets().add(MainController.class.getResource("/css/Components.css").toExternalForm());
            linkStage.setScene(scene);
            linkStage.show();
        });
        //程序退出
        exit.setOnAction(event -> {
            Platform.exit();
        });
        //扫描链接
        scanLink.setOnMouseClicked(event -> {
            initSpinner();
            if (StrUtil.isEmpty(moduleId.getText())) {
                NotificationUtil.notification("错误", "Id is Not Null！", "error");
            } else {
                try {
                    scanLinkTask = new PostsHandler.Builder()
                            .AscOrDesc(true)
                            .PageCount(new LetvPageCount())
                            .Rule(Global.linkClass)
                            .Mudule(Integer.valueOf(moduleId.getText()))
                            .builder();
                    statusBar.textProperty().bind(scanLinkTask.messageProperty());
                    statusBar.progressProperty().bind(scanLinkTask.progressProperty());
                    new Thread(scanLinkTask).start();
                    scanLink.setDisable(true);
                    scanLinkTask.setOnSucceeded(success -> {
                        scanLink.setDisable(false);
                        statusBar.textProperty().unbind();
                        statusBar.progressProperty().unbind();
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        //扫描用户
        scanUser.setOnMouseClicked(event -> {
            initSpinner();
            statusBar.setDisable(true);
            List<LetvLinkEntity> letvLinkEntities;
            if (!StrUtil.isEmpty(moduleId.getText())) {
                logger.info("开始扫描编号为：" + moduleId.getText() + "的帖子");
                letvLinkEntities = session.createQuery("from db.LetvLinkEntity where letvModuleId=" + moduleId.getText()).list();
            } else {
                logger.info("开始扫描全部连接……");
                letvLinkEntities = session.createQuery("from db.LetvLinkEntity").list();
            }
            Task<Void> scanUserService = new ScanUserService(new LetvFilterUserId(), letvLinkEntities);
            statusBar.textProperty().bind(scanUserService.messageProperty());
            statusBar.progressProperty().bind(scanUserService.progressProperty());
            new Thread(scanUserService).start();
            scanUserService.setOnSucceeded(event1 -> {
                NotificationUtil.notification("信息", "添加成功", "info");
            });
        });

        //啓動加好友
        addFriend.setOnMouseClicked(event -> {
            Task<Void> addFriendTask = new AddFriendService();
            new Thread(addFriendTask).start();
            statusBar.textProperty().bind(addFriendTask.messageProperty());
            statusBar.progressProperty().bind(addFriendTask.progressProperty());
            addFriendTask.setOnSucceeded(s -> {
                NotificationUtil.notification("信息", "添加完毕", "info");
                statusBar.textProperty().unbind();
                statusBar.progressProperty().unbind();
            });
        });

        statusQuery.setOnMouseClicked(event -> {
            Platform.runLater(() -> {
                initSpinner();
                showSpinner();
            });
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    Session session = HibernateUtil.currentSession();
                    List<LetvUserEntity> letvUserEntities = session.createQuery("from db.LetvUserEntity").list();
                    List<LetvLinkEntity> letvLinkEntities = session.createQuery("from db.LetvLinkEntity group by letvModuleId").list();

                    long finish = letvUserEntities.stream().filter(u -> u.getLetvUserStatus().equals(Status.TRUE.getCode())).count();
                    long newUser = letvUserEntities.stream().filter(u -> u.getLetvUserStatus().equals(Status.FALSE.getCode())).count();
                    SimpleTable simpleTable = new SimpleTable();
                    simpleTable.nextRow().nextCell().addLine("总数").nextCell().addLine("已发").nextCell().addLine("未发").nextCell();
                    simpleTable.nextRow()
                            .nextCell().addLine(String.valueOf(letvUserEntities.size()))
                            .nextCell().addLine(String.valueOf(finish)).nextCell().addLine(String.valueOf(newUser));
                    GridTable gridTable = simpleTable.toGrid();
                    gridTable = Border.of(Border.Chars.of('+', '-', '|')).apply(gridTable);

                    SimpleTable moduleTable = new SimpleTable();
                    moduleTable.nextRow().nextCell().addLine("板块");
                    letvLinkEntities.forEach(g -> {
                        moduleTable.nextRow().nextCell().addLine(String.valueOf(g.getLetvModuleId()));
                    });
                    GridTable gModuleTable = moduleTable.toGrid();
                    gModuleTable = Border.of(Border.Chars.of('+', '-', '|')).apply(gModuleTable);

                    logger.info("查询结果：\n" + Util.asString(gridTable) + "\n" + Util.asString(gModuleTable));
                    return null;
                }
            };
            new Thread(task).start();
            task.setOnSucceeded(event1 -> {
                closeSpinner(() -> NotificationUtil.notification("信息", "查询成功", "info"));
            });
        });

        help.setOnAction(event -> {
            Stage stage = new Stage();
            VBox vBox = new VBox();
            vBox.setPrefSize(500, 500);
            Text text = new Text();
            text.setText("扫描用户时要输入模块Id否则会扫描所有链接，极为耗时。");
            text.setFont(Font.font("System", 15));
            vBox.setPadding(new Insets(10, 10, 10, 10));
            vBox.getChildren().add(text);
            CustomJFXDecorator customJFXDecorator = new CustomJFXDecorator(stage, vBox);
            Scene scene = new Scene(customJFXDecorator, 500, 500);
            scene.getStylesheets().add(MainController.class.getResource("/css/Components.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("帮助");
            stage.show();
        });
    }

    private void initForm() {
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.TOP_CENTER);
        moduleId = new JFXTextField();
        moduleId.setStyle(FX_LABEL_FLOAT_TRUE);
        moduleId.setPromptText("版块ID");
        moduleId.setLabelFloat(true);
        moduleId.setFocusColor(Paint.valueOf("#101011"));
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().add(moduleId);
        scanLink = new JFXButton();
        scanLink.setText("扫描链接");
        scanLink.getStyleClass().add("button-raised");
        scanUser = new JFXButton();
        scanUser.setText("扫描用户");
        scanUser.getStyleClass().add("button-raised");
        addFriend = new JFXButton();
        addFriend.setText("启动");
        addFriend.getStyleClass().add("button-raised");
        statusQuery = new JFXButton();
        statusQuery.setText("状态查询");
        statusQuery.getStyleClass().add("button-query");
        hBox.getChildren().addAll(scanLink, scanUser, addFriend, statusQuery);
        HBox.setHgrow(moduleId, Priority.ALWAYS);
        HBox.setMargin(scanLink, new Insets(0, 0, 0, 5));
        HBox.setMargin(scanUser, new Insets(0, 0, 0, 5));
        HBox.setMargin(addFriend, new Insets(0, 0, 0, 5));
        HBox.setMargin(statusQuery, new Insets(0, 0, 0, 5));
        VBox.setMargin(hBox, new Insets(20, 10, 20, 10));
        moduleId.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                logger.info("OK");
            }
        });
        statusBar = new StatusBar();
        statusBar.setText("未启动");
        logList = new ListView<>();
        logList.getStylesheets().add(JFoenixResources.load("/css/LogCss.css").toExternalForm());
        Text text = new Text("日志");
        text.setFill(Color.WHITE);

        logList.getItems().add(text);
        VBox.setVgrow(logList, Priority.ALWAYS);
        VBox.setMargin(statusBar, new Insets(20, 0, 0, 0));
        vBox.getChildren().addAll(hBox, statusBar, logList);
        root.setCenter(vBox);
    }

    private void initCss() {
        root.getStylesheets().add(JFoenixResources.load("/css/Components.css").toExternalForm());
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initForm();
        initCss();
        initEvent();
        initLog();
        checkTable();
    }

    private void checkTable() {
        SimpleTable cookieTable = SimpleTable.of();
        List<LetvCookieEntity> cookieEntities = session.createQuery("from db.LetvCookieEntity").list();
        cookieTable.nextRow().nextCell().addLine("CookieKey").nextCell().addLine("CookeValue");
        cookieEntities.forEach(c -> {
            cookieTable.nextRow().nextCell().addLine(c.getCookieKey()).nextCell().addLine(c.getCookieValue());
        });
        GridTable g = cookieTable.toGrid();
        g = Border.of(Border.Chars.of('+', '-', '|')).apply(g);

//        SimpleTable configTable = SimpleTable.of();
//        configTable.nextRow().nextCell().addLine("MessageText");
//        List<LetvConfigEntity> letvConfigEntities = session.createQuery("from db.LetvConfigEntity").list();
//        configTable.nextRow().nextCell().addLine(letvConfigEntities.get(0).getConfigMessageText());
//        GridTable cg = configTable.toGrid();
//        cg = Border.of(Border.Chars.of('+', '-', '|')).apply(cg);
        logger.info("\n" + Util.asString(g));
//        logger.info("\n" + Util.asString(cg));
    }

    private void initLog() {
        try {
            new Thread(new LogReader(logList)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void initSpinner() {
        StackPane spinnerRoot = new StackPane();
        spinnerRoot.getStyleClass().add("register-dialog");
        JFXSpinner first = new JFXSpinner();
        first.getStyleClass().addAll("spinner-black", "first-spinner");
        first.setStartingAngle(-40);
        JFXSpinner second = new JFXSpinner();
        second.getStyleClass().addAll("spinner-dark", "second-spinner");
        second.setStartingAngle(-90);
        JFXSpinner third = new JFXSpinner();
        third.getStyleClass().addAll("spinner-gray", "third-spinner");
        third.setStartingAngle(-120);
        spinnerRoot.getChildren().addAll(first, second, third);

        spinnerStage = new Stage(StageStyle.TRANSPARENT);
        spinnerStage.initModality(Modality.APPLICATION_MODAL);
        Scene scene = new Scene(spinnerRoot, Color.TRANSPARENT);
        scene.getStylesheets().add(MainController.class
                .getResource("/css/register-dialog.css").toExternalForm());
        spinnerStage.initOwner(scanLink.getScene().getWindow());
        spinnerStage.setScene(scene);
    }

    private void showSpinner() {
        Stage primaryStage = (Stage) scanLink.getScene().getWindow();
        spinnerStage.setWidth(primaryStage.getWidth());
        spinnerStage.setHeight(primaryStage.getHeight());
        spinnerStage.setX(primaryStage.getX());
        spinnerStage.setY(primaryStage.getY());
        spinnerStage.show();
    }

    private void closeSpinner(Runnable later) {
        Platform.runLater(() -> {
            spinnerStage.close();
            if (later != null) {
                later.run();
            }
        });
    }

}
