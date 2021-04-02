package TinyWebserver.servlet;

import java.io.IOException;

public abstract class HttpServlet{

    public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException {
        String protocol = request.getProtocol();
        if (protocol.endsWith("1.1")) {
            response.sendError(HttpServletResponse.HTTP_METHOD_NOT_ALLOWED,"Method NOT allowed",
                    "Method NOT allowed");
        } else {
            response.sendError(HttpServletResponse.HTTP_BAD_REQUEST,"BAD Request","BAD Request");
        }
    }

    public void doPost(HttpServletRequest request,HttpServletResponse response) throws  IOException{
        String protocol = request.getProtocol();
        if (protocol.endsWith("1.1")) {
            response.sendError(HttpServletResponse.HTTP_METHOD_NOT_ALLOWED,"Method NOT allowed",
                    "Method NOT allowed");
        } else {
            response.sendError(HttpServletResponse.HTTP_BAD_REQUEST,"BAD Request","BAD Request");
        }
    }

    public void service(HttpServletRequest request,HttpServletResponse response) {
        String method = request.getMethod();
        try {
            if (method.equals("GET"))
                doGet(request,response);
            else
                doPost(request,response);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
