package pro.pfe.first;

import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by wassi on 5/20/2018.
 */

public class StudentSocketAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private List<StudentSocket> Studentlist;
    public DuringHostingActivity dha;
    public static int TIMED_OUT_DELAY=25000;
    public StudentSocketAdapter(List<StudentSocket> studentlist,DuringHostingActivity dha) {
        Studentlist = studentlist;
        this.dha=dha;
    }

    public class StudentSocketHolder extends RecyclerView.ViewHolder {
        public TextView nom,status;
        public Button btn;
        ImageView img1,img2,img3,img4;

        public StudentSocketHolder(View view) {
            super(view);
            nom = (TextView) view.findViewById(R.id.name);
            status= (TextView) view.findViewById(R.id.status);
            btn=view.findViewById(R.id.btn);
            img1 =view.findViewById(R.id.simg1);
            img2 =view.findViewById(R.id.simg2);
            img3 =view.findViewById(R.id.simg3);
            img4 =view.findViewById(R.id.simg4);
            }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new StudentSocketHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.student_socket_row, parent, false)); }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final StudentSocket student = Studentlist.get(position);
        final StudentSocketHolder hold = (StudentSocketHolder) holder;
        if(!student.isConnected())
//        new CountDownTimer(45000, 1000) {
//
//            public void onTick(long millisUntilFinished) {
//                Log.e("Timer "," millis remainins  socket: "+millisUntilFinished);
//            }
//
//            public void onFinish() {
//
//               if(!student.isConnected()&& dha.CONNECTING==0)
//                   dha.onRetryStudent(position);
//            }
//        }.start();
        hold.btn.setVisibility(View.INVISIBLE);
        hold.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            dha.onRetryStudent(position);




            }
        });

        hold.nom.setText(student.getName());
        switch (student.getState()) {

            case 0:
                hold.status.setText("connecting");
                hold.img1.setVisibility(View.VISIBLE);
                hold.img2.setVisibility(View.GONE);
                hold.img3.setVisibility(View.GONE);
                hold.img4.setVisibility(View.GONE);
                new CountDownTimer(TIMED_OUT_DELAY, 1000) { // Temporary

                    public void onTick(long millisUntilFinished) {

                    }

                    public void onFinish() {

                        if(student.getState()==0){
                            hold.status.setText("TIMED OUT , Reesayer");
                            hold.btn.setVisibility(View.VISIBLE);
                            dha.onPreRetry(position);
                        }
                    }
                }.start();
                break;


            case 1:

                dha.notfyStudent(position);
                hold.status.setText("Connected !");
                hold.img1.setVisibility(View.GONE);
                hold.img2.setVisibility(View.VISIBLE);
                hold.img3.setVisibility(View.GONE);
                hold.img4.setVisibility(View.GONE);
                break;
            case 2:

                hold.status.setText("Exam envoyé !");
                break;
            case 3:

                hold.status.setText("Commence l'examin");
                //dha.notfyStudent(position);
                break;
            case 4:

                hold.status.setText("Sorti de l'application !");
                dha.notfyStudent(position);
                hold.img1.setVisibility(View.GONE);
                hold.img2.setVisibility(View.GONE);
                hold.img3.setVisibility(View.GONE);
                hold.img4.setVisibility(View.VISIBLE);
                break;
            case 5:
                dha.notfyStudent(position);
                hold.status.setText("L'etudiant est revenu !");
                hold.img1.setVisibility(View.VISIBLE);
                hold.img2.setVisibility(View.GONE);
                hold.img3.setVisibility(View.GONE);
                hold.img4.setVisibility(View.GONE);
                break;
            case 6:
                dha.notfyStudent(position);
                hold.status.setText("Deconnecté");
                student.setSr(null);
                hold.img1.setVisibility(View.GONE);
                hold.img2.setVisibility(View.GONE);
                hold.img3.setVisibility(View.VISIBLE);
                hold.img4.setVisibility(View.GONE);
                break;
            case 7:

                hold.status.setText("Note : "+student.getScore()+"/"+student.getN());
                break;
        }
    }



    @Override
    public int getItemCount() {
        return Studentlist.size();
    }


}
