/*
 * Created by JFormDesigner on Tue Dec 24 18:40:39 HKT 2024
 */

package com.DyDanmaku.gui;

import java.awt.event.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import javax.swing.*;

import com.DyDanmaku.DYSign;
import com.DyDanmaku.DyDanmakuRequest;
import com.DyDanmaku.Main;
import com.DyDanmaku.WebSocketClient;
import net.miginfocom.swing.*;

/**
 * @author tao
 */
public class gui extends JFrame {
    public gui() {
        initComponents();
    }

    private void RoomConnect(ActionEvent e){
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

        String UserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36";
        Map<String, String> params = DyDanmakuRequest.getParams(live_id);
        if (params != null) {
            String roomId = params.get("roomId");
            String user_unique_id = params.get("user_unique_id");
            String ttwid = params.get("ttwid");
            String live_status = params.get("live_status");
            String live_title = params.get("live_title");
            String nickname = params.get("nickname");

            if (!live_status.equals("2")) {
                JOptionPane.showMessageDialog(null, "当前房间未开播！", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            } else {
                String signature = null;
                try {
                    //根据roomId和user_unique_id获取Signature
                    DYSign.getSignFile();
                    signature = DYSign.sign(roomId, user_unique_id);
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(null, "wss签名获取失败！", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                WebSocketClient listener = new WebSocketClient();
                listener.gui_init(this);
                listener.connect(params, signature);
                DanmakuArea.append(" 连接房间：" + live_title + " || 主播：" + nickname + " [" + Main.CurrentTime() + "]\n");
            }




        } else {
            JOptionPane.showMessageDialog(null, "直播间信息获取失败！", "提示", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        DanmakuScrollPanel = new JScrollPane();
        DanmakuArea = new JTextArea();
        RoomNumberLabel = new JLabel();
        RoomNumberInput = new JTextField();
        ConnectButton = new JButton();

        //======== this ========
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
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JScrollPane DanmakuScrollPanel;
    public JTextArea DanmakuArea;
    private JLabel RoomNumberLabel;
    private JTextField RoomNumberInput;
    private JButton ConnectButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
