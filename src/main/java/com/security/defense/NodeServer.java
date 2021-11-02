package com.security.defense;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.*;
import java.util.Set;

@Component
public class NodeServer implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(NodeServer.class);

    private Integer port;

    private DatagramSocket socket;
    private byte[] buf = new byte[1024];

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private Node node;

    @Autowired
    private NodeController nodeController;

    public void setPort(Integer port) { this.port = port; }

    @Override
    public void run() {
        try {
            if(socket == null) {
                socket = new DatagramSocket(port, InetAddress.getByName(node.getLocalNode()));
            }
        } catch (SocketException | UnknownHostException | NumberFormatException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
        while (true) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                Message message = objectMapper.readValue(received, Message.class);
                switch(message.getCommand()) {
                    case "infect":
                        nodeController.infect();
                        break;
                    case "group":
                        nodeController.group(objectMapper.readValue((String) message.getData(), Set.class));
                        break;
                    case "alert":
                        nodeController.alert();
                        break;
                    default:
                        log.info("Received unknown command " + message.getCommand());
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public void send(String nodeAddress, Message message) {
        try {
            if(socket == null) {
                socket = new DatagramSocket(port, InetAddress.getByName(node.getLocalNode()));
            }
            String messageJson = objectMapper.writeValueAsString(message);
            byte[] msgBuf = messageJson.getBytes();
            DatagramPacket packet = new DatagramPacket(msgBuf, msgBuf.length, InetAddress.getByName(nodeAddress), 8085);
            socket.send(packet);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
