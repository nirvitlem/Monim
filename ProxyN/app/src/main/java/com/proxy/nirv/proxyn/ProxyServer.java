package com.proxy.nirv.proxyn;

/**
 * Created by NirV on 10/10/2016.
 */

import android.content.pm.PackageManager;
import android.util.Log;

import java.net.*;
import java.io.*;

public class ProxyServer {


    public static void main(String portP) throws IOException {
        ServerSocket serverSocket = null;
        boolean listening = true;

        int port = 10000;    //default
        try {
            port = Integer.parseInt(portP);
        } catch (Exception e) {
            //ignore me
        }

        try {
            serverSocket = new ServerSocket(port);
            Log.d("RStarted on: ", String.valueOf(port));
            System.out.println("Started on: " + port);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + portP);
            Log.d("Cannot listen on port:", String.valueOf(portP));
            System.exit(-1);
        }

        while (listening) {
            new ProxyThread(serverSocket.accept()).start();
        }
        serverSocket.close();

    }

}

