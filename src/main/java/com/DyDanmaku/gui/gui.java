/*
 * Created by JFormDesigner on Tue Dec 24 18:40:39 HKT 2024
 */

package com.DyDanmaku.gui;

import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import javax.imageio.ImageIO;
import javax.swing.*;

import com.DyDanmaku.DYSign;
import com.DyDanmaku.DyDanmakuRequest;
import com.DyDanmaku.Main;
import com.DyDanmaku.WebSocketClient;
import net.miginfocom.swing.*;

/**
 * @author tiangalon
 */
public class gui extends JFrame {

    WebSocketClient listener = new WebSocketClient(this);


    public gui() {
        initComponents();
    }

    private void RoomConnect(ActionEvent e){
        if(ConnectButton.getText().equals("断开")){
            ConnectButton.setEnabled(false);
            ConnectButton.setText("断开中...");
            listener.close();
            DanmakuArea.append("已断开连接 [" + Main.CurrentTime() + "]\n");
            ConnectButton.setEnabled(true);
            ConnectButton.setText("连接");
        } else {
            ConnectButton.setEnabled(false);
            ConnectButton.setText("连接中...");
            String live_id = RoomNumberInput.getText();
            if (live_id.isEmpty()) {
                JOptionPane.showMessageDialog(null, "房间号不能为空！", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            } else if (!live_id.matches("[0-9]+")) {
                JOptionPane.showMessageDialog(null, "房间号只能为数字！", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String live_url = "https://live.douyin.com/" + live_id;
            DanmakuArea.append("正在尝试连接至" + live_url + "...[" + Main.CurrentTime() + "]\n");

            Runnable RoomConnectAttempt = () -> {
                Map<String, String> params = DyDanmakuRequest.getParams(live_id);
                if (params != null) {
                    String roomId = params.get("roomId");
                    String user_unique_id = params.get("user_unique_id");
                    String live_status = params.get("live_status");
                    String live_title = params.get("live_title");
                    String nickname = params.get("nickname");
                    String avatar = params.get("avatar");

                    if (!live_status.equals("2")) {
                        JOptionPane.showMessageDialog(null, "当前房间未开播！", "提示", JOptionPane.WARNING_MESSAGE);
                        ConnectButton.setEnabled(true);
                        ConnectButton.setText("连接");
                        return;
                    } else {
                        String signature = null;
                        try {
                            //根据roomId和user_unique_id获取Signature
                            DYSign.getSignFile();
                            signature = DYSign.sign(roomId, user_unique_id);
                        } catch (IOException e1) {
                            JOptionPane.showMessageDialog(null, "wss签名获取失败！", "提示", JOptionPane.WARNING_MESSAGE);
                            ConnectButton.setEnabled(true);
                            ConnectButton.setText("连接");
                            return;
                        }
                        setAvatar(avatar);
                        nickname_label.setText("主播：" + nickname);
                        title_label.setText("标题：" + live_title);
                        status_label.setText("<html>当前状态：<font color='green'>正在直播...</font></html>");
                        listener.connect(params, signature);
                        DanmakuArea.append(" 连接房间：" + live_title + " || 主播：" + nickname + " [" + Main.CurrentTime() + "]\n");
                        ConnectButton.setEnabled(true);
                        ConnectButton.setText("断开");
                    }

                } else {
                    JOptionPane.showMessageDialog(null, "直播间信息获取失败！", "提示", JOptionPane.WARNING_MESSAGE);
                }
            };

            Thread RoomConnectThread = new Thread(RoomConnectAttempt);
            RoomConnectThread.start();
        }


    }

    private void setAvatar(String avatar_url) {
        try {
            URL url = new URL(avatar_url);
            BufferedImage avatar = ImageIO.read(url);
            avatar_label.setIcon(new ImageIcon(avatar));
        } catch (MalformedURLException e) {
            return;
        } catch (IOException e) {
            return;
        }
    }



    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        Roominfo = new JPanel();
        avatar_label = new JLabel();
        nickname_label = new JLabel();
        title_label = new JLabel();
        status_label = new JLabel();
        DanmakuScrollPanel = new JScrollPane();
        DanmakuArea = new JTextArea();
        RoomNumberLabel = new JLabel();
        RoomNumberInput = new JTextField();
        ConnectButton = new JButton();

        //======== this ========
        setTitle("\u6296\u97f3\u5f39\u5e55\u83b7\u53d6\u5de5\u5177");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        var contentPane = getContentPane();
        contentPane.setLayout(new MigLayout(
            "hidemode 3",
            // columns
            "[40:213,fill]" +
            "[616,fill]" +
            "[114,fill]",
            // rows
            "[235]" +
            "[]" +
            "[]"));

        //======== Roominfo ========
        {
            Roominfo.setLayout(new MigLayout(
                "hidemode 3",
                // columns
                "[189,fill]",
                // rows
                "[84,center]" +
                "[]" +
                "[]" +
                "[]"));
            Roominfo.add(avatar_label, "cell 0 0");

            //---- nickname_label ----
            nickname_label.setText("\u4e3b\u64ad\uff1a");
            Roominfo.add(nickname_label, "cell 0 1");

            //---- title_label ----
            title_label.setText("\u76f4\u64ad\u6807\u9898\uff1a");
            Roominfo.add(title_label, "cell 0 2");

            //---- status_label ----
            status_label.setText("\u5f53\u524d\u72b6\u6001\uff1a");
            Roominfo.add(status_label, "cell 0 3");
        }
        contentPane.add(Roominfo, "cell 0 0");

        //======== DanmakuScrollPanel ========
        {

            //---- DanmakuArea ----
            DanmakuArea.setRows(15);
            DanmakuArea.setEditable(false);
            DanmakuScrollPanel.setViewportView(DanmakuArea);
        }
        contentPane.add(DanmakuScrollPanel, "cell 1 0 2 1");

        //---- RoomNumberLabel ----
        RoomNumberLabel.setText("\u8f93\u5165\u6296\u97f3\u623f\u95f4\u53f7\uff1a");
        RoomNumberLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        contentPane.add(RoomNumberLabel, "cell 0 1");
        contentPane.add(RoomNumberInput, "cell 1 1");

        //---- ConnectButton ----
        ConnectButton.setText("\u8fde\u63a5");
        ConnectButton.addActionListener(e -> RoomConnect(e));
        contentPane.add(ConnectButton, "cell 2 1");
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    public static void main(String[] args) {
        gui gui = new gui();
        gui.setVisible(true);
    }

    public void DanmakuOutput(String text) {
        DanmakuArea.append(text);
        DanmakuArea.setCaretPosition(DanmakuArea.getDocument().getLength());
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JPanel Roominfo;
    private JLabel avatar_label;
    private JLabel nickname_label;
    private JLabel title_label;
    private JLabel status_label;
    private JScrollPane DanmakuScrollPanel;
    public JTextArea DanmakuArea;
    private JLabel RoomNumberLabel;
    private JTextField RoomNumberInput;
    private JButton ConnectButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
