package pro.pfe.first;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

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

        RecyclerView recyclerView = findViewById(R.id.studntscroll);
        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        StudentAdapter adapter = new StudentAdapter(students,examin.getId());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        RecyclerView rv = findViewById(R.id.correction);
        MyQuestionAnswerRecyclerViewAdapter qAdapter= new MyQuestionAnswerRecyclerViewAdapter(examin.getQuestions(),db.getStudentAnswer(students.get(0).getID(),examin.getId()));
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(qAdapter);
        qAdapter.notifyDataSetChanged();
    }

}
