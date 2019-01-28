package com.mvp.plug.gui;

import javax.swing.*;
import java.awt.event.*;

public class MyDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textField1;
    private JTextField textField2;
    private JComboBox comboBox1;

    private DialogCallBack mCallBack;

    public MyDialog(DialogCallBack callBack) {
        this.mCallBack = callBack;
        setTitle("Mvp Create Helper");
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setSize(300, 220);
        setLocationRelativeTo(null);
        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        textField1.setText("kcj");

        comboBox1.addItem("activity");
        comboBox1.addItem("fragment");
        comboBox1.addItem("activityList");
        comboBox1.addItem("fragmentList");
    }

    private void onOK() {
        // add your code here
        if (null != mCallBack){
            mCallBack.ok(textField1.getText().trim(), textField2.getText().trim() ,(String)comboBox1.getSelectedItem());
        }
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        MyDialog dialog = new MyDialog(new DialogCallBack() {
            @Override
            public void ok(String author, String moduleName, String moduleType) {
                System.out.print("成功啦！！！！！！ " + "author = " + author + "moduleName = " + moduleName +"moduleType = " + moduleType);
            }
        });
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    public interface DialogCallBack{
        void ok(String author, String moduleName, String moduleType);
    }

}
