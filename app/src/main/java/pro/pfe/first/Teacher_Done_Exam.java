package pro.pfe.first;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import static pro.pfe.first.Teacher.db;

public class Teacher_Done_Exam extends AppCompatActivity {
    ArrayList<Student> students=new ArrayList<>();
    public String studentAnswer;
    Exam examin;
    RecyclerView rv;
    TextView note,nom;
    public MyQuestionAnswerRecyclerViewAdapter qAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher__done__exam);
        examin=db.getExam(getIntent().getIntExtra("id",0));
        students=db.getStudentWithExam(examin.getId());
        note=findViewById(R.id.note);
        nom=findViewById(R.id.name);
        RecyclerView recyclerView = findViewById(R.id.studntscroll);
        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        StudentAdapter adapter = new StudentAdapter(students,examin.getId(),this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        studentAnswer=db.getStudentAnswer(students.get(0).getID(),examin.getId());

        rv = findViewById(R.id.correction);
        setupRv(examin.getQuestions(),studentAnswer,students.get(0).getName());

    }

    public void setupRv(ArrayList<Question> array,String answer,String name){
        note.setText(Exam.CalculerNote(examin,answer)+"/"+examin.getQuestionsSize());
        nom.setText(name);
        qAdapter= new MyQuestionAnswerRecyclerViewAdapter(array,answer);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(qAdapter);
        qAdapter.notifyDataSetChanged();

    }
    public void goBackPressed(View v){
        Intent home = new Intent(this,Teacher.class);
        startActivity(home);
    }
}
