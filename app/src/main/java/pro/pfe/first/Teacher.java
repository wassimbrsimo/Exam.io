package pro.pfe.first;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class Teacher extends AppCompatActivity implements  ZXingScannerView.ResultHandler {
    public static List<Exam> Examlist=new ArrayList<Exam>();
    public static ExamListAdapter eAdapter;
    RecyclerView rv;
    public static DB db;
    public static Boolean VIEWHOSTEDEXAM=false;

    private ZXingScannerView zxing;
    View addPanel,addqPanel ;
    Button hosted,add,close,validate;
    TextView titre,module,question;
    RadioButton t,f;
    NumberPicker np;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        np=findViewById(R.id.numberPicker);
        final String[] values= {"15 mins","30 mins", "45 mins", "1 heure", "2 heures"};
        np.setMinValue(0);
        np.setValue(0);
        np.setMaxValue(values.length-1);
        np.setDisplayedValues(values);
        np.setWrapSelectorWheel(true);



        addPanel= findViewById(R.id.add_epanel);
        close= findViewById(R.id.add_qbtn);
        add= findViewById(R.id.create);
        validate= findViewById(R.id.add_btn);
        titre= findViewById(R.id.add_titre);
        module=findViewById(R.id.add_module);
        question=findViewById(R.id.add_question);

        db = new DB(getApplicationContext());

        Examlist= db.getAllExams();

        eAdapter=new ExamListAdapter(Examlist);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rv = (RecyclerView) findViewById(R.id.recyclerview_Teacher);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(eAdapter);
        eAdapter.notifyDataSetChanged();

        hosted=findViewById(R.id.Hosted);
        hosted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (VIEWHOSTEDEXAM){
                    VIEWHOSTEDEXAM=false;

                }
                else {
                    VIEWHOSTEDEXAM=true;
                }
            }
        });
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
        int dur=0;
        switch (np.getValue()){
            case 0:
                dur=1;
                break;
            case 1:
                dur=30;
                break;
            case 2:
                dur=45;
                break;
            case 3:
                dur=60;
                break;
            case 4:
                dur=120;
                break;
        }

        Exam e = new Exam(titre.getText().toString(),module.getText().toString(),-1,dur);
        long id=db.create(e);
        e.setId((int)id);
        Examlist.add(e);
        eAdapter.notifyItemInserted(Examlist.size());
        AddExamToggler(view);
    }

    public void HostExam(View view){
       zxing = new ZXingScannerView(getApplicationContext());
       setContentView(zxing);
       zxing.setResultHandler(this);
       zxing.startCamera();
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

    @Override
    public void handleResult(Result result) {
        zxing.removeAllViews();
        zxing.stopCamera();
        Toast.makeText(getApplicationContext(), "Scanned : \" "+result.getText()+" \"", Toast.LENGTH_SHORT).show();
        setContentView(R.layout.activity_teacher);
    }
}
