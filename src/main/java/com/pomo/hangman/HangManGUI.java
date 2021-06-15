/*
 * Copyright © 2021 Mr.Po (ldd_live@foxmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pomo.hangman;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

/**
 * <h2>刽子手游戏 界面</h2>
 *
 * <p>依赖{@link HangManCMD}，提供图像显示，与交互。</p>
 *
 * @author Mr.Po, ldd_live@foxmail.com
 */
public final class HangManGUI extends Application {

    private static final CountDownLatch LATCH = new CountDownLatch(1);
    private static HangManGUI INSTANCE;

    private final Rotate rotate = new Rotate(0, 42, 0);

    private final MessageFormat format1 = new MessageFormat("Guess a word: {0}");
    private final MessageFormat format2 = new MessageFormat("Missed letters: {0}");
    private final MessageFormat format3 = new MessageFormat("The word is: {0}");

    private final Label label1 = new Label("Wait for cmd start...");
    private final Label label2 = new Label();
    private final StackPane person = new StackPane();
    private final Listener listener = new Listener();

    private Stage primaryStage;
    private HangManCMD hangManCMD;
    private Timeline timeline1;
    private Timeline timeline2;

    private boolean finish = false;

    @Override
    public void start(Stage primaryStage) {

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: white;");

        drawBackground(root);

        person.getTransforms().add(rotate);

        timeline1 = new Timeline(
                new KeyFrame(Duration.millis(750), new KeyValue(rotate.angleProperty(), 50,
                        Interpolator.EASE_OUT))
        );

        timeline2 = new Timeline(
                new KeyFrame(Duration.millis(1500), new KeyValue(rotate.angleProperty(), -50,
                        Interpolator.EASE_BOTH))
        );

        timeline2.setAutoReverse(true);
        timeline2.setCycleCount(Timeline.INDEFINITE);
        timeline1.setOnFinished(event -> timeline2.play());


        Scene scene = new Scene(root, 800, 600);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Hangman Game");
        primaryStage.setScene(scene);

        primaryStage.setOnCloseRequest(it -> System.exit(0));

        primaryStage.addEventHandler(KeyEvent.ANY, event -> {

            if (hangManCMD != null && event.getEventType() == KeyEvent.KEY_PRESSED) {

                String value = event.getText().trim();

                if (value.length() == 1) {

                    char next = value.charAt(0);
                    hangManCMD.input(next);

                } else if (KeyCode.ENTER == event.getCode() && finish) {

                    hangManCMD.input('y');
                }
            }
        });

        primaryStage.show();

        this.primaryStage = primaryStage;

        INSTANCE = this;
        LATCH.countDown();
    }

    /**
     * 画背景
     *
     * @param root 画板
     */
    private void drawBackground(StackPane root) {

        //弧线
        //前两个参数表示位置
        //紧接着两个参数表示弧线长轴与短轴（可以使用Arc画椭圆）
        //最后两个参数表示弧线的起始角度，与覆盖角度
        Arc arc = new Arc(0, 0, 100, 40, -180, -180);
        arc.setTranslateX(50);
        arc.setTranslateY(-50);
        arc.setFill(Color.TRANSPARENT);//设置填充颜色
        arc.setStroke(Color.BLACK);//设置画笔颜色
        arc.setStrokeWidth(1);//设置画笔宽度
        StackPane.setAlignment(arc, Pos.BOTTOM_LEFT);

        //竖线
        Line line1 = new Line(0, 0, 0, 450);
        line1.setTranslateX(150);
        line1.setTranslateY(-90);
        StackPane.setAlignment(line1, Pos.BOTTOM_LEFT);

        //横线
        Line line2 = new Line(0, 0, 200, 0);
        line2.setTranslateX(150);
        line2.setTranslateY(-540);
        StackPane.setAlignment(line2, Pos.BOTTOM_LEFT);

        VBox label = new VBox(label1, label2);
        label.setMaxSize(480, Control.USE_PREF_SIZE);
        label.setTranslateY(-50);
        label.setTranslateX(120);
        label.setStyle("-fx-font-size: 25");
        StackPane.setAlignment(label, Pos.BOTTOM_CENTER);

        person.setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
        person.setTranslateX(-50);
        person.setTranslateY(60);
        StackPane.setAlignment(person, Pos.TOP_CENTER);

        root.getChildren().addAll(arc, line1, line2, person, label);
    }

    /**
     * 得到小人的部分节点
     *
     * @param count 错误次数
     * @return 节点
     */
    private Node getPartNode(int count) {

        Node node = null;

        switch (count) {

            case 0:

                String url = HangManGUI.class.getResource("face.png").toExternalForm();
                Image image = new Image(url, 50, 60, true, true);
                node = new ImageView(image);
                node.setTranslateY(30);

                break;

            case 1:

                node = new Line(0, 0, 0, 50);

                break;

            case 2:

                Arc arc = new Arc(0, 0, 40, 40, 360, 360);
                arc.setFill(Color.TRANSPARENT);//设置填充颜色
                arc.setStroke(Color.BLACK);//设置画笔颜色
                node = arc;

                node.setTranslateY(50);

                break;

            case 3:

                node = new Line(0, 0, -70, 70);

                node.setTranslateY(110);
                node.setTranslateX(-70);

                break;

            case 4:

                node = new Line(0, 0, 70, 70);

                node.setTranslateY(110);
                node.setTranslateX(70);

                break;

            case 5:

                node = new Line(0, 0, 0, 120);

                node.setTranslateY(130);

                break;

            case 6:

                node = new Line(0, 0, -70, 70);

                node.setTranslateY(250);
                node.setTranslateX(-35);

                break;

            case 7:

                node = new Line(0, 0, 70, 70);

                node.setTranslateY(250);
                node.setTranslateX(35);

                break;
        }

        return node;
    }

    /**
     * <h2>监听</h2>
     *
     * <p>内部类，用于外部类操作本类。</p>
     *
     * @author Mr.Po, ldd_live@foxmail.com
     */
    public final class Listener {

        public void onReset(String mask) {

            timeline1.stop();
            timeline2.stop();

            Platform.runLater(() -> {

                finish = false;

                rotate.setAngle(0);
                person.getChildren().clear();

                label1.setText(format1.format(new Object[]{mask}));
                label2.setVisible(false);
            });
        }

        public void onError(int count, String mask, char next) {

            Node node = getPartNode(count);

            StackPane.setAlignment(node, Pos.TOP_CENTER);

            Platform.runLater(() -> {

                person.getChildren().add(node);

                label1.setText(format1.format(new Object[]{mask}));
                label2.setText(format2.format(new Object[]{next}));
                label2.setVisible(true);

                if (count == 7) {

                    person.getChildren().add(getPartNode(0));

                    timeline1.play();
                }
            });
        }

        public void onValid(String mask) {

            Platform.runLater(() -> {

                label1.setText(format1.format(new Object[]{mask}));
                label2.setVisible(false);
            });
        }

        public void onFinish(String word) {

            Platform.runLater(() -> {

                finish = true;

                label1.setText(format3.format(new Object[]{word}));
                label2.setText("To continue the game, press Enter.");
                label2.setVisible(true);
            });
        }

        public void onDestroy() {
            Platform.runLater(() -> primaryStage.close());
        }
    }

    /**
     * <h2>启动gui</h2>
     *
     * @param hangManCMD 刽子手命令行
     * @return 监听
     */
    public static Listener start(HangManCMD hangManCMD) throws InterruptedException {

        if (INSTANCE != null) return INSTANCE.listener;

        new Thread(() -> Application.launch(HangManGUI.class)).start();

        LATCH.await();

        Objects.requireNonNull(INSTANCE);

        INSTANCE.hangManCMD = hangManCMD;

        return INSTANCE.listener;
    }

}
