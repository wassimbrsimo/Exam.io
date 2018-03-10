package pro.pfe.first;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class DuringExamActivity extends AppCompatActivity {
        RecyclerView rv;
        Button done_exam;
        ExamAdapter examAdapter;
        List<Question> quest_list = new ArrayList<>();
        TextView txt;
    public static List<String> TypedAnswers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examination);
        txt=(TextView) findViewById(R.id.txt);
        for(int i=0 ; i<20;i++)
        {
            quest_list.add(new Question(0,"es ce que 1+"+i+" = "+String.valueOf(3),(i+1)==3,i,0));
            TypedAnswers.add("");
        }
        rv= findViewById(R.id.quest_rv);
        examAdapter = new ExamAdapter(quest_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(examAdapter);
        examAdapter.notifyDataSetChanged();
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
        for(int i =0;i<TypedAnswers.size();i++)
            if(TypedAnswers.get(i).equals(quest_list.get(i).getAnswer().toString()))
            {score++;
            }
        return score;
    }
}
