package pro.pfe.first;

import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/**
 * Created by wassi on 5/20/2018.
 */

public class StudentSocketAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private List<StudentSocket> Studentlist;
    public DuringHostingActivity dha;
    public StudentSocketAdapter(List<StudentSocket> studentlist,DuringHostingActivity dha) {
        Studentlist = studentlist;
        this.dha=dha;
    }

    public class StudentSocketHolder extends RecyclerView.ViewHolder {
        public TextView nom,status;
        public Button btn;

        public StudentSocketHolder(View view) {
            super(view);
            nom = (TextView) view.findViewById(R.id.name);
            status= (TextView) view.findViewById(R.id.status);
            btn=view.findViewById(R.id.btn);
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
                break;


            case 1:
                hold.status.setText("Connected !");
                break;
            case 2:

                hold.status.setText("Exam envoyé !");
                break;
            case 3:

                hold.status.setText("exam terminé");
                break;
            case 4:

                hold.status.setText("Sorti de l'application !");
                break;
            case 5:

                hold.status.setText("L'etudiant est revenu !");
                break;
            case 6:

                hold.status.setText("Deconnecté");
                break;
        }
    }



    @Override
    public int getItemCount() {
        return Studentlist.size();
    }


}
