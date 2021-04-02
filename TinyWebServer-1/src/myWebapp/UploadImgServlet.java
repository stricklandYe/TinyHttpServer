package myWebapp;

import TinyWebserver.MultipartData;
import TinyWebserver.WebServlet;
import TinyWebserver.servlet.HttpServlet;
import TinyWebserver.servlet.HttpServletRequest;
import TinyWebserver.servlet.HttpServletResponse;

import java.io.*;

@WebServlet(urlPattern = "/uploadimg")
public class UploadImgServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        super.doGet(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String path = System.getProperty("user.dir")+File.separator + "tmp";
        MultipartData multipartData = request.getData();
        path += File.separator+multipartData.getValue();
        File file = new File(multipartData.getTempFile());
        System.out.println(file.length());
        FileInputStream input = new FileInputStream(file);
        System.out.println(input.available());
        FileOutputStream out = new FileOutputStream(path);
        int data = 0;
        while((data = input.read()) != -1) {
            out.write(data);
        }
        out.flush();
        out.close();
        PrintWriter writer = response.getWriter();
        writer.write("<h1>Hello world</h1>");
    }
}
