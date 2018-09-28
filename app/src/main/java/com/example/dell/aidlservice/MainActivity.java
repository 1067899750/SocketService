package com.example.dell.aidlservice;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.aidlservice.service.BackService;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private TextView mResultText;
    private EditText mEditText;
    private Intent mServiceIntent;

    private IBackService iBackService;

    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            iBackService = null;

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iBackService = IBackService.Stub.asInterface(service);
        }
    };

    class MessageBackReciver extends BroadcastReceiver {
        private WeakReference<TextView> textView;

        public MessageBackReciver(TextView tv) {
            textView = new WeakReference<TextView>(tv);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            TextView tv = textView.get();
            if (action.equals(BackService.HEART_BEAT_ACTION)) {
                if (null != tv) {
                    Log.i("danxx", "Get a heart heat");
                    tv.setText("Get a heart heat");
                }
            } else {
                Log.i("danxx", "Get a heart heat");
                String message = intent.getStringExtra("message");
                tv.setText("服务器消息:" + message);
            }
        }

    }

    private MessageBackReciver mReciver;

    private IntentFilter mIntentFilter;

    private LocalBroadcastManager mLocalBroadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);

        mResultText = (TextView) findViewById(R.id.resule_text);
        mEditText = (EditText) findViewById(R.id.content_edit);
        findViewById(R.id.send).setOnClickListener(this);
        findViewById(R.id.send1).setOnClickListener(this);
        mReciver = new MessageBackReciver(mResultText);

        mServiceIntent = new Intent(this, BackService.class);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(BackService.HEART_BEAT_ACTION);
        mIntentFilter.addAction(BackService.MESSAGE_ACTION);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mLocalBroadcastManager.registerReceiver(mReciver, mIntentFilter);
        bindService(mServiceIntent, conn, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(conn);
        mLocalBroadcastManager.unregisterReceiver(mReciver);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send:
                String content = mEditText.getText().toString();
                try {
                    boolean isSend = iBackService.sendMessage(content);//Send Content by socket
                    Toast.makeText(this, isSend ? "success" : "fail", Toast.LENGTH_SHORT).show();
                    mEditText.setText("");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.send1:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            acceptServer();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;

            default:
                break;
        }
    }


    private void acceptServer() throws IOException {
        //1.创建客户端Socket，指定服务器地址和端口
        Socket socket = new Socket("172.16.50.115", 12345);
        //2.获取输出流，向服务器端发送信息
        OutputStream os = socket.getOutputStream();
        PrintWriter printWriter = new PrintWriter(os); //将输出流包装为打印流

        //获取客户端的IP地址
        InetAddress address = InetAddress.getLocalHost();
        String ip = address.getHostAddress();
        printWriter.write("客户端：~" + ip + "~ 接入服务器！！");
        printWriter.flush();
        socket.shutdownInput();
        socket.close();

    }


}
