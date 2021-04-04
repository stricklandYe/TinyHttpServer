package TinyWebserver;


import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Server {

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8080, 5, InetAddress.getByName("127.0.0.1"));
            System.out.printf("TinyWebServer listen at port:%d,ip address:%s\n",8080,"127.0.0.1");
            System.out.println("TinyWebServer launch successfully");
            System.out.println("--------------------------------------");
            Executor executor = Executors.newFixedThreadPool(10);
            while(true) {
                //使用线程池来处理HTTP报文
                Socket socket = serverSocket.accept();
                ServerHandler handler = new ServerHandler(socket);
//                handler.run();
                executor.execute(handler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
