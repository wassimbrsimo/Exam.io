package pro.pfe.first;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.nsd.NsdManager;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static pro.pfe.first.Teacher.db;

public class DuringHostingActivity extends AppCompatActivity  {
    ListView listView;
    Button ToggleWifi,Discover,SendMsg;

    TextView readMsg,connStatus,conect;
    EditText writeMsg,ssid;
    int EXAM_ID;

    int mac=0;
    private ZXingScannerView zxing;

    WifiManager wm;
    WifiP2pManager wp2pm;
    NsdManager mNsdManager;
    WifiP2pManager.Channel mChannel;

    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;

    List<WifiP2pDevice> peers= new ArrayList<WifiP2pDevice>();
    String[] deviceNameArray;
    WifiP2pDevice[] deviceArray;
    Exam examin;

    public static Activity activity;
    public static ArrayAdapter<String> adapter;
    List <String> values;

    static final int MESSAGE_READ=1;

    ArrayList<SendRecieve> sendRecieve= new ArrayList<>();
    ArrayList<StudentSocket> students = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_during_hosting);
        initWork();
        Listeners();
        activity=this;

        //SETUP WIFI
        if(!wm.isWifiEnabled())
            wm.setWifiEnabled(true);

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

              launchQRScanner(view);
            }
        });
    }
    private void initWork() {
        ToggleWifi = (Button)findViewById(R.id.onOfff);
        Discover =(Button)findViewById(R.id.discover2);
        SendMsg = (Button) findViewById(R.id.sendButton0);


        listView = (ListView) findViewById(R.id.peerListView0);

        values = new ArrayList<String>();



        adapter= new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);


        // Assign adapter to ListView
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        readMsg =(TextView) findViewById(R.id.readMsg);
        connStatus = (TextView) findViewById(R.id.connectionStatus);
        conect = (TextView) findViewById(R.id.conct);

        EXAM_ID=getIntent().getIntExtra("Exam",0);
        examin= db.getExam(EXAM_ID);
        Log.e("DB"," exam : "+examin.getTitre()+" questions : "+examin.getQuestions().size());

        Toast.makeText(getApplicationContext(),Exam.toString(examin),Toast.LENGTH_LONG).show();
        wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mNsdManager = (NsdManager) getSystemService(Context.NSD_SERVICE);
        wp2pm = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = wp2pm.initialize(this,getMainLooper(),null);

        mReceiver = new TeacherWifiReceiver(wp2pm,mChannel,this);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }
    void updateList(){

    }
    public void startDiscovery(){
        wp2pm.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                connStatus.setText("good , Discovery Started");
            }

            @Override
            public void onFailure(int i) {

            }
        });
    }


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
/*
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
        // clientClass.stop();
        // serverClass.stop();
    }
*/


    void CalculateScore(String recievedMsg,StudentSocket ss){
        String scoremsg="2]";
        int score = 0;
        String answers=recievedMsg.split("]")[1];
        String[] TypedAnswer = answers.split(Student_Lobby.ANSWERS_SEPARATOR);
        Log.e("Recieved answer ","answer : "+answers);
        String student_answer="";
        for(int i =0;i<TypedAnswer.length;i++){
            student_answer+=TypedAnswer[i]+Student_Lobby.ANSWERS_SEPARATOR;
            scoremsg+=examin.getQuestions().get(i).getAnswer()+Student_Lobby.ANSWERS_SEPARATOR;
            Log.e("Calculating ","answer : "+TypedAnswer[i]+" / realAnswer :"+examin.getQuestions().get(i).getAnswer());
            if(TypedAnswer[i].equals(examin.getQuestions().get(i).getAnswer()))
                score++;
        }
        try {
            db.pushAnswer(answers,examin.getId(),ss.getID());
            Log.e("Sending note  answer ","temMSg to send  : "+scoremsg+String.valueOf(score));
            ss.getSr().write((scoremsg+String.valueOf(score)).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void ConnectToMAC(String mac, final WifiP2pDevice device){
        final WifiP2pConfig config =new WifiP2pConfig();
        config.deviceAddress=mac;
        config.groupOwnerIntent=15;
        config.wps.setup = WpsInfo.PBC;
        wp2pm.connect(mChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(),"Connecting to "+device.deviceName,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int i) {
                Toast.makeText(getApplicationContext(),"Cannot connect with "+device.deviceName,Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onStudentIdentified(String result){
        connStatus.setText("Connecting to "+result);
        // String[] data=result.split("/");
        String MAC=result.split("]")[3];
        conect.setText("going to connect to : "+result.split("]")[3]);
        Log.e("MAC","MAC ADRESS IS "+MAC);
        //todo : check if student already exists DB
        students.add(new StudentSocket(new Student(result.split("]")[0],result.split("]")[1],(int)db.insertStudent(result.split("]")[0],result.split("]")[1])),result.split("]")[3],null));
        mac++;
        adapter.add(students.get(students.size()-1).getName() +"          connecting ..");
    }

    //TODO : -  A Z examination tests , security system ,history and marks for each student
    WifiP2pManager.PeerListListener peerListListener=new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
            if(mac>0){
                for (final WifiP2pDevice device : wifiP2pDeviceList.getDeviceList()) {
                    if (device.deviceAddress.equals(students.get(students.size() - (students.get(students.size() - 1).isConnected() ? 2 : 1)).getMAC())) {
                        Log.e("PEERS", "FOUND " + device.deviceName);

                        ConnectToMAC(students.get(students.size() - (students.get(students.size() - 1).isConnected() ? 2 : 1)).getMAC(), device);
                        mac--;
                    }
                }
            }
            if(peers.size()==0){
                Toast.makeText(getApplicationContext(),"No peers Found ! ",Toast.LENGTH_SHORT).show();
            }
        }
    };

    WifiP2pManager.ConnectionInfoListener connectionInfoListener=new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            if(wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner){

                connStatus.setText("Groupe Owner");
                wp2pm.requestGroupInfo(mChannel,groupeInfoListener);

                ServerClass serverClass=new ServerClass();

                serverClass.start();

            }
            else
                Log.e("Error","the TEACHER IS NOT OWNER");
        }
    };

    WifiP2pManager.GroupInfoListener groupeInfoListener=new WifiP2pManager.GroupInfoListener() {
        @Override
        public void onGroupInfoAvailable(WifiP2pGroup wifiP2pInfo) {
            if(wifiP2pInfo!=null) {
                Log.e("GROUPEINFO","We got : "+wifiP2pInfo.getClientList().size());

            if(wifiP2pInfo.isGroupOwner()){
                connStatus.setText("Groupe Owner"+wifiP2pInfo.getClientList().size()+")" );
                conect.setText("pass :"+wifiP2pInfo.getPassphrase()+"owner adress:"+wifiP2pInfo.getOwner().deviceAddress+" network name : "+wifiP2pInfo.getNetworkName()+" size :"+wifiP2pInfo.getClientList().size()+" isOwner : "+wifiP2pInfo.isGroupOwner());

            }
            else {
                WifiP2pDevice d =wifiP2pInfo.getOwner();
                conect.setText("pass :"+wifiP2pInfo.getPassphrase()+"owner adress:"+wifiP2pInfo.getOwner().deviceAddress+" network name : "+wifiP2pInfo.getNetworkName()+" size :"+wifiP2pInfo.getClientList().size()+" isOwner : "+wifiP2pInfo.isGroupOwner());
                connStatus.setText("Client ("+wifiP2pInfo.getClientList().size()+")" );

            } }
        }
    };

    int getStudentIndexByIP(String IP){
        for(int i=0;i<students.size();i++){
            if(students.get(i).isConnected() && students.get(i).getSr().socket.getInetAddress().toString().equals(IP))
                return i;
        }return -1;}
    int getSendRecieveIndexByIP(String IP){
        for(int i=0;i<sendRecieve.size();i++){
            if(sendRecieve.get(i).socket.getInetAddress().toString().equals(IP))
                return i;
        }return -1;}

    int matchSrStudent(String MAC,int indexSR){
        for(int i=0;i<students.size();i++)
            if(students.get(i).getMAC().equals(MAC)){
                students.get(i).setSr(sendRecieve.get(indexSR));
                sendRecieve.remove(indexSR);
                Log.e("RESEAU","Added SR to main and removed from Queu");
                return i;
            }
        Log.e("RESEAU","didnt find SR MAC ");
        return -1;
    }

    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            byte[] readBuff= (byte[]) msg.obj;
            String tempMsg = new String(readBuff,0,msg.arg1);
            readMsg.setText(tempMsg);
            conect.setText(conect.getText()+" O ");
            if (tempMsg.split("]")[0].equals("0")) {
                try {

                    Log.e("Host RECEPTION","msg : "+tempMsg + " //// msg.what="+msg.what);
                    students.get(matchSrStudent(tempMsg.split("]")[1],msg.what)).getSr().write(("00]authentification done").getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (tempMsg.split("]")[0].equals("1")) {

                Log.e("Host RECEPTION","Exam Request Recieved FROM :         " +students.get(msg.what).getName());
                try {
                    // here give the sendRecieve ta3 le recieved item
                    students.get(msg.what).getSr().write(Exam.toString(examin).getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            else if (tempMsg.split("]")[0].equals("2")) {

                CalculateScore(tempMsg,students.get(msg.what));

            }
            return true;
        }
    });

    public class SendRecieve extends Thread{
        public Socket socket;
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
                        int index;
                        for(int i=0;i<students.size();i++)
                            Log.e("SR ","student "+students.get(i).getName()+"connected ? "+students.get(i).isConnected());
                        if(srExists(socket.getInetAddress()))
                            index=getSendRecieveIndexByIP(socket.getInetAddress().toString());
                        else
                            index=getStudentIndexByIP(socket.getInetAddress().toString());

                        handler.obtainMessage(index,bytes,-1,buffer).sendToTarget();

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
    boolean srExists(InetAddress ip){
        for(int i=0;i<sendRecieve.size();i++)
            if(sendRecieve.get(i).socket.getInetAddress().equals(ip))
                return true;
        return false;
    }
    public class ServerClass extends Thread {
        Socket socket;
        ServerSocket serverSocket;

        @Override
        public void run() {
            try {
                serverSocket= new ServerSocket(8888);
                socket=serverSocket.accept();// connecté avec le seyed

                sendRecieve.add(new SendRecieve(socket));
                sendRecieve.get(sendRecieve.size()-1).start();
                sendRecieve.get(sendRecieve.size()-1).write("0]sendMac".getBytes());
                Log.e("GO","Can Now Send To The Dude  sendRecieve size: "+sendRecieve.size());

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    public void launchQRScanner(View view){
       /* zxing = new ZXingScannerView(getApplicationContext());
        setContentView(zxing);
        zxing.setResultHandler(this);
        zxing.startCamera();*/
        Intent qrscan=new Intent(this,QRScanner.class);
        startActivity(qrscan);
    }
}
