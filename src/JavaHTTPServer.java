

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.StringTokenizer;

public class JavaHTTPServer implements Runnable {
    static final File WEB_ROOT = new File("./");
    static final String DEFAULT_STRING = "index.html";
    static final String FILE_NOT_FOUND = "404.html";
    static final String METHOD_NOT_SUPPORTED = "not_supported.html";
    static final int PORT = 3000;

    // verbose mode
    static final boolean verbose = true;

    // socket for connection
    private Socket connect;

    public JavaHTTPServer(Socket c) {
        connect = c;
    }

    public static void main(String[] args) {
        try {
            ServerSocket serverConnect = new ServerSocket(PORT);
            System.out.println("Server started...\nListening for connections on port " + PORT + "...\n");

            while (true) {
                JavaHTTPServer myServer = new JavaHTTPServer(serverConnect.accept());

                if (verbose) {
                    System.out.println("Connection opened. (" + new Date() + ")");
                }

                Thread thread = new Thread(myServer);
                thread.start();
            }
        } catch (IOException e) {
            System.err.println("Server Connection Error: " + e.getLocalizedMessage());
        }
    }

    @Override
    public void run() {
        BufferedReader in = null;
        PrintWriter out = null;
        BufferedOutputStream dataOut = null;
        String fileRequested = null;
        try {
            in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
            out = new PrintWriter(connect.getOutputStream());
            dataOut = new BufferedOutputStream(connect.getOutputStream());
            String input = in.readLine();
            StringTokenizer parse = new StringTokenizer(input);
            String method = parse.nextToken().toUpperCase();
            fileRequested = parse.nextToken().toLowerCase();

            // GET support only for this server at the moment
            if (!method.equals("GET") && !method.equals("HEAD")) {
                if (verbose) System.out.println("Error Status 501: " + method + " method is not supported.");
                File file = new File(WEB_ROOT, METHOD_NOT_SUPPORTED);
                int fileLength = (int) file.length();
                String mimeType = "text/html";
                byte[] fileData = readFileData(file, fileLength);
                out.println("HTTP/1.1 501 Method Not Found Implemented");
                out.println("Java HTTP Server from Brian: 1.0");
                out.println("Date: " + new Date());
                out.println("Content-type: " + mimeType);
                out.println("Content-Length: " + fileLength);
                out.println(); // blank line between headers and content, very important!!
                out.flush();
                dataOut.write(fileData, 0, fileLength);
                dataOut.flush();
                return;
            } else {
                // handle GET request and 404 errors here...
            }



        } catch (IOException e) {
            System.err.println("Error with input stream: " + e.getLocalizedMessage());
        }
    }

    private byte[] readFileData(File file, int fileLength) throws IOException {
        FileInputStream fileIn = null;
        byte[] fileData = new byte[fileLength];

        try {
            fileIn = new FileInputStream(file);
            fileIn.read(fileData);
        } finally {
            if (fileIn != null) {
                fileIn.close();
            }
        }
        return fileData;
    }
}
