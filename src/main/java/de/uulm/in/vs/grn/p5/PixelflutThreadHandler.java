package de.uulm.in.vs.grn.p5;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

//this class implements the basic task which are used for working with the data i did it this
public abstract class PixelflutThreadHandler extends Thread{
    protected Pixelflut.UdpConnection udpConnection;
    protected ConcurrentLinkedQueue<PixelflutData> inputQueue; //genereal definition as every one of the handlers has an input Queue which they consum
    protected ConcurrentLinkedQueue<PixelflutData> outputQueue;

    public PixelflutThreadHandler(Pixelflut.UdpConnection udpConnection, ConcurrentLinkedQueue<PixelflutData> inputQueue ,ConcurrentLinkedQueue<PixelflutData> outputQueue){
        this.udpConnection = udpConnection;
        this.inputQueue = inputQueue;
        this.outputQueue = outputQueue;
    }
    public void run(){
        //TODO: think about if if even makes sense to make it a while true or if i should handel the thread thing in the main class
        while(true){
            try{
                //I COULD MAKE THIS AS A STREAM
                if(!inputQueue.isEmpty()) outputQueue.add(work(inputQueue.remove()));
            }catch (Exception e){

            }
        }
    }
    //TO be implemented by the inheritors
    protected abstract PixelflutData work(PixelflutData pixelflutData) throws IOException;
}
