package pro.pfe.first;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.nsd.NsdManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;

import net.glxn.qrgen.android.QRCode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class Student_Lobby extends AppCompatActivity {
    Button ToggleWifi,Discover,SendMsg;
    ImageView QR;
    TextView readMsg,connStatus,conect;
    EditText writeMsg,ssid;


    WifiManager wm;
    WifiP2pManager wp2pm;
    NsdManager mNsdManager;
    WifiP2pManager.Channel mChannel;

    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;

    List<WifiP2pDevice> peers= new ArrayList<WifiP2pDevice>();
    String[] deviceNameArray;
    WifiP2pDevice[] deviceArray;

    static final int MESSAGE_READ=1;

    Boolean groupeFormed=false;
    String otherguysIp="0";
    ServerClass serverClass;
    ClientClass clientClass;
    SendRecieve sendRecieve;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student__lobby);
        initWork();
        Listeners();
        //SETUP WIFI
        if(!wm.isWifiEnabled())
            wm.setWifiEnabled(true);
        InitQR("moh","lol","8C:54:ed:5a:da");

        wp2pm.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                connStatus.setText("Discovery Started . . .");
            }

            @Override
            public void onFailure(int i) {
                connStatus.setText("Discovery couldn't establish ! , sorry..");
            }
        });


    }
    private void Listeners() {
        ToggleWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(wm.isWifiEnabled()){
                    wm.setWifiEnabled(false);
                    ToggleWifi.setText("OFF");
                }
                else {
                    wm.setWifiEnabled(true);
                    ToggleWifi.setText("ON");
                }
            }
        });

        Discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wp2pm.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        connStatus.setText("Discovery Started . . .");
                    }

                    @Override
                    public void onFailure(int i) {
                        connStatus.setText("Discovery couldn't establish ! , sorry..");
                    }
                });
                //  mNsdManager.discoverServices(
                //        "_http._tcp.", NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
            }
        });

        SendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = writeMsg.getText().toString();
                try {
                    if(sendRecieve!=null)
                        sendRecieve.write(msg.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }
    private void initWork() {
        ToggleWifi = (Button)findViewById(R.id.onOfff);
        Discover =(Button)findViewById(R.id.discover2);
        SendMsg = (Button) findViewById(R.id.sendButton0);
        QR =(ImageView) findViewById(R.id.imageView2);

        readMsg =(TextView) findViewById(R.id.readMsg);
        connStatus = (TextView) findViewById(R.id.connectionStatus);
        conect = (TextView) findViewById(R.id.conct);
        writeMsg = (EditText) findViewById(R.id.writeMsg0);
        ssid= (EditText) findViewById(R.id.ssid);

        wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mNsdManager = (NsdManager) getSystemService(Context.NSD_SERVICE);
        wp2pm = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = wp2pm.initialize(this,getMainLooper(),null);

        mReceiver = new StudentWifiReceiver(wp2pm,mChannel,this);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }
    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case MESSAGE_READ:
                    byte[] readBuff= (byte[]) msg.obj;
                    String tempMsg = new String(readBuff,0,msg.arg1);
                    readMsg.setText(tempMsg);
                    conect.setText(conect.getText()+" O ");

                    Toast.makeText(getApplicationContext(),"recieved : "+tempMsg,Toast.LENGTH_SHORT);
                    break;

            }
            return true;
        }
    });
   // WifiP2pManager.PeerListListener peerListListener=new WifiP2pManager.PeerListListener() {
     //   @Override
       // public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
         //   if(!wifiP2pDeviceList.getDeviceList().equals(peers)){
           //     peers.clear();
             //   peers.addAll(wifiP2pDeviceList.getDeviceList());
               // deviceNameArray=new String[wifiP2pDeviceList.getDeviceList().size()];
               // deviceArray=new WifiP2pDevice[wifiP2pDeviceList.getDeviceList().size()];
                //int index = 0;
                //for (WifiP2pDevice device : wifiP2pDeviceList.getDeviceList()){
                 //   deviceNameArray[index]=device.deviceName;
                 //   deviceArray[index]=device;
                 //   index++;
                //}
                //ArrayAdapter<String> adapter =new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,deviceNameArray);
                //listView.setAdapter(adapter);
            //}
            //if(peers.size()==0){
            //    Toast.makeText(getApplicationContext(),"No peers Found ! ",Toast.LENGTH_SHORT).show();
            //}
        //}
    //};

    WifiP2pManager.ConnectionInfoListener connectionInfoListener=new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            final InetAddress groupOwnerAddress = wifiP2pInfo.groupOwnerAddress;
            if(wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner){
                groupeFormed=true;
                connStatus.setText("Groupe Owner");
                wp2pm.requestGroupInfo(mChannel,groupeInfoListener);
                serverClass=new ServerClass();
                otherguysIp="";
                serverClass.start();
            }
            else if(wifiP2pInfo.groupFormed){
                connStatus.setText("Client ");
                clientClass = new ClientClass(groupOwnerAddress.getHostAddress());
                otherguysIp=groupOwnerAddress.getHostAddress();
                clientClass.start();
            }
        }
    };

    WifiP2pManager.GroupInfoListener groupeInfoListener=new WifiP2pManager.GroupInfoListener() {
        @Override
        public void onGroupInfoAvailable(WifiP2pGroup wifiP2pInfo) {

            if(wifiP2pInfo.isGroupOwner()){
                connStatus.setText("Groupe Owner"+wifiP2pInfo.getClientList().size()+")" );
                conect.setText("pass :"+wifiP2pInfo.getPassphrase()+"owner adress:"+wifiP2pInfo.getOwner().deviceAddress+" network name : "+wifiP2pInfo.getNetworkName()+" size :"+wifiP2pInfo.getClientList().size()+" isOwner : "+wifiP2pInfo.isGroupOwner());

            }
            else {
                WifiP2pDevice d =wifiP2pInfo.getOwner();
                conect.setText("pass :"+wifiP2pInfo.getPassphrase()+"owner adress:"+wifiP2pInfo.getOwner().deviceAddress+" network name : "+wifiP2pInfo.getNetworkName()+" size :"+wifiP2pInfo.getClientList().size()+" isOwner : "+wifiP2pInfo.isGroupOwner());
                connStatus.setText("Client ("+wifiP2pInfo.getClientList().size()+")" );

            }
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
        //  if (otherguysIp.equals(""))
        //    {       serverClass = new ServerClass();
        //      serverClass.start();
        // }else if (!otherguysIp.equals("0")){
        //     clientClass = new ClientClass(otherguysIp);
        //     clientClass.start();
        //  ://  }

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
        // clientClass.stop();
        // serverClass.stop();
    }

    private class SendRecieve extends Thread{
        private Socket socket;
        private InputStream inputStream;
        private OutputStream outputStream;

        public SendRecieve(Socket skt){
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
                        handler.obtainMessage(MESSAGE_READ,bytes,-1,buffer).sendToTarget();
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

    public class ServerClass extends Thread {
        Socket socket;
        ServerSocket serverSocket;

        @Override
        public void run() {
            try {
                serverSocket= new ServerSocket(8888);
                socket=serverSocket.accept();
                sendRecieve=new SendRecieve(socket);
                sendRecieve.start();
                sendRecieve.write("Welcome HP SLate 7 .. l'exam is : What is France Doing to america right now ? (true) ( False ) ".getBytes());
                Log.e("GO","Can Now Send To The Dude ");

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    public class ClientClass extends Thread{
        Socket socket;
        String hostAdd;

        public ClientClass(String hostAddress){

            // hostAdd=hostAddress.getHostAddress();
            hostAdd=hostAddress;
            socket=new Socket();
        }

        @Override
        public void run() {
            try {
                socket.connect(new InetSocketAddress(hostAdd,8888),500);
                sendRecieve=new SendRecieve(socket);
                sendRecieve.start();
                sendRecieve.write("HP SLATE 7 , Please send me The Exam .. ".getBytes());
                Log.e("Client","Can Now Send To The Dude ");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    void InitQR(String name,String matricule,String MAC){
        Bitmap myBitmap = QRCode.from("0]"+name+"]"+matricule+"]"+MAC).withSize(700, 700).bitmap();
        QR.setImageBitmap(myBitmap);
    }




}
