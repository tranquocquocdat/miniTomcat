package dat.tran;

import dat.tran.container.HttpServlet;
import dat.tran.container.Request;
import dat.tran.container.Response;

import java.io.PrintWriter;

public class HelloWorldServlet extends HttpServlet {

    @Override
    public void init() {
        System.out.println("HelloWorldServlet init() called.....");
    }

    @Override
    public void doGet(Request request, Response response) {
        PrintWriter out = response.getPrintWriter();
        out.println("<html><body>");
        out.println("doGet() in HelloWorldServlet");
        out.println("</body></html>");
    }

    @Override
    public void destory() {
        System.out.println("clean up resources in HelloWorldServlet destory()......");
    }
}
