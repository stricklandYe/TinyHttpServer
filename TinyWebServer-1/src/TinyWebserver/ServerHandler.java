package TinyWebserver;

import TinyWebserver.servlet.HttpServlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class ServerHandler implements Runnable{
    private Socket socket;
    private static final HashMap<String,Class<? extends HttpServlet>> servletMap = new HashMap<>();
    public static long KEEP_ALIVE_TIMEOUT = 1000*30;

    public ServerHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            socket.setKeepAlive(true);
            HttpRequest request = new HttpRequest();
            HttpResponse response = new HttpResponse(this.socket,request);
            HttpParser parser = new HttpParser(this.socket,request,response);
            scanServlet();
            parser.parse();
            response.responseToClient();
            //关闭tcp链接，但是应该可以实现keep alive，但是我也不知道如何实现好
            closeLink(socket,socket.getInputStream(),socket.getOutputStream());
        } catch (IOException e){
            e.printStackTrace();
        } catch (NullPointerException e) {
            /*
            * 在服务器运行一段时间后,parse()中的read()函数会返回-1表示没有数据读取了
            * 所以request内的数据并没有被正确的初始化，接下来执行response.responseToClient()会
            * 引发空指针错误，所以在这里捕捉空指针错误。return来结束当前指针的运行.
            * 在这里，不能调用system.exit()，因为根据文档,调用system.exit()会终止整个jvm
            * */
            System.out.println("no data to read,thread exits");
            try {
                closeLink(socket,socket.getInputStream(),socket.getOutputStream());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            return;
        }

    }

    public void scanServlet() {
        /*
        * 扫描myWebapp之下的所有类,将@WebServlet中的urlparttern作为key,将对应的Class作为value
        * 放到servletMap中.在HttpResponse需要的时候,使用getServlet来所需要的Class对象
        * */
        String servletDir = System.getProperty("user.dir")+ File.separator + "src"+File.separator + "myWebapp";
        String myWebapp = "myWebapp.";
        File[] files = new File(servletDir).listFiles();
        assert files != null;
        try {
            for (File file:files) {
                String name = file.getName();
                String fileName = name.substring(0,name.indexOf("."));
                String servletName = myWebapp + fileName;
                Class servlet = Class.forName(servletName);
                if (servlet.getSuperclass() == HttpServlet.class) {
                    WebServlet annotation = (WebServlet) servlet.getAnnotation(WebServlet.class);
                    if (annotation != null) {
                        String url = annotation.urlPattern();
                        servletMap.put(url, servlet);
                    } else {
                        System.out.println("Error");
                    }
                } else {
                    System.out.println(servletName+" is not subclass of HttpServlet");
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public  void closeLink(Socket socket, InputStream in, OutputStream writer) {
        //关闭链接,注意应该先关闭InputSteam，OutputStream再关闭sock
        try {
            if (in != null)
                in.close();
            if (writer != null)
                writer.close();
            if (socket != null )
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    public void setKeepAliveTimer(Socket socket, InputStream in, OutputStream writer) {
        /*
        * 我最开始想新建一个线程，经过固定时间后来关闭socket链接。一次来模仿Keep alive的效果
        * 但是在代码中似乎这样操作并不行。但是我也没有把这个函数删除，放在这以作参考。
        * */
        /*
        * 希望在HTTP中保持keep alive.client和server在一段时间后再关闭tcp链接。可以使得
        * 在一个同一个TCP链接之内发送多个HTTP报文。话是这么说，但是通过socket.getPort()函数中，
        * 可以看到在同一个网页内(index.html)中的html页面和img标签使用的是两个TCP链接.
        * stackoverflow上看到一个回答说，浏览器也会使用多线程来同时接受多个内容.所以我也不好判断我的
        * 实现是否正确.
        * */

        Timer keepAliveTimer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                closeLink(socket,in,writer);
                keepAliveTimer.cancel();
            }
        };
        keepAliveTimer.schedule(task,KEEP_ALIVE_TIMEOUT);
    }

    public static Class<? extends HttpServlet>  getServlet(String urlPattern) {
        return servletMap.get(urlPattern);
    }
}
