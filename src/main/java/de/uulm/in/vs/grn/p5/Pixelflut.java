package de.uulm.in.vs.grn.p5;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import javax.swing.Timer;
import javax.swing.JFrame;
import javax.swing.JColorChooser;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Pixelflut {        // redraw canvas with a fixed frame rate of ~30fps


    private final DatagramSocket socket;
    private final InetAddress address;
    private final int port;
    private final JFrame frame;
    private final BufferedImage image;
    private Color color;

    public Pixelflut(String host, int port) throws IOException {
            image = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
        color = new Color((int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255));


        this.socket = new DatagramSocket();
        this.port = port;
        this.address = InetAddress.getByName(host);//this should give me the adress for the given host name

        // TODO: redo the thread/the data type since idk how else to get the udp packages also how tf do i get other peoples update
        //       do i just send a request for every single pixel?????
        PixelflutImageHandler imageHandler = new PixelflutImageHandler();
        imageHandler.start();

        //TODO: replace my cursed shit with the inbuild functions
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
                        color = new Color(image.getRGB(x, y));
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
        //TODO: make a seperate render thread
        // redraw canvas with a fixed frame rate of ~30fps
        Timer timer = new Timer(33, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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

    //TODO: split the programm in to 3 seperate classes of threads
    // Each thread class has a Concurrent list of tasks it has to do
    // The sender threads get a list of pixels it has to send after which it will add the the data to the toBeReceived list
    // this list will be given to the receiver threads pool which will wait for the response of the server and then give that response to the toBeDrawen List
    // this list is exclusivly consumed by the pixelflut imageahandler which will update the image accordingly
    public void sendUpdate(byte x, byte y, Color color) throws IOException {
        PixelflutData pixelflutData = new PixelflutData(x,y,color);
        DatagramPacket packet = new DatagramPacket(pixelflutData.getFormatedData(), 0, pixelflutData.getLength(),this.address,this.port);
        socket.send(packet);
        pixelflutData.print("Send in Send Update");

        //TODO: split this in a seperate thread
        //this feels like im working with pointers in c wtf
        packet = new DatagramPacket(pixelflutData.getFormatedData(), pixelflutData.getLength());
        socket.receive(packet); //why wont you fucking work
        pixelflutData = new PixelflutData(packet.getData());
        pixelflutData.print("Received in send Update:");

        //TODO: figure out how the fuck the image actually works
        image.setRGB(pixelflutData.getX(),pixelflutData.getY(),pixelflutData.getRGB());
    }

}
