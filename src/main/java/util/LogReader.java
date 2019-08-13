package util;

import javafx.application.Platform;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;


public class LogReader implements Runnable {

    private ListView<Text> logView;
    private File logFile = null;
    private long lastTimeFileSize = 0; // 上次文件大小

    public LogReader(ListView<Text> logView) throws IOException {
        File projectPath = new File("");
        String filePath = projectPath.getCanonicalPath();
        logFile = new File(filePath + "\\" + "logs/letv.log");
        this.logView = logView;
        this.lastTimeFileSize = logFile.length();
    }

    /**
     * 实时输出日志信息
     */
    public void run() {
        while (true) {
            try {
                long len = logFile.length();
                if (len < lastTimeFileSize) {
                    lastTimeFileSize = len;
                } else if (len > lastTimeFileSize) {
                    RandomAccessFile randomFile = new RandomAccessFile(logFile, "r");
                    randomFile.seek(lastTimeFileSize);
                    String tmp = null;
                    while ((tmp = randomFile.readLine()) != null) {
                        tmp = new String(tmp.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                        Text text = new Text(tmp);
                        text.setFill(Color.WHITE);
                        text.setFont(Font.font("System",15));
                        Platform.runLater(() -> {
                            logView.getItems().add(text);
                        });

                    }
                    lastTimeFileSize = randomFile.length();
                    randomFile.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}




