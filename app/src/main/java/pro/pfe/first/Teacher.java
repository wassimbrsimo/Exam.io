package pro.pfe.first;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Teacher extends AppCompatActivity {
    public static List<Exam> Examlist=new ArrayList<Exam>();
    public static ExamAdapter eAdapter;
    public static int EXAM_ID_MANAGER=0,QUESTION_ID_MANAGER=0;
    RecyclerView rv;
    public static DB db;
    View addPanel,addqPanel ;
    Button qvalidate,add,close,validate;
    TextView titre,module,question;
    RadioButton t,f;
    //public static int Toggeled_Exam_Id=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        addPanel= findViewById(R.id.add_epanel);
        close= findViewById(R.id.add_qbtn);
        add= findViewById(R.id.create);
        validate= findViewById(R.id.add_btn);
        titre= findViewById(R.id.add_titre);
        module=findViewById(R.id.add_module);
        question=findViewById(R.id.add_question);

        db = new DB(getApplicationContext());

        Examlist= db.getAllExams();
        //for(int j=0;j<1000;j++)
        //    Examlist.add(new Exam("KHRA","ZBEL",Examlist.size()));

        eAdapter=new ExamAdapter(Examlist);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rv = (RecyclerView) findViewById(R.id.recyclerview_Teacher);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(eAdapter);
        eAdapter.notifyDataSetChanged();
    }
    @Override
    public void onStart(){
        super.onStart();

    }
    public void AddExamToggler(View view){
        if(addPanel.getVisibility()==View.GONE) {
            addPanel.setVisibility(View.VISIBLE);
            add.setVisibility(View.GONE);
        }
        else {
            addPanel.setVisibility(View.GONE);
            titre.setText("");module.setText("");
            add.setVisibility(View.VISIBLE);
        }
    }

    public void AddExam(View view){
        Exam e = new Exam(titre.getText().toString(),module.getText().toString(),EXAM_ID_MANAGER++);
        db.create(e);
        Examlist.add(e);
        eAdapter.notifyItemInserted(Examlist.size());
        AddExamToggler(view);
    }


    public void FormatExams(View view){
        db.FormatExams();
        eAdapter.notifyDataSetChanged();
    }

    public int grabExamNextAvailableID(){
        int ID = Examlist.size();
        for(int j =0;j<Examlist.size();j++)
            for(int i=0;i<Examlist.size();i++){
                if(j==Examlist.get(i).getId())
                {
                    break;
                }
                ID=j;
            }
        return ID;
    }

    public static int getExamIndexByID(int ID) {
        int index=-1;
        for (int i = 0; i < Examlist.size(); i++)
            if (Examlist.get(i).getId() == ID){
                index=i;
                break;
        }
        return index;
    }
    public static int getQuestionIndexByID(int ID,int ExamIndex){
        int index=-1;
        for (int j = 0; j < Examlist.get(ExamIndex).getQuestions().size(); j++){
            if (Examlist.get(ExamIndex).getQuestions().get(j).getId() ==ID){
                index=j;
                break;
            }}
        return index;
    }
}
