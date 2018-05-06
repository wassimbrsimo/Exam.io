package pro.pfe.first;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

import static pro.pfe.first.Teacher.db;

public class Teacher_Done_Exam extends AppCompatActivity {
    ArrayList<Student> students=new ArrayList<>();
    Exam examin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher__done__exam);
        examin=db.getExam(getIntent().getIntExtra("id",0));
        students=db.getStudentWithExam(examin.getId());
    }

}
