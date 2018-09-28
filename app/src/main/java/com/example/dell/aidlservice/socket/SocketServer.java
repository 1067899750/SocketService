package com.example.dell.aidlservice.socket;

import com.example.dell.aidlservice.service.BackService;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Dell on 2018/5/4.
 */

public class SocketServer {
    private static Socket mSocket;

    public static void main(String[] argc) {
        try {
            //1.创建一个服务器端Socket，即ServerSocket，指定绑定的端口，并监听此端口
            ServerSocket serverSocket = new ServerSocket(12345);
            InetAddress address = InetAddress.getLocalHost();
            String ip = address.getHostAddress();

            //2.调用accept()等待客户端连接
            System.out.println("~~~服务端已就绪，等待客户端接入~，服务端ip地址: " + ip);
            mSocket = serverSocket.accept();

            //3.连接后获取输入流，读取客户端信息
            InputStream is = null;
            InputStreamReader isr = null;
            BufferedReader br = null;
            OutputStream os = null;
            is = mSocket.getInputStream();
            isr = new InputStreamReader(is, "UTF-8");
            br = new BufferedReader(isr);
            String info = null;
            while ((info = br.readLine()) != null) {
                System.out.println("客户端发送过来的信息" + info);
                if (info.equals(BackService.HEART_BEAT_STRING)) {
                    sendmsg("ok");

                } else {
                    sendmsg("服务器发送过来的信息" + info);

                }
            }

            mSocket.shutdownInput();
            mSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    //为连接上服务端的每个客户端发送信息
    public static void sendmsg(String msg) {
        PrintWriter pout = null;
        try {
            pout = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(mSocket.getOutputStream(), "UTF-8")), true);
            pout.println(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}





















