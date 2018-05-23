package pro.pfe.first;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import static pro.pfe.first.StudentActivity.Etudiant;
import static pro.pfe.first.StudentActivity.db;


public class Student_Result extends AppCompatActivity {
    TextView note,nom,matricule,titre;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student__result);


        note=findViewById(R.id.rnote);
        nom=findViewById(R.id.rname);
        matricule=findViewById(R.id.rmatricule);
        titre=findViewById(R.id.rtitre);

    }


    @Override
    protected void onStart()
    {
        super.onStart();
        int id=getIntent().getIntExtra("id",-1);
        RecyclerView rv = (RecyclerView) findViewById(R.id.recyclerview);
        Exam exam=db.getExam(id);
        Log.e("EXAM TO TREAT : "+exam.getTitre()," )QUESTION : "+exam.getQuestions().size());
        MyQuestionAnswerRecyclerViewAdapter eAdapter=new MyQuestionAnswerRecyclerViewAdapter(exam.getQuestions(),db.getStudentAnswer(0,id));
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(eAdapter);
        rv.setNestedScrollingEnabled(false);
        eAdapter.notifyDataSetChanged();

        String scoremsg="2]";
        int score = 0;
        String answers=db.getStudentAnswer(0,id);
        String[] TypedAnswer = answers.split(Student_Lobby.ANSWERS_SEPARATOR);
        String student_answer="";
        for(int i =0;i<TypedAnswer.length;i++){
            Log.e("CORECTION   ","         BONNE REPONSE : "+exam.getQuestions().get(i).getAnswer()+" TA REPONSE : "+TypedAnswer[i]);
            student_answer+=TypedAnswer[i]+Student_Lobby.ANSWERS_SEPARATOR;
            scoremsg+=exam.getQuestions().get(i).getAnswer()+Student_Lobby.ANSWERS_SEPARATOR;
            if(TypedAnswer[i].equals(exam.getQuestions().get(i).getAnswer()))
                score++;

            note.setText(score+"/"+exam.getQuestions().size());
            nom.setText(Etudiant.getName());
            matricule.setText(Etudiant.getMatricule());
            titre.setText(exam.getTitre());
        }
    }

    public void goBackPressed(View v){
        Intent home = new Intent(this,StudentActivity.class);
        startActivity(home);
    }
    @Override
    public void onBackPressed()
    {
       goBackPressed(getCurrentFocus());
    }
}
