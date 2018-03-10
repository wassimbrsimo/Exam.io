package pro.pfe.first;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class DuringExamActivity extends AppCompatActivity {
        RecyclerView rv;
        Button done_exam;
        ExamAdapter examAdapter;
        List<Question> quest_list = new ArrayList<>();

    public static void setTypedAnswers(List<String> typedAnswers) {
        TypedAnswers = typedAnswers;
    }

    public static List<String> TypedAnswers = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examination);
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
}
