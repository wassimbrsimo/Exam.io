package pro.pfe.first;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.ListView;
import android.widget.TextView;


public class Student_Result extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student__result);
        DB db=new DB(getApplicationContext());
        TextView note,nom,matricule;
        ListView correction;
        String reponse=db.getStudentAnswer(getIntent().getIntExtra("student",0),getIntent().getIntExtra("exam",0));
    }
    @Override
    public void onBackPressed()
    {
        Intent StudentActivity = new Intent(this, pro.pfe.first.StudentActivity.class);
        startActivity(StudentActivity);
    }
}
