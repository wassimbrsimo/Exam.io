package pro.pfe.first;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import static pro.pfe.first.Teacher.db;

public class Edit_Exam extends AppCompatActivity {
    private Exam examin;
    Button toggle_vf,toggle_multi;
    TextView titre,module;
    public static RecyclerView rv;
    QuestionAdapter qAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__exam);
        examin= db.getExam(getIntent().getIntExtra("id",0));
        titre = findViewById(R.id.rtitre);
        module= findViewById(R.id.module);
        rv = (RecyclerView) findViewById(R.id.quest_list);
        titre.setText(examin.getTitre());
        module.setText(examin.getModule());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);
        rv.setNestedScrollingEnabled(false);
        qAdapter = new QuestionAdapter(examin.getQuestions());
        rv.setAdapter(qAdapter);
        qAdapter.notifyDataSetChanged();
        toggle_vf = findViewById(R.id.bool_btn);
        toggle_multi =findViewById(R.id.multi_btn);

        toggle_vf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addQuestion(0);
            }
        });
        toggle_multi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addQuestion(1);
            }
        });
    }
    private void addQuestion(int type ){
        ArrayList<String> temp=new ArrayList<String>();
        temp.add("");
        if(type==1) {
            temp.add("ce ci est n choi");
            temp.add("la deuxieme choice");
        }      Question q= new Question(temp,"00000",-1,examin.getId());
        int identity=(int)db.create(q);
        q.setId(identity);
        examin.getQuestions().add(q);
        qAdapter.notifyDataSetChanged();
        rv.scrollBy(0,9999);
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent Teach=new Intent(this,Teacher.class);
        startActivity(Teach);
    }

}
