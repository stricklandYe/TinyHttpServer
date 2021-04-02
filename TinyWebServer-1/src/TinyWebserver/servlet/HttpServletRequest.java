package TinyWebserver.servlet;

import TinyWebserver.MultipartData;

import java.util.List;

public interface HttpServletRequest {
    String getHeader(String name);
    String getMethod();
    String getProtocol();
    String getPath();
    String getParameter(String name);
    MultipartData getData();
    String[] getParameters(String name);
}
