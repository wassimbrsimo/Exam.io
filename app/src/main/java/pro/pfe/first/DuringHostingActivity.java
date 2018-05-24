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
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    RecyclerView recyclerView;
    Button  addStudent,startStudent;

    TextView readMsg,connStatus,conect;
    EditText writeMsg,ssid;
    int EXAM_ID;
    private ZXingScannerView zxing;

    WifiManager wm;
    WifiP2pManager wp2pm;
    NsdManager mNsdManager;
    WifiP2pManager.Channel mChannel;

    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;

    List<WifiP2pDevice> peers= new ArrayList<WifiP2pDevice>();
    Exam examin;

    public static  Activity activity;
    public  StudentSocketAdapter adapter;

    public int CONNECTING=0;

    public SocketConnexion socket_Connexion =null;
    public ArrayList<StudentSocket> Etudiants = new ArrayList<>();
    public ArrayList<String> AttenteMac=new ArrayList<>();


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
        addStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CONNECTING==0)
              launchQRScanner(view);
            }
        });
        startStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CONNECTING==0)
                    for(StudentSocket socket : Etudiants){
                        try {
                            socket.getSr().write("START_SIGNAL".getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
            }
        });
    }
    private void initWork() {
        addStudent = (Button) findViewById(R.id.sendButton);
        startStudent = (Button) findViewById(R.id.startButton);


        recyclerView = (RecyclerView) findViewById(R.id.rvstudents);
        adapter= new StudentSocketAdapter(Etudiants,this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();



        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        readMsg =(TextView) findViewById(R.id.readMsg);
        connStatus = (TextView) findViewById(R.id.connectionStatus);
        conect = (TextView) findViewById(R.id.conct);

        EXAM_ID=getIntent().getIntExtra("Exam",0);
        examin= db.getExam(EXAM_ID);

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

        registerReceiver(mReceiver, mIntentFilter);


    }
    public void startDiscovery(){
        wp2pm.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                connStatus.setText("good , Discovery Started");
                Log.e("DISCOVERY","ON");
            }

            @Override
            public void onFailure(int i) {

            }
        });
    }
    public void stopDiscovery(){
        wp2pm.stopPeerDiscovery(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                connStatus.setText("good , Discovery Started");
                Log.e("DISCOVERY","OFF");
            }

            @Override
            public void onFailure(int i) {

            }
        });
    }

/*
    @Override
    protected void onResume() {
        super.onResume();

        //  if (otherguysIp.equals(""))
        //    {       serverClass = new ServerClass();
        //      serverClass.start();
        // }else if (!otherguysIp.equals("0")){
        //     clientClass = new ClientClass(otherguysIp);
        //     clientClass.start();
        //  ://  }

    }
*/    public void onDestroy() {

        unregisterReceiver(mReceiver);
    wm.setWifiEnabled(false);
    finish();
        super.onDestroy();

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


    void ConnectToMAC(String mac, final WifiP2pDevice device){
        final WifiP2pConfig config =new WifiP2pConfig();
        config.deviceAddress=mac;
        config.groupOwnerIntent=15;
        config.wps.setup = WpsInfo.PBC;
        wp2pm.connect(mChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(),"Connecting to "+device.deviceName,Toast.LENGTH_SHORT).show();
                Log.e("CONNECT", "CONNECTING TO "+device.deviceName);

                AttenteMac.remove(0);
            }

            @Override
            public void onFailure(int i) {
                Toast.makeText(getApplicationContext(),"Cannot connect with "+device.deviceName,Toast.LENGTH_SHORT).show();
                Log.e("CONNECT", "CANNOT CONNECT TO   XXXXXXXXXXXXXXX" +device.deviceName);
            }
        });
    }
    public boolean studentsexists(String Mac){
        for(StudentSocket ss : Etudiants){
            if(ss.getMAC().equals(Mac))
                return true;
        }
        return  false;
    }
    public void onRetryStudent(int position){
        stopDiscovery();
        for(int i=0;i<AttenteMac.size();i++)
            if(AttenteMac.get(i).equals(Etudiants.get(position).getMAC())){
                Log.e("Removed from attent","position "+i +" mac : "+AttenteMac.get(i));
                AttenteMac.remove(i);
            }
        String name,mat,mac;
        name=Etudiants.get(position).getName();
        mac=Etudiants.get(position).getMAC();
        mat=Etudiants.get(position).getName();
        Etudiants.remove(position);
        adapter.notifyDataSetChanged();
        onStudentIdentified(name+"]"+mat+"]"+mac);
        CONNECTING=0;
    }

    public void startTimedOutTimer(){
        new CountDownTimer(500, 2) {

            public void onTick(long millisUntilFinished) {
                Log.e("Timer "," millis remainins discovery : "+millisUntilFinished);
            }

            public void onFinish() {

                    startDiscovery();
            }
        }.start();

    }
    public void onStudentIdentified(String result){
     if(!studentsexists(result.split("]")[2])) {
         connStatus.setText("Connecting to " + result);
         // String[] data=result.split("/");
         String MAC = result.split("]")[2];
         conect.setText("going to connect to : " + result.split("]")[2]);
         //todo : check if student already exists DB
         Log.e("QR", "Result : " + result);
         Etudiants.add(new StudentSocket(new Student(result.split("]")[0], result.split("]")[1], (int) db.insertStudent(result.split("]")[0], result.split("]")[1])), result.split("]")[2], null));
         AttenteMac.add(MAC);
         Log.e("ETUDIANT AJOUTER  ", "ETUDIENT ::: " + Etudiants.get(Etudiants.size() - 1).getName());

         addStudent.setText("Attente connexion ..");
         startStudent.setText("Attente connextion ..");
         startTimedOutTimer();

         //  addStudent.setEnabled(false);




       //  startDiscovery();
         adapter.notifyDataSetChanged();
     }
     }

    //TODO : -  A Z examination tests , security system ,history and marks for each student
    WifiP2pManager.PeerListListener peerListListener=new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
            Log.e("Discovering "," . . . ");
            if(AttenteMac.size()>0 && CONNECTING==0){

                Log.e("PEERS","faire entrer wahed "+AttenteMac.size());
                for (final WifiP2pDevice device : wifiP2pDeviceList.getDeviceList()) {

                        if (device.deviceAddress.equals(AttenteMac.get(0))) {
                            ConnectToMAC(AttenteMac.get(0), device);
                            CONNECTING=1;

                            Log.e("PEERS","Found "+AttenteMac.size());

                        }
                else
                            Log.e("PEERS","not found "+AttenteMac.size());

                }}
        }
    };

    WifiP2pManager.ConnectionInfoListener connectionInfoListener=new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            if(wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner && CONNECTING==1){
                connStatus.setText("Groupe Owner");
                wp2pm.requestGroupInfo(mChannel,groupeInfoListener);
                Log.e("SERVERCLASS","INITIATED");
                ServerClass serverClass=new ServerClass();

                serverClass.start();

            }
            else{
                Log.e("Error","the TEACHER IS NOT OWNER");
            }
        }
    };

    WifiP2pManager.GroupInfoListener groupeInfoListener=new WifiP2pManager.GroupInfoListener() {
        @Override
        public void onGroupInfoAvailable(WifiP2pGroup wifiP2pInfo) {

            if(wifiP2pInfo!=null) {

            if(wifiP2pInfo.isGroupOwner()){
                connStatus.setText("Connected ("+wifiP2pInfo.getClientList().size()+")" );
                conect.setText(" "+wifiP2pInfo.getClientList().size()+" ");
            }
            }
        }
    };

    int getStudentIndexByIP(String IP){
        for(int i = 0; i< Etudiants.size(); i++){
            if(Etudiants.get(i).isConnected() && Etudiants.get(i).getSr().socket.getInetAddress().toString().equals(IP))
                return i;
        }return -1;}

    int matchSrStudent(String MAC,int indexSR){
        for(int i = 0; i< Etudiants.size(); i++)
            if(Etudiants.get(i).getMAC().equals(MAC)){
                Etudiants.get(i).setSr(socket_Connexion);
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
            Log.e("eeeeeeeee","recieved : _______________________________________________"+tempMsg+" _______________________");

            if(tempMsg.equals("FINISH")){
                Etudiants.get(msg.what).setState(6);
                boolean finished=true;
                for(StudentSocket s :Etudiants)
                    if(s.getState()!=6)
                        finished=false;
                if(finished){
                    Intent resultExam = new Intent(getApplicationContext(),Teacher_Done_Exam.class);
                    resultExam.putExtra("id",examin.getId());
                    startActivity(resultExam);
                    wm.setWifiEnabled(false);
                    finish();
                }
                adapter.notifyDataSetChanged();
            }
            else if (tempMsg.equals("OUT_OF_APP")) {
                Etudiants.get(msg.what).setState(4);
                adapter.notifyDataSetChanged();
            }
            else if (tempMsg.equals("BACK_TO_APP")) {
                Etudiants.get(msg.what).setState(5);
                adapter.notifyDataSetChanged();
            }
            else if (tempMsg.split("]")[0].equals("0")) {
                try {
                    stopDiscovery();
                    addStudent.setText("Ajouter Eleve");
                    startStudent.setText("Commencer Examin");
                    CONNECTING=0;
                    Log.e("Host RECEPTION","msg : "+tempMsg );
                    int index =matchSrStudent(tempMsg.split("]")[1],msg.what);
                    Etudiants.get(index).getSr().write(("AUTHENTIFICATION_CONFIRMED").getBytes());
                    Etudiants.get(index).setState(1);
                    // addStudent.setEnabled(true);
                    adapter.notifyDataSetChanged();
                } catch (IOException e) {
                    e.printStackTrace();}}
            else if (tempMsg.equals("EXAM_REQUEST")) {

                Log.e("Host RECEPTION","Exam Request Recieved FROM :" + Etudiants.get(msg.what).getName());
                try {
                    Etudiants.get(msg.what).setState(2);
                    Etudiants.get(msg.what).getSr().write(Exam.toString(examin).getBytes());
                    adapter.notifyDataSetChanged();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }



            else if (tempMsg.split("]")[0].equals("2")) {

                CalculateScore(tempMsg, Etudiants.get(msg.what));
                Etudiants.get(msg.what).setState(3);
                adapter.notifyDataSetChanged();

            }



            return true;
        }
    });
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

    public class SocketConnexion extends Thread{
        public Socket socket;
        private InputStream inputStream;
        private OutputStream outputStream;

        public SocketConnexion(Socket skt){
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
                        if(srExists(socket.getInetAddress()))
                            index=getStudentIndexByIP(socket.getInetAddress().toString());
                        else//not connected yet
                            index=-1;

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
        for(int i = 0; i< Etudiants.size(); i++)
            if(Etudiants.get(i).isConnected() && Etudiants.get(i).getSr().socket.getInetAddress().equals(ip))
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

                socket_Connexion=new SocketConnexion(socket);
                socket_Connexion.start();
                socket_Connexion.write("MAC_REQUEST".getBytes());
                Log.e("GO","Can Now Send To The Dude  socket_Connexion size: "+ socket_Connexion.getName());

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
