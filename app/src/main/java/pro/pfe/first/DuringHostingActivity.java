package pro.pfe.first;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class DuringHostingActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    Button add,start,discoverbtn;
    TextView status;
    ListView list;
    String[] listItems = {"student 1 ..","student 2 ..","student 3 ..","student 4 ..","student 5 .."};

    private ZXingScannerView zxing;

    String MAC,Name,Matricule;

    WifiP2pManager wp2pm;
    WifiManager wm;
    WifiP2pManager.Channel mChannel;

    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;

    MessagingSystem sendRecieve;
    ServerClass serverClass;

    List<WifiP2pDevice> peers= new ArrayList<WifiP2pDevice>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_during_hosting);
        int id = getIntent().getIntExtra("Exam_ID", -1);
        list = (ListView) findViewById(R.id.list);
        status = (TextView) findViewById(R.id.status);
        add = (Button) findViewById(R.id.addstudent);
        start = (Button) findViewById(R.id.startexam);
        discoverbtn = (Button) findViewById(R.id.dscover);
        Exam exam;
        if (id != -1)
        {
            exam = Teacher.db.getExam(id);
            status.setText("Exam  : "+exam.getTitre());
        }
        else {
            status.setText("Exam Not Found Error");
        }
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems);
        list.setAdapter(adapter);
        InitNetwork();
        discoverbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onStudentIdentified("lol done");
            }
        });
    }

    public void launchQRScanner(View view){
        zxing = new ZXingScannerView(getApplicationContext());
        setContentView(zxing);
        zxing.setResultHandler(this);
        zxing.startCamera();
    }


    @Override
    public void handleResult(Result result) {
        zxing.removeAllViews();
        zxing.stopCamera();
        Toast.makeText(getApplicationContext(), "Scanned : \" "+result.getText()+" \"", Toast.LENGTH_SHORT).show();
        setContentView(R.layout.activity_during_hosting);
        onStudentIdentified(result.getText());
        //TODO : Implement The Messaging sysstem ( XCODE/Full Name/AdressMac ) ,(XCODE/EXAMSTRING),(XCODE/EXAMANSWER)
    }
    void onStudentIdentified(String result){
        status.setText("Connecting to "+result);
       // String[] data=result.split("/");
        MAC=result;
        wp2pm.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                status.setText("Discovery Started : LOADING ..");
                discoverbtn.setEnabled(false);
            }

            @Override
            public void onFailure(int i) {
                status.setText("Discovery couldn't establish ! , sorry..");
            }
        });
    }
    void InitNetwork(){
        wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(!wm.isWifiEnabled())
            wm.setWifiEnabled(true);

        wp2pm = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = wp2pm.initialize(this,getMainLooper(),null);
        mReceiver = new TeacherWifiReceiver(wp2pm,mChannel,this);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        WifiInfo info = wm.getConnectionInfo();


    }
    WifiP2pManager.PeerListListener peerListListener=new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
            if(!wifiP2pDeviceList.getDeviceList().equals(peers)){
                Toast.makeText(getApplicationContext(),"peers list has  "+wifiP2pDeviceList.getDeviceList().size(),Toast.LENGTH_SHORT).show();
                peers.clear();
                peers.addAll(wifiP2pDeviceList.getDeviceList());

                final WifiP2pConfig config = new WifiP2pConfig();
                listItems= new String[wifiP2pDeviceList.getDeviceList().size()];
                int i=0;
                for (final WifiP2pDevice device : wifiP2pDeviceList.getDeviceList()){
                    listItems[i]=device.deviceName;
                    Log.e("Device PEeer","Found the "+device.deviceName);
                    i++;
                    if(device.deviceName.equals("hp")){

                        //todo : connect TO SCANNED QR if found
                        config.deviceAddress = device.deviceAddress;
                        config.wps.setup = WpsInfo.PBC;
                        config.groupOwnerIntent = 15;
                        Toast.makeText(getApplicationContext(),"Connecting to "+device.deviceName,Toast.LENGTH_SHORT).show();
                        Log.e("Connecting ","to "+device.deviceName);
                    }
                }

                        ArrayAdapter<String> adapter =new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,listItems);
                        list.setAdapter(adapter);
                        wp2pm.connect(mChannel, config, new WifiP2pManager.ActionListener() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(getApplicationContext(),"Connected to +"+config.deviceAddress+" successfully",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(int i) {
                                Toast.makeText(getApplicationContext(),"Cannot connect with inten gO =  "+config.groupOwnerIntent,Toast.LENGTH_SHORT).show();
                            }
                        });


            }
            if(peers.size()==0){
                Toast.makeText(getApplicationContext(),"No peers Found ! ",Toast.LENGTH_SHORT).show();
            }
        }
    };

    WifiP2pManager.ConnectionInfoListener connectionInfoListener=new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            final InetAddress groupOwnerAddress = wifiP2pInfo.groupOwnerAddress;
            if(wifiP2pInfo.isGroupOwner){
                status.setText("Groupe Owner");
                wp2pm.requestGroupInfo(mChannel,groupeInfoListener);
                 serverClass=new ServerClass();
                 serverClass.start();
            }

        }
    };

    WifiP2pManager.GroupInfoListener groupeInfoListener=new WifiP2pManager.GroupInfoListener() {
        @Override
        public void onGroupInfoAvailable(WifiP2pGroup wifiP2pInfo) {

            if(wifiP2pInfo.isGroupOwner()){
                status.setText("pass :"+wifiP2pInfo.getPassphrase()+"owner adress:"+wifiP2pInfo.getOwner().deviceAddress+" network name : "+wifiP2pInfo.getNetworkName()+" size :"+wifiP2pInfo.getClientList().size()+" isOwner : "+wifiP2pInfo.isGroupOwner());

            }
            else {
                WifiP2pDevice d =wifiP2pInfo.getOwner();
                status.setText("pass :"+wifiP2pInfo.getPassphrase()+"owner adress:"+wifiP2pInfo.getOwner().deviceAddress+" network name : "+wifiP2pInfo.getNetworkName()+" size :"+wifiP2pInfo.getClientList().size()+" isOwner : "+wifiP2pInfo.isGroupOwner());

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
                    status.setText(tempMsg);

                    Toast.makeText(getApplicationContext(),"recieved : "+tempMsg,Toast.LENGTH_SHORT);
                    break;

            }
            return true;
        }
    });

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
    public class ServerClass extends Thread {
        Socket socket;
        ServerSocket serverSocket;

        @Override
        public void run() {
            try {
                serverSocket= new ServerSocket(8888);
                socket=serverSocket.accept();
                sendRecieve=new MessagingSystem(socket,handler);
                sendRecieve.start();
                sendRecieve.write("Welcome HP SLate 7 .. are you recieveing ? (true) ( False ) ".getBytes());
                Log.e("GO","Can Now Send To The Dude ");

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
