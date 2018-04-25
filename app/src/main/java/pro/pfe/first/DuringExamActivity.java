package pro.pfe.first;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import static pro.pfe.first.Teacher.db;

public class DuringExamActivity extends AppCompatActivity {
        RecyclerView rv;
        Button done_exam;
        ExamAdapter examAdapter;
        List<Question> quest_list = new ArrayList<>();
        TextView txt,time;
        ProgressBar ptime;
    public static List<String> TypedAnswers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examination);
        txt=(TextView) findViewById(R.id.txt);
        time=(TextView) findViewById(R.id.time);
        ptime=(ProgressBar) findViewById(R.id.ptime);


        rv= findViewById(R.id.quest_rv);
        examAdapter = new ExamAdapter(quest_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(examAdapter);
        examAdapter.notifyDataSetChanged();
    }
    void InitExam(final Exam e){
        new CountDownTimer(e.getDuration()*60*1000, 1000){ // Temporary

            public void onTick(long millisUntilFinished){
                ptime.setMax(e.getDuration()*60);
                time.setText(millisUntilFinished/1000/60+":"+(millisUntilFinished/1000-(millisUntilFinished/1000/60)*60));
                ptime.setProgress((int) (millisUntilFinished/1000));
            }
            public  void onFinish(){
                BtnFinishClicked(null);
            }
        }.start();

        quest_list=Teacher_Tab1.Examlist.get(0).getQuestions();
        for(int i=0 ; i<quest_list.size();i++)
        {
            TypedAnswers.add("");
        }
    }
    public void BtnFinishClicked(View view){
        if(DoneAllQuestions())
           txt.setText("Your Score is : "+AnswerPoints()+"/"+TypedAnswers.size());
        else
            txt.setText("please answer To All the questions");
    }
    Boolean DoneAllQuestions(){
        Boolean statement=true;
        for(int i =0;i<TypedAnswers.size();i++)
            if(TypedAnswers.get(i).equals(""))
            {statement=false;
             break;
            }
        return statement;
    }

    int AnswerPoints(){
        int score=0;
        String answers="";
        for(int i =0;i<TypedAnswers.size();i++) {
            answers+=TypedAnswers.get(i)+"/";
            if (TypedAnswers.get(i).equals(quest_list.get(i).getAnswer().toString())) {
                score++;
            }
        }
        //db.pushAnswerStudent(answers,Teacher.Examlist.get(0).getId(),0);
        return score;
    }
}
