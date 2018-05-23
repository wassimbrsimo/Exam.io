
package pro.pfe.first;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.wifi.WifiManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.glxn.qrgen.android.QRCode;

import java.util.ArrayList;
import java.util.List;

public class StudentActivity extends FragmentActivity {
    public static  DB db;
    public static Student Etudiant;
    public static float NOTE=0;
    TextView avg,name;
    List<Exam> Examlist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
         db = new DB(getApplicationContext());
        if(!db.isStudentExists(false,"")){
            Intent StudentProfile=new Intent(this,Student_Profile.class);
            startActivity(StudentProfile);
        }
        else{


            Etudiant=db.getAllStudents().get(0);

        Examlist= db.getHostedExams();
        float average=0;
        for(Exam e : Examlist){
            average+=ExamListAdapter.CalculNote(e.getAnswers(),db.getStudentAnswer(0,e.getId()));
        }
        average/=Examlist.size();
        avg=findViewById(R.id.avg_note);
        avg.setText("Moyenne : "+average+"%");
        name=findViewById(R.id.profil_name);
        name.setText(Etudiant.getName());
        RecyclerView rv = findViewById(R.id.rv_passed_exams);
        ExamListAdapter eAdapter=new ExamListAdapter(Examlist,1);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(eAdapter);
        rv.setNestedScrollingEnabled(false);
        eAdapter.notifyDataSetChanged();
            CollapsingToolbarLayout collapsingToolbarLayout;
            collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
            collapsingToolbarLayout.setTitle(Etudiant.getName());
            collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));


        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wm.setWifiEnabled(false);

    }
    public void passExam(View view){
        Intent Lobby=new Intent(getApplicationContext(),Student_Lobby.class);
        startActivity(Lobby);
        //todo: Animation ..
    }
    @Override
    public void onBackPressed() {
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();

    }

}
