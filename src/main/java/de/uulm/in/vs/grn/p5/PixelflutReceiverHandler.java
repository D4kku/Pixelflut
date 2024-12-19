package de.uulm.in.vs.grn.p5;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PixelflutReceiverHandler extends PixelflutThreadHandler{

    public PixelflutReceiverHandler(Pixelflut.UdpConnection udpConnection, ConcurrentLinkedQueue<PixelflutData> inputQueue, ConcurrentLinkedQueue<PixelflutData> outputQueue){
        super(udpConnection, inputQueue, outputQueue);
    }

    //TODO: maybe make it so i dont HAVE to send to update
    @Override
    protected PixelflutData work(PixelflutData pixelflutData) throws IOException {
        DatagramPacket packet = new DatagramPacket(pixelflutData.getFormatedData(), pixelflutData.getLength());
        super.udpConnection.socket().receive(packet);
        pixelflutData = new PixelflutData(packet.getData());
        pixelflutData.print("Received in send Update:");
        return pixelflutData;
    }
}
