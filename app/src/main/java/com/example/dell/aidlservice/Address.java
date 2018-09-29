package com.example.dell.aidlservice;

import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

public class Address {

     public static void main(String[] rgc){
         try {
             InetAddress inetAddress = InetAddress.getLocalHost();   //获取本机InetAddress的实例：
             System.out.println(inetAddress.getAddress());
             System.out.println(inetAddress.getHostAddress()); //IP地址
             System.out.println(inetAddress.getHostName()); //本机名


             System.out.println(Arrays.toString(inetAddress.getAddress())); //字节数组形式的IP地址
             System.out.println(inetAddress.getCanonicalHostName());


             System.out.println(inetAddress);

         } catch (UnknownHostException e) {
             e.printStackTrace();
         }

     }




}










