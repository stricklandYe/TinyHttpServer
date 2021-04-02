package TinyWebserver.servlet;

import java.io.PrintWriter;

public interface HttpServletResponse {

    int HTTP_BAD_REQUEST = 400;
    int HTTP_NOT_FOUND = 404;
    int HTTP_METHOD_NOT_ALLOWED = 405;
    int HTTP_OK = 200;
    int HTTP_SERVER_ERROR = 500;

    void setStatus(int status);
    void sendError(int status, String errorMsg,String errorDetail);
    PrintWriter getWriter();
}
