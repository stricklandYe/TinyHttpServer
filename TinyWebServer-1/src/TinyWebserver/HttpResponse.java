package TinyWebserver;


import TinyWebserver.servlet.HttpServlet;
import TinyWebserver.servlet.HttpServletRequest;
import TinyWebserver.servlet.HttpServletResponse;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

public class HttpResponse implements HttpServletResponse  {

    private OutputStream out; //输出二进制文件的时候用这个
    private ResponseWriter writer; //输出文本的文件的用这个
    private HttpRequest request;
    private Socket socket;
    private int status;
    private enum RESOURCE_TYPE {
        STATIC, //图片文件
        DOC
    }

    public static final String STATIC_DIR = System.getProperty("user.dir")+ File.separator+ "static";
    int BUFFER_SIZE = 2048;


    public HttpResponse(Socket socket,HttpRequest request) {
        this.socket = socket;
        try {
            this.out = socket.getOutputStream();
            this.writer = new ResponseWriter(this.out,true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.request = request;
    }

    HashMap<String,String> mimeMap = new HashMap<>();
    {
        mimeMap.put(".html","text/html");
        mimeMap.put(".gif","image/gif");
        mimeMap.put(".png","image/png");
        mimeMap.put(".jpg","image/jpeg");
    }

    public void responseToClient() {
        /*
        * 运行一段时间后会出现空指针错误，不知道什么原因
        * */
        switch (request.getType()) {
            //带有参数的GET
            case GET:
                doGetWithParams();
                break;
            //返回静态页面的GET请求
            case GET_STATIC:
                doGetWithoutParams();
                break;
            //普通表单的POST请求
            case POST:
                doPost();
                break;
            //带有文件的POST请求
            case POST_FILE:
                doPostContainsFile();
                break;
            default:
                System.out.println("Error");
        }
    }

    public void doGetWithParams() {
        //普通的get请求和不带有文件的post请求基本一样
        doPost();
    }

    public void doGetWithoutParams() {
        /*
        * 返回静态资源，根据uri的后缀来判断是返回文本数据还是二进制的数据，如文件等
        * 如果uri只有/,那么表示访问的是首页，所以直接返回index.html
        * */
        File file;
        if (request.getUri().equals("/")) {
            file = new File(STATIC_DIR,"index.html");
            request.setFileSuffix(".html");
        } else {
            file = new File(STATIC_DIR,request.getUri());
        }
        String fileSuffix = request.getFileSuffix();
        if (fileSuffix.equals(".jpg") ||
            fileSuffix.equals(".gif") ||
            fileSuffix.equals(".png") ) {
            sendBinaryFiles(file);
        } else {
            sendCharacters(file);
        }
    }

    public void sendCharacters(File  file) {
        //返回文本信息,如html,.js等
        byte[] buffer = new byte[BUFFER_SIZE];
        if (file.exists()) {
            String requestHeaders = assembleResponse(file.length(),RESOURCE_TYPE.DOC); //先装配一部分HTTP报文
            try {
                FileInputStream fileInput = new FileInputStream(file);
                while((fileInput.read(buffer, 0, BUFFER_SIZE)) > 0 ) {
                    writer.write(new String(buffer));
                }
                fileInput.close();
            } catch (FileNotFoundException e) {
                sendError(HTTP_NOT_FOUND,"NOT Found",e.getMessage());
            } catch (IOException e) {
                sendError(HTTP_SERVER_ERROR,"Server Error",e.getMessage());
            } finally {
                writer.realWrite(requestHeaders);
                writer.realFlush();
            }
        } else {
            //如果文件不存在，返回404
            sendError(HTTP_NOT_FOUND,"NOT Found", "Couldn't find " + file.getAbsolutePath());
        }
    }

    public void sendBinaryFiles(File file) {
        //返回图片等内容，因为在返回图片的时候不能使用字符流(PrintWriter)
        //所以需要用字节流(ByteStream),总体逻辑与上面差不多，但是用outputStream来返回
        byte[] buffer = new byte[BUFFER_SIZE];
        if (file.exists()) {
            try {
                int len;
                FileInputStream inputStream = new FileInputStream(file);
                String requestHeaders = assembleResponse(file.length(),RESOURCE_TYPE.STATIC);
                writer.writeHeaders(requestHeaders);
                writer.implicitFlush();
                while((len = inputStream.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
                out.flush();
                inputStream.close();
            } catch (IOException e) {
                sendError(HTTP_SERVER_ERROR,"Server Error",e.getMessage());
            }
        } else {
            sendError(HTTP_NOT_FOUND,"NOT Found", "Couldn't find " + file.getAbsolutePath());
        }
    }

    public String assembleResponse(long length,RESOURCE_TYPE type) {
        /*
        * 对于html文档不做缓存
        * 对于图片资源做缓存
        * */

        String mimeType = mimeMap.get(request.getFileSuffix());
        StringBuilder responseHeaders = new StringBuilder();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        if (type == RESOURCE_TYPE.DOC) {
            //文本文件
            String date = format.format(calendar.getTime());
            responseHeaders
                    .append(String.format("HTTP/1.1 %d OK\r\n",HTTP_OK))
                    .append(String.format("Content-type: %s\r\n",mimeType))
                    .append(String.format("Content-length: %d\r\n",length))
                    .append("Connection: keep-alive\r\n")
                    .append(String.format("Date: %s\r\n",date))
                    .append("\r\n");
        } else {
            //图片静态资源
            String now = format.format(calendar.getTime());
            calendar.add(Calendar.DATE,1);
            String  tomorrow= format.format(calendar.getTime());
            responseHeaders
                    .append(String.format("HTTP/1.1 %d OK\r\n",HTTP_OK))
                    .append(String.format("Content-type: %s\r\n",mimeType))
                    .append(String.format("Content-length: %d\r\n",length))
                    .append(String.format("Expires: %s\r\n",tomorrow))
                    .append("Connection: keep-alive\r\n")
                    .append(String.format("Date: %s\r\n",now))
                    .append("Cache-control: max-age=36000\r\n")
                    .append("\r\n");
        }
        return responseHeaders.toString();
    }
    
    public void sendError(int errCode ,String shortMsg,String longMsg) {
        StringBuilder errorInfo = new StringBuilder();
        errorInfo
                .append("<h1>ErrorPage</h1>")
                .append(String.format("<h1>Error Code:%d %s</h1>",errCode,shortMsg))
                .append(String.format("<h1>Error Detail:%s</h1>",longMsg));

        StringBuilder responseHeaders = new StringBuilder();
        responseHeaders
                .append(String.format("HTTP/1.1 %d %s\r\n",errCode,shortMsg))
                .append("Content-type: text/html\r\n")
                .append(String.format("Content-length: %d\r\n",errorInfo.length()))
                .append("\r\n");
        //write只是将数据暂时写到responseWriter中的缓冲区当中
        //realWrite才是讲数据直接发送给浏览器
        writer.write(errorInfo.toString());
        writer.realWrite(responseHeaders.toString());
        writer.realFlush();
    }

    public void doPost() {
        Class<? extends HttpServlet> httpServlet = ServerHandler.getServlet(request.getPath());
        if (httpServlet == null) {
            sendError(HTTP_BAD_REQUEST,"Bad Request",request.getPath() + " hasn't be mapped");
            return;
        }
        try {
            HttpServlet servletObj = httpServlet.newInstance();
            Method service = httpServlet.getMethod("service", HttpServletRequest.class, HttpServletResponse.class);
            service.invoke(servletObj,this.request,this);
            String requestHeaders = assembleResponse(this.writer.count,RESOURCE_TYPE.DOC);
            this.writer.realWrite(requestHeaders);
            this.writer.realFlush();
        } catch (NoSuchMethodException e) {
            sendError(HTTP_SERVER_ERROR,"Server Error", e.getMessage());
        } catch (InvocationTargetException e) {
            sendError(HTTP_SERVER_ERROR,"Invalid Reflection Argument",e.getMessage());
        } catch (IllegalAccessException e) {
            sendError(HTTP_SERVER_ERROR,"Illegal Access",e.getMessage());
        } catch (InstantiationException e) {
            sendError(HTTP_SERVER_ERROR,"Cannot Instantiate HttpServlet",e.getMessage());
        }
    }

    public void doPostContainsFile() {
        doPost();
    }

    @Override
    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public PrintWriter getWriter() {
        return this.writer;
    }
}
