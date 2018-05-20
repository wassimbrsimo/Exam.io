
package pro.pfe.first;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.wifi.WifiManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import net.glxn.qrgen.android.QRCode;

import java.util.ArrayList;
import java.util.List;

public class StudentActivity extends FragmentActivity {
    public static  DB db;
    public static Student Etudiant;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        db = new DB(getApplicationContext());
        List<Exam> Examlist= db.getAllExams();
        RecyclerView rv = findViewById(R.id.rv_passed_exams);
        ExamListAdapter eAdapter=new ExamListAdapter(Examlist,true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(eAdapter);
        eAdapter.notifyDataSetChanged();

        if(!db.isStudentExists(false,"")){
            Intent StudentProfile=new Intent(this,Student_Profile.class);
            startActivity(StudentProfile);
        }
        else{
            Etudiant=db.getAllStudents().get(0);
            Log.e("Etudiant ","Confirmé " +db.getAllStudents().get(0).getName()+ " size :"+db.getAllStudents().size());
        }
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wm.setWifiEnabled(false);

    }

    public void passExam(View view){
        Intent Lobby=new Intent(getApplicationContext(),Student_Lobby.class);
        startActivity(Lobby);
        //todo: Animation ..
    }

}
