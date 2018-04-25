package pro.pfe.first;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by wassi on 4/24/2018.
 */

public class MessagingSystem extends Thread {
    private Socket socket;
    private Handler handler;
    private InputStream inputStream;
    private OutputStream outputStream;

    public MessagingSystem(Socket skt,Handler handler){
        socket=skt;
        try {
            inputStream=socket.getInputStream();
            outputStream=socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        byte[] buffer=new byte[1024];
        int bytes;
        while(socket!=null){

            try {
                bytes=inputStream.read(buffer);
                if(bytes>0){
                    handler.obtainMessage(1,bytes,-1,buffer).sendToTarget();
                    Log.e("recieving "," Local adress: "+socket.getLocalAddress()+" inet adress : "+socket.getInetAddress());

//                       Toast.makeText(getApplicationContext(),"recieved : "+bytes,Toast.LENGTH_SHORT);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void write(byte[] bytes) throws IOException {
        outputStream.write(bytes);
    }
}