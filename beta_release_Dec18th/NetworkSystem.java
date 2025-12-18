import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class NetworkSystem {
    private Map<Integer, SocketConnection> connections;

    public NetworkSystem() {
        connections = new ConcurrentHashMap<>();
    }
    
    public void openConnection(int id, String host, int port) throws IOException {
        // Validate inputs
        if (host == null || host.trim().isEmpty()) {
            throw new IOException("Host cannot be null or empty");
        }
        if (port < 1 || port > 65535) {
            throw new IOException("Port must be between 1 and 65535: " + port);
        }

        Socket socket = null;
        BufferedReader input = null;
        PrintWriter output = null;

        try {
            socket = new Socket(host, port);
            socket.setSoTimeout(30000); // 30 second timeout
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);

            SocketConnection conn = new SocketConnection();
            conn.socket = socket;
            conn.input = input;
            conn.output = output;
            connections.put(id, conn);
        } catch (IOException e) {
            // Clean up resources on failure
            if (output != null) try { output.close(); } catch (Exception ignored) {}
            if (input != null) try { input.close(); } catch (Exception ignored) {}
            if (socket != null) try { socket.close(); } catch (Exception ignored) {}
            throw e;
        }
    }
    
    public void sendData(int id, String data) throws IOException {
        SocketConnection conn = connections.get(id);
        if (conn != null && conn.output != null) {
            conn.output.println(data);
        }
    }
    
    public String receiveData(int id) throws IOException {
        SocketConnection conn = connections.get(id);
        if (conn != null && conn.input != null) {
            String line = conn.input.readLine();
            return line != null ? line : "";
        }
        return "";
    }
    
    public void closeConnection(int id) throws IOException {
        SocketConnection conn = connections.get(id);
        if (conn != null) {
            // Close all resources, even if one throws exception
            IOException firstException = null;

            if (conn.input != null) {
                try {
                    conn.input.close();
                } catch (IOException e) {
                    firstException = e;
                }
            }

            if (conn.output != null) {
                try {
                    conn.output.close();
                } catch (Exception e) {
                    if (firstException == null && e instanceof IOException) {
                        firstException = (IOException)e;
                    }
                }
            }

            if (conn.socket != null) {
                try {
                    conn.socket.close();
                } catch (IOException e) {
                    if (firstException == null) {
                        firstException = e;
                    }
                }
            }

            connections.remove(id);

            if (firstException != null) {
                throw firstException;
            }
        }
    }
    
    public void closeAll() {
        for (Integer id : new ArrayList<>(connections.keySet())) {
            try {
                closeConnection(id);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private static class SocketConnection {
        Socket socket;
        BufferedReader input;
        PrintWriter output;
    }
}
