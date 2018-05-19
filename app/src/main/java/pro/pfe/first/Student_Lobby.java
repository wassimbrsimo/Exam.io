package pro.pfe.first;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.nsd.NsdManager;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class Student_Lobby extends AppCompatActivity {
    static ImageView QR;
    TextView connStatus;


    WifiManager wm;
    WifiP2pManager wp2pm;
    NsdManager mNsdManager;
    WifiP2pManager.Channel mChannel;

    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;


    static final int MESSAGE_READ=1;
    static String MAC_ADDRESS;

    String otherguysIp="0";
    ClientClass clientClass;
    public static SendRecieve sendRecieve;

    //____________________Examination__________________________
    RecyclerView rv;
    Button done_exam;
    StudentExamAdapter studentExamAdapter;
    ArrayList<Question> quest_list = new ArrayList<Question>();
    TextView txt,time;
    ProgressBar ptime;
    public static final String ANSWERS_SEPARATOR = "-";
    public static List<String> TypedAnswers = new ArrayList<>();


    View LinearQR,LinearExam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student__lobby);
        initNetWork();
        initExamView();
    }

    public void BtnFinishClicked(View view){
        if(DoneAllQuestions())
            txt.setText("Waiting for Answer "+AnswerPoints());
        else
            txt.setText("please answer To All the questions");
    }
    Boolean DoneAllQuestions(){
        Boolean statement=true;
        for(int i =0;i<TypedAnswers.size();i++)
            if(TypedAnswers.get(i).equals(""))
            {statement=false;
                break;
            }
        return statement;
    }

    int AnswerPoints(){
        int score=0;

        String answers="";
        for(int i =0;i<TypedAnswers.size();i++) {
            answers += TypedAnswers.get(i) + ANSWERS_SEPARATOR;

        }
        try {
            sendRecieve.write(("2]"+answers).getBytes());
            Log.e("CLIENT ","NOTE envoyer : "+answers);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return score;
    }
    private void initExamView(){
        txt=(TextView) findViewById(R.id.txt);
        time=(TextView) findViewById(R.id.time);
        ptime=(ProgressBar) findViewById(R.id.ptime);


        LinearQR=(LinearLayout) findViewById(R.id.qr);
        LinearExam=(LinearLayout) findViewById(R.id.exam);

        LinearExam.setVisibility(View.GONE);
        LinearQR.setVisibility(View.VISIBLE);
    }
    public static void InitQR(String name,String matricule,String MAC){
        Log.e("QR","DONE THE MAC ADRESS IS {"+MAC+"}");
        MAC_ADDRESS=MAC;
        Bitmap myBitmap = QRCode.from("0]"+name+"]"+matricule+"]"+MAC).withSize(700, 700).bitmap();
        QR.setImageBitmap(myBitmap);
    }

    // ___________________________________________________________________
    private void initNetWork() {

        wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(!wm.isWifiEnabled())
            wm.setWifiEnabled(true);

        QR =(ImageView) findViewById(R.id.imageView2);

        connStatus = (TextView) findViewById(R.id.connectionStatus);

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
    void InitExam(final Exam e){
        new CountDownTimer(e.getDuration()*60*1000, 1000){ // Temporary

            public void onTick(long millisUntilFinished){
                ptime.setMax(e.getDuration()*60);
                time.setText(millisUntilFinished/1000/60+":"+(millisUntilFinished/1000-(millisUntilFinished/1000/60)*60));
                ptime.setProgress((int) (millisUntilFinished/1000));
            }
            public  void onFinish(){
                BtnFinishClicked(null);
            }
        }.start();

        rv= findViewById(R.id.quest_rv);
        quest_list=e.getQuestions();
        studentExamAdapter = new StudentExamAdapter(quest_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(studentExamAdapter);
        studentExamAdapter.notifyDataSetChanged();
        for(int i=0 ; i<quest_list.size();i++)
        {
            TypedAnswers.add("");
        }

        LinearExam.setVisibility(View.VISIBLE);
        LinearQR.setVisibility(View.GONE);
    }
    public void sendAnswer(String Answer){
        try {
            sendRecieve.write(Answer.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case MESSAGE_READ:
                    byte[] readBuff= (byte[]) msg.obj;
                    String tempMsg = new String(readBuff,0,msg.arg1);

                    Log.e("CLIENT RECEPTION","msg : "+tempMsg);
                    if (tempMsg.split("]")[0].equals("0")) {
                        try {
                            sendRecieve.write(("0]"+MAC_ADDRESS).getBytes());
                            Log.e("MAC","Sending mac adress to host");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else if (tempMsg.split("]")[0].equals("00")) {
                        Log.e("CLIENT RECEPTION","MAC authentified");
                        Toast.makeText(getApplicationContext(), "Connected succesfully " + tempMsg, Toast.LENGTH_SHORT).show();
                        try {
                            sendRecieve.write("1]ExamRequest".getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                    else if (tempMsg.split("]")[0].equals("1")) {

                        Log.e("CLIENT RECEPTION","EXAM RECIEVED ! : "+tempMsg+ " DURATION : "+Exam.toExam(tempMsg).getQuestions().get(0));
                        InitExam(Exam.toExam(tempMsg));

                        /////////////   FORMAT :  ONE]NAME]Module]ID]DURATION]NUMBERQUESTIONS]QUESTION 1]...2] 3 ..

                    }
                    else if (tempMsg.split("]")[0].equals("2")) {

                        Log.e("CLIENT RECEPTION","NOTE RECIEVED  ! : "+tempMsg);
                            String answer=tempMsg.split("]")[1];
                            String[] notemsg =answer.split(ANSWERS_SEPARATOR);

                        int score = Integer.valueOf(notemsg[notemsg.length-1]);

                        txt.setText("Note Recu : "+score+" sur "+String.valueOf(notemsg.length-1));
                        }
                        /////////////   FORMAT :  ONE]NAME]Module]ID]DURATION]NUMBERQUESTIONS]QUESTION 1]...2] 3 ..


                    break;

            }
            return true;
        }
    });

    WifiP2pManager.ConnectionInfoListener connectionInfoListener=new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            final InetAddress groupOwnerAddress = wifiP2pInfo.groupOwnerAddress;
            if(wifiP2pInfo.groupFormed){
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

            }
            else {
                WifiP2pDevice d =wifiP2pInfo.getOwner();
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
                sendRecieve.write(("").getBytes());
                Log.e("Client","Can Now Send To The Dude ");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }




}
