package pro.pfe.first;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.glxn.qrgen.android.QRCode;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Student_Lobby extends AppCompatActivity {
    TextView name_txt,matricule_txt,MAC_txt;
    String name,matricule,MAC;


    Button dscvr ;
    TextView conninfo,grpinfo;

    WifiP2pManager wp2pm;
    WifiManager wm;
    WifiP2pManager.Channel mChannel;

    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;

    MessagingSystem sendRecieve;
    ClientClass clientClass;

    List<WifiP2pDevice> peers= new ArrayList<WifiP2pDevice>();
    String[] deviceNameArray;
    WifiP2pDevice[] deviceArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student__lobby);
        name_txt=(TextView) findViewById(R.id.name);
        matricule_txt=(TextView) findViewById(R.id.matricule);
        MAC_txt=(TextView) findViewById(R.id.mac);
        conninfo=(TextView) findViewById(R.id.status);
        grpinfo=(TextView)findViewById(R.id.groupstatus);
        dscvr=(Button) findViewById(R.id.dscvr);

        //TODO : Implement Discovery of this Peer then Share its MAC


        name="Testeur Miloud";
        matricule="04941041963";
        MAC="8C:5D:55:84:DA";
        //Temporary VALUES
        dscvr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDiscovery();
            }
        });

        InitNetwork();
        InitQR(name,matricule,MAC);
    }
    void startDiscovery(){
        wp2pm.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                conninfo.setText("Discovery Started . . .");
                Log.e("DESCOVERY","DISCOVERED");
                dscvr.setEnabled(false);
            }

            @Override
            public void onFailure(int i) {
                conninfo.setText("Discovery couldn't establish ! , sorry..");
            }
        });
    }
    void InitNetwork(){
        wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(!wm.isWifiEnabled())
            wm.setWifiEnabled(true);

        wp2pm = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = wp2pm.initialize(this,getMainLooper(),null);
        mReceiver = new StudentWifiReceiver(wp2pm,mChannel,this);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        WifiInfo info = wm.getConnectionInfo();
        MAC = info.getMacAddress();
        startDiscovery();

    }
    public void StartExam(View view){
        Intent exam_activity = new Intent(this, DuringExamActivity.class);
        startActivity(exam_activity);
    }
    void InitQR(String name,String matricule,String MAC){
        name_txt.setText(name);
        matricule_txt.setText(matricule);
        MAC_txt.setText(MAC);
        Bitmap myBitmap = QRCode.from("0/"+name+"/"+matricule+"/"+MAC).withSize(700, 700).bitmap();
        ImageView myImage = (ImageView) findViewById(R.id.imageView);
        myImage.setImageBitmap(myBitmap);
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);

    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
        wp2pm.stopPeerDiscovery(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.e("Stoped","Discovery");
            }

            @Override
            public void onFailure(int i) {

            }
        });
    }





    WifiP2pManager.ConnectionInfoListener connectionInfoListener=new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            final InetAddress groupOwnerAddress = wifiP2pInfo.groupOwnerAddress;
            Log.e("Connect","owner? : "+wifiP2pInfo.isGroupOwner);
            if(wifiP2pInfo.isGroupOwner){
                conninfo.setText("Groupe Owner");
                wp2pm.requestGroupInfo(mChannel,groupeInfoListener);
              //  serverClass=new ServerClass();
                //otherguysIp="";
              //  serverClass.start();
            }
            else{
                conninfo.setText("Client ");
                clientClass = new ClientClass(groupOwnerAddress.getHostAddress());
                clientClass.start();

                Log.e("COMUNCATION","Started ");
            }
        }
    };

    WifiP2pManager.GroupInfoListener groupeInfoListener=new WifiP2pManager.GroupInfoListener() {
        @Override
        public void onGroupInfoAvailable(WifiP2pGroup wifiP2pInfo) {

            if(wifiP2pInfo.isGroupOwner()){
                conninfo.setText("Groupe Owner"+wifiP2pInfo.getClientList().size()+")" );
                grpinfo.setText("pass :"+wifiP2pInfo.getPassphrase()+"owner adress:"+wifiP2pInfo.getOwner().deviceAddress+" network name : "+wifiP2pInfo.getNetworkName()+" size :"+wifiP2pInfo.getClientList().size()+" isOwner : "+wifiP2pInfo.isGroupOwner());

            }
            else {
                WifiP2pDevice d =wifiP2pInfo.getOwner();
                grpinfo.setText("pass :"+wifiP2pInfo.getPassphrase()+"owner adress:"+wifiP2pInfo.getOwner().deviceAddress+" network name : "+wifiP2pInfo.getNetworkName()+" size :"+wifiP2pInfo.getClientList().size()+" isOwner : "+wifiP2pInfo.isGroupOwner());
                conninfo.setText("Client ("+wifiP2pInfo.getClientList().size()+")" );

            }
        }
    };
    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    byte[] readBuff= (byte[]) msg.obj;
                    String tempMsg = new String(readBuff,0,msg.arg1);
                    grpinfo.setText(tempMsg);

                    Toast.makeText(getApplicationContext(),"recieved : "+tempMsg,Toast.LENGTH_SHORT);
                    break;

            }
            return true;
        }
    });

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
                sendRecieve=new MessagingSystem(socket,handler);
                sendRecieve.start();
                sendRecieve.write("HP SLATE 7 , Please send me The Exam .. ".getBytes());
                Log.e("Client","Can Now Send To The Dude ");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
