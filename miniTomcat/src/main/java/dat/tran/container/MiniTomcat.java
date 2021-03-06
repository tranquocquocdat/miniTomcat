package dat.tran.container;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class MiniTomcat {

    private final int port;
    private final String configFileName;
    private Map<String, HttpServlet> handlers = new HashMap<>();

    public MiniTomcat(int port, String configFileName) {
        this.port = port;
        this.configFileName = configFileName;
    }

    private void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);

        while (true) {
            Socket socket = serverSocket.accept();
            Thread socketHandler = new SocketHandler(socket, handlers);
            socketHandler.start();
        }
    }

    private void loadPropertiesFile() throws IOException {
        InputStream input = getClass().getClassLoader().getResourceAsStream(configFileName);
        if (input == null) {
            throw new RuntimeException("Unable to find file: " + configFileName);
        }

        Properties properties = new Properties();
        properties.load(input);
        properties.forEach((key, value) -> {
            HttpServlet servlet = getServletInstance((String) value);
            servlet.init();
            handlers.put((String) key, servlet);
        });

    }

    private HttpServlet getServletInstance(String className) {
        try {
            return (HttpServlet) Class.forName(className).getDeclaredConstructor().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void main(String[] args) throws IOException {
        MiniTomcat container = new MiniTomcat(8888, "config.properties");
        container.loadPropertiesFile();
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                container.handlers.forEach((url, servlet) -> servlet.destory());
            }
        });
        container.start();
    }
}
