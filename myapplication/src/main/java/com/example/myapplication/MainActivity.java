package com.example.myapplication;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements Runnable {

    //定义相关变量,完成初始化
    private TextView mTextView;
    private EditText mEditText;
    private Button mButton;
    private static final String HOST = "172.16.50.115";
    private static final int PORT = 12345;
    private Socket socket = null;
    private BufferedReader in = null;
    private PrintWriter out = null;
    private String content = "";
    private StringBuilder sb = null;

    //定义一个handler对象,用来刷新界面
    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0x123) {
                sb.append(content);
                mTextView.setText(sb.toString());
            }
        }

        ;
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sb = new StringBuilder();
        mTextView = findViewById(R.id.txtshow);
        mEditText = findViewById(R.id.editsend);
        mButton = findViewById(R.id.btnsend);

        //当程序一开始运行的时候就实例化Socket对象,与服务端进行连接,获取输入输出流
        //因为4.0以后不能再主线程中进行网络操作,所以需要另外开辟一个线程
        new Thread() {

            public void run() {
                try {
                    socket = new Socket(HOST, PORT);
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                    out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        //为发送按钮设置点击事件
        mButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String msg = mEditText.getText().toString();
                if (socket.isConnected()) {
                    if (!socket.isOutputShutdown()) {
                        out.println(msg);
                    }
                }
            }
        });
        new Thread(MainActivity.this).start();
    }

    //重写run方法,在该方法中输入流的读取
    @Override
    public void run() {
        try {
            while (!socket.isClosed() && !socket.isInputShutdown()) {
                if (socket.isConnected()) {
                    if (!socket.isInputShutdown()) {
                        if ((content = in.readLine()) != null) {
                            content += "\n";
                            handler.sendEmptyMessage(0x123);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}













