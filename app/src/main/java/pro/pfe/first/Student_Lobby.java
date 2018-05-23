package pro.pfe.first;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.view.LayoutInflater;
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

import static pro.pfe.first.StudentActivity.db;

public class Student_Lobby extends AppCompatActivity {
    static ImageView QR;
    TextView connStatus;


    WifiManager wm;
    WifiP2pManager wp2pm;
    NsdManager mNsdManager;
    WifiP2pManager.Channel mChannel;

    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;


    static final int MESSAGE_READ = 1;
    static String MAC_ADDRESS;

    String otherguysIp = "0";
    ClientClass clientClass;
    SendRecieve sendRecieve=null;
    boolean CONNECTED=false;
    //____________________Examination__________________________
    RecyclerView rv;
    Button retry;
    StudentExamAdapter studentExamAdapter;
    ArrayList<Question> quest_list = new ArrayList<Question>();
    TextView txt, time;
    ProgressBar ptime;
    Exam actualExam = null;
    int id=0;
    public static final String ANSWERS_SEPARATOR = "-";
    public static List<String> TypedAnswers = new ArrayList<>();


    View LinearQR, LinearExam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student__lobby);
        initNetWork();
        initExamView();
        TypedAnswers.clear();
    }

    public void BtnFinishClicked(View view) {
        if (DoneAllQuestions())
            AnswerPoints();
        else{

            LayoutInflater factory = LayoutInflater.from(view.getContext());
            final View dialogView = factory.inflate(R.layout.dialog_unfinished, null);
            final AlertDialog confirmDialog = new AlertDialog.Builder(view.getContext()).create();
            confirmDialog.setView(dialogView);
            confirmDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));



            dialogView.findViewById(R.id.validate).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmDialog.dismiss();
                    AnswerPoints();
                }
            });
            dialogView.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmDialog.dismiss();
                }
            });
            confirmDialog.show();

        }

    }

    public void retryNetwork(View view) {
        wm.setWifiEnabled(false);
    }

    Boolean DoneAllQuestions() {
        Boolean statement = true;
        for (int i = 0; i < TypedAnswers.size(); i++)
            if (TypedAnswers.get(i).equals("")) {
                statement = false;
                break;
            }
        return statement;
    }

    int AnswerPoints() {
        int score = 0;

        String answers = "";
        for (int i = 0; i < TypedAnswers.size(); i++) {
            answers += TypedAnswers.get(i) + ANSWERS_SEPARATOR;

        }
        try {
            sendRecieve.write(("2]" + answers).getBytes());
            db.pushAnswer(answers, id, 0);
            txt.setText("Waiting for Answer ");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return score;
    }

    private void initExamView() {
        txt = (TextView) findViewById(R.id.txt);
        time = (TextView) findViewById(R.id.time);
        ptime = (ProgressBar) findViewById(R.id.ptime);
        retry = findViewById(R.id.retry);


        LinearQR = (LinearLayout) findViewById(R.id.qr);
        LinearExam = (LinearLayout) findViewById(R.id.exam);

        LinearExam.setVisibility(View.GONE);
        LinearQR.setVisibility(View.VISIBLE);
    }

    public static void InitQR(String name, String matricule, String MAC) {
        Log.e("QR", "DONE THE MAC ADRESS IS {" + MAC + "}");
        MAC_ADDRESS = MAC;
        Bitmap myBitmap = QRCode.from( name + "]" + matricule + "]" + MAC).withSize(700, 700).bitmap();
        QR.setImageBitmap(myBitmap);
    }

    // ___________________________________________________________________
    private void initNetWork() {

        wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wm.isWifiEnabled())
            wm.setWifiEnabled(true);

        QR = (ImageView) findViewById(R.id.imageView2);

        connStatus = (TextView) findViewById(R.id.connectionStatus);

        mNsdManager = (NsdManager) getSystemService(Context.NSD_SERVICE);
        wp2pm = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = wp2pm.initialize(this, getMainLooper(), null);

        mReceiver = new StudentWifiReceiver(wp2pm, mChannel, this);
        sendRecieve=null;
        clientClass=null;
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        registerReceiver(mReceiver, mIntentFilter);
       // startTimedOutTimer();
    }

    public void startDiscovery() {
        wp2pm.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                connStatus.setText("good , Discovery Started");
               // startTimedOutTimer();
            }

            @Override
            public void onFailure(int i) {

            }
        });
    }
    public void stopDiscovery() {
        wp2pm.stopPeerDiscovery(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                connStatus.setText("stoped discovery");
            }

            @Override
            public void onFailure(int i) {

            }
        });
    }

    void InitExam(final Exam e) {
        new CountDownTimer(e.getDuration() * 60 * 1000, 1000) { // Temporary

            public void onTick(long millisUntilFinished) {
                ptime.setMax(e.getDuration() * 60);
                time.setText(millisUntilFinished / 1000 / 60 + ":" + (millisUntilFinished / 1000 - (millisUntilFinished / 1000 / 60) * 60));
                ptime.setProgress((int) (millisUntilFinished / 1000));
            }

            public void onFinish() {
                BtnFinishClicked(ptime.getRootView());
            }
        }.start();

        rv = findViewById(R.id.quest_rv);
        quest_list = e.getQuestions();
        studentExamAdapter = new StudentExamAdapter(quest_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(studentExamAdapter);
        rv.setNestedScrollingEnabled(false);
        studentExamAdapter.notifyDataSetChanged();
        studentExamAdapter.notifyDataSetChanged();
        for (int i = 0; i < quest_list.size(); i++) {
            TypedAnswers.add("");
        }

        LinearExam.setVisibility(View.VISIBLE);
        LinearQR.setVisibility(View.GONE);
    }

    public void sendAnswer(String Answer) {
        try {
            sendRecieve.write(Answer.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void startTimedOutTimer(){
        new CountDownTimer(40000, 1000) {

            public void onTick(long millisUntilFinished) {
                Log.e("Timer "," millis remainins : "+millisUntilFinished);
            }

            public void onFinish() {
                if(!CONNECTED){
                    wm.setWifiEnabled(false);
                    //startTimedOutTimer();
                }
            }
        }.start();

    }
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_READ:
                    byte[] readBuff = (byte[]) msg.obj;
                    String tempMsg = new String(readBuff, 0, msg.arg1);

                    Log.e("CLIENT RECEPTION", "msg : " + tempMsg);
                                                                                                        // CODES DE RECEPTION ETUDIANT
                    if (tempMsg.equals("MAC_REQUEST")) {                                    // CODE 0
                        try {
                            sendRecieve.write(("0]" + MAC_ADDRESS).getBytes());
                            Log.e("MAC", "Sending mac adress to host");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    else if (tempMsg.equals("AUTHENTIFICATION_CONFIRMED")) {                              // CODE 00
                        Log.e("CLIENT RECEPTION", "MAC authentified");
                        CONNECTED=true;
                        Toast.makeText(getApplicationContext(), "Connected succesfully " + tempMsg, Toast.LENGTH_SHORT).show();
                        try {
                            sendRecieve.write("EXAM_REQUEST".getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();}
                    }
                    else if (tempMsg.split("]")[0].equals("1")) {                                  /// CODE 1

                        Log.e("CLIENT RECEPTION", "EXAM RECIEVED ! : " + tempMsg );
                        actualExam = Exam.toExam(tempMsg);
                        Log.e("EXAM ", " WE REVIECED EXAM WITH "+actualExam.getQuestions().size()+" QUESTIONS");
                        InitExam(actualExam);
                        id =(int)db.create(actualExam);

                        /////////////   FORMAT :  ONE]NAME]Module]ID]DURATION]NUMBERQUESTIONS]QUESTION 1]...2] 3 ..

                    } else if (tempMsg.split("]")[0].equals("2")) {                                 // CODE 2

                        String answer = tempMsg.split("]")[1];
                        String[] notemsg = answer.split(ANSWERS_SEPARATOR);

                        Log.e("CLIENT RECEPTION", "NOTE RECIEVED  ! : " + tempMsg
                                + "\n" + " notemsg[]=" + notemsg.length + "  /  exam.questions = " + actualExam.getQuestions().size());

                        for (int i = 0; i < actualExam.getQuestions().size(); i++) {
                            db.create(new Question(actualExam.getQuestions().get(i).getQuestion(),notemsg[i],0,id));
                            Log.e("CREATEDD QUESTION DB "," ID QUESTION EXAM  id = "+id);
                        }
                        Intent resultActivity = new Intent(getApplicationContext(),Student_Result.class);
                        resultActivity.putExtra("id",id);
                        try {
                            sendRecieve.write(("FINISH").getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        wm.setWifiEnabled(false);
                        startActivity(resultActivity);
                        finish();
                    }
                    /////////////   FORMAT :  ONE]NAME]Module]ID]DURATION]NUMBERQUESTIONS]QUESTION 1]...2] 3 ..


                    break;

            }
            return true;
        }
    });

    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            final InetAddress groupOwnerAddress = wifiP2pInfo.groupOwnerAddress;
            Log.e("CONNECTION INFO done ","");
            if (wifiP2pInfo.groupFormed) {


                            connStatus.setText("Client ");
                            clientClass = new ClientClass(groupOwnerAddress.getHostAddress());
                            otherguysIp = groupOwnerAddress.getHostAddress();
                            clientClass.start();


            }
        }
    };

    WifiP2pManager.GroupInfoListener groupeInfoListener = new WifiP2pManager.GroupInfoListener() {
        @Override
        public void onGroupInfoAvailable(WifiP2pGroup wifiP2pInfo) {

            if (wifiP2pInfo.isGroupOwner()) {
                connStatus.setText("Groupe Owner" + wifiP2pInfo.getClientList().size() + ")");

            } else {
                WifiP2pDevice d = wifiP2pInfo.getOwner();
                connStatus.setText("Client (" + wifiP2pInfo.getClientList().size() + ")");

            }
        }
    };

    @Override
    public void onBackPressed() {
        if(sendRecieve==null)
            startActivity(new Intent(this,StudentActivity.class));
    }
    @Override
    public void onPause() {
        super.onPause();
        if(sendRecieve!=null)
        try {
            sendRecieve.write("OUT_OF_APP".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();

        if(sendRecieve!=null)
        try {
            sendRecieve.write("BACK_TO_APP".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
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
            try {
                write(("").getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
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
//                    Log.e("Client","Can Now Send To The Dude ");
//                while(networkState==0)
//                {
//
//
//                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }




}
