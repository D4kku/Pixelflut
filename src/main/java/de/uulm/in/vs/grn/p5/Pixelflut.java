package de.uulm.in.vs.grn.p5;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import javax.swing.Timer;
import javax.swing.JFrame;
import javax.swing.JColorChooser;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Pixelflut {
    private final JFrame frame;
    private Color color;
    private ConcurrentLinkedQueue<PixelflutData> sendQueue;
    private ConcurrentLinkedQueue<PixelflutData> receiveQueue;
    private ConcurrentLinkedQueue<PixelflutData> drawQueue;

    //just a record to hold all the data needed to send a packet
    public record UdpConnection(DatagramSocket socket, int port, InetAddress address){}

    public Pixelflut(String host, int port) throws IOException {
        color = new Color((int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255));


        UdpConnection udpConnection = new UdpConnection(new DatagramSocket(),port,InetAddress.getByName(host));

        //Conncurent queues which are split up according to the task
        //i do it this way so i can give these to the seperate threads as i want different threads to do different things
        this.sendQueue = new ConcurrentLinkedQueue<PixelflutData>();
        this.receiveQueue = new ConcurrentLinkedQueue<PixelflutData>();
        this.drawQueue = new ConcurrentLinkedQueue<PixelflutData>();

        // TODO: redo the thread/the data type since idk how else to get the udp packages also how tf do i get other peoples update
        //       do i just send a request for every single pixel?????
        PixelflutImageHandler imageHandler = new PixelflutImageHandler(drawQueue);

        ExecutorService executorService = Executors.newCachedThreadPool();

        //TODO: make this actually use more than 2 threads
        executorService.execute(new PixelflutSendingHandler(udpConnection,sendQueue,receiveQueue));
        executorService.execute(new PixelflutReceiverHandler(udpConnection,receiveQueue,drawQueue));


        // TODO: replace my cursed shit with the inbuild functions
        // pixels can be set using the following method, the Byte.toUnsignedInt()
        // function is used to prevent java to interpret the most significant bit
        // of a byte as the sign of the resulting integer
        // image.setRGB(x, y, Byte.toUnsignedInt(r), Byte.toUnsignedInt(g), Byte.toUnsignedInt(b));

        // simple pixelflut gui, you do not have to change this for the task,
        // but feel free to make improvements if you want to :)
        frame = new JFrame("VNS Pixelflut");
        frame.setSize(1024, 1024);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                byte x = (byte) Math.round(e.getX()/8);
                byte y = (byte) Math.round(e.getY()/8);
                switch (e.getButton()) {
                    case MouseEvent.BUTTON1:
                        try {
                            sendUpdate(x, y, color);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        break;

                    case MouseEvent.BUTTON2:
                        // middle click: copy color of selected pixel
                        color = new Color(imageHandler.image.getRGB(x, y));
                        break;

                    case MouseEvent.BUTTON3:
                        // right click: select new color with picker
                        Color c = JColorChooser.showDialog(frame, "Choose color", color);
                        if (c != null) {
                            color = c;
                        }
                        break;
                }
            }
        });
        // redraw canvas with a fixed frame rate of ~30fps
        Timer timer = new Timer(33, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                imageHandler.renderNewImage();//renders a new Image if there is new data (maybe slower since its not async)
                frame.getGraphics().drawImage(imageHandler.image, 0, 0, 1024, 1024, frame);
            }
        });
        timer.start();

        // send a first update to say hello, otherwise we won't receive updates by the server
        sendUpdate((byte) 0, (byte) 0, color);
    }

    public static void main(String[] args) throws Exception {

        if (args.length >= 2) {
            System.err.println("Usage: java Pixelflut HOST PORT");
            System.exit(1);
        }
        //just defaults to the in the moodle provided adress if nothing else was specified
        //better for working with it TODO: maybe remove it
        String host = args.length != 2 ? "vns.lxd-vs.uni-ulm.de" : args[0];
        int port = args.length != 2 ? 9999: Integer.parseInt(args[1]);
        new Pixelflut(host, port);
    }
    public void sendUpdate(byte x, byte y, Color color) throws IOException {
        sendQueue.add(new PixelflutData(x,y,color));
    }

}
