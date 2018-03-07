package pro.pfe.first;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import static pro.pfe.first.Teacher.QUESTION_ID_MANAGER;
import static pro.pfe.first.Teacher.db;
import static pro.pfe.first.Teacher.getExamIndexByID;


public class ExamAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private List<Exam> Examlist;

    public class ExamViewHolder extends RecyclerView.ViewHolder {
        public TextView Title,Module,question;
        public RecyclerView r;
        public View question_panel;
        public Button Questions,Delete,add_question;
        public Switch answer_toggle;

        public ExamViewHolder(View view) {
            super(view);
            Title = (TextView) view.findViewById(R.id.titre);
            Module = (TextView) view.findViewById(R.id.module);
            add_question=(Button)view.findViewById(R.id.add_question);
            answer_toggle=(Switch) view.findViewById(R.id.toggleButton);

            question=(TextView)view.findViewById(R.id.question_text);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
            r=(RecyclerView) view.findViewById(R.id.recyclerview_question);
            r.setLayoutManager(layoutManager);
            question_panel= view.findViewById(R.id.question_panel);
            Questions=(Button) view.findViewById(R.id.edit);
            Delete =(Button) view.findViewById(R.id.delete);
        }
    }

    public ExamAdapter(List<Exam> List){
        this.Examlist=List;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.exam_row, parent, false);
        return new ExamViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final Exam  exam =Examlist.get(position);
        final ExamViewHolder hold=(ExamViewHolder)holder;
        hold.Title.setText(exam.getTitre());
        hold.Module.setText(exam.getModule());
        hold.Delete.setId(exam.getId());

        final QuestionAdapter qAdapter = new QuestionAdapter(exam.questions);
        hold.r.setAdapter(qAdapter);
        qAdapter.notifyDataSetChanged();


        hold.Questions.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                ShowQuestions(hold,exam);
            }
        });

        hold.Delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                DeleteExam(exam.getId());
            }
        });

        hold.add_question.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                AddQuestion(hold.question.getText().toString(),hold.answer_toggle.isChecked(),exam.getId());
                qAdapter.notifyItemInserted(Examlist.get(getExamIndexByID(exam.getId())).getQuestions().size());
            }
        });




    }
    public  void AddQuestion(String question,Boolean reponse,int id){
        Question q= new Question(0,question,reponse,QUESTION_ID_MANAGER++,id);
        db.create(q);
        Examlist.get(getExamIndexByID(id)).questions.add(q);

    }

    public void ShowQuestions(ExamViewHolder hold,Exam exam){
        if (hold.isRecyclable()){
            hold.setIsRecyclable(false);

            hold.question_panel.setVisibility(View.VISIBLE);
        }else {
            hold.setIsRecyclable(true);
            hold.question_panel.setVisibility(View.GONE);
        }
    }
    public void DeleteExam(int id){

        Teacher.db.DeleteExam(id);
        int index =getExamIndexByID(id);
        Examlist.remove(getExamIndexByID(id));
        notifyItemRemoved(index);
    }
    @Override
    public int getItemCount() {
       return Examlist.size();
    }


}