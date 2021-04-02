package myWebapp;

import TinyWebserver.WebServlet;
import TinyWebserver.servlet.HttpServlet;
import TinyWebserver.servlet.HttpServletRequest;
import TinyWebserver.servlet.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPattern = "/upload")
public class MyServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        super.doGet(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = request.getParameter("name");
        String age = request.getParameter("gender");
        PrintWriter writer = response.getWriter();
        writer.write("<h1>name:"+name+"</h1>");
//        writer.write("<h1>name:"+name+"</h1>");
        writer.write("<h1>age:"+age+"</h1>");
    }
}
