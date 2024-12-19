package de.uulm.in.vs.grn.p5;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.concurrent.ConcurrentLinkedQueue;

//This class implements its non consuming almost streamlike behavior to send the pixeldata to the server
public class PixelflutSendingHandler extends PixelflutThreadHandler{

    public PixelflutSendingHandler(Pixelflut.UdpConnection udpConnection, ConcurrentLinkedQueue<PixelflutData> inputQueue, ConcurrentLinkedQueue<PixelflutData> outputQueue) {
        super(udpConnection, inputQueue, outputQueue);
    }

    @Override
    protected PixelflutData work(PixelflutData pixelflutData) throws IOException {
        super.udpConnection.socket().send(
                new DatagramPacket(pixelflutData.getFormatedData(),
                        0,
                        pixelflutData.getLength(),
                        super.udpConnection.address(),
                        this.udpConnection.port()));
        return pixelflutData;
    }
}

