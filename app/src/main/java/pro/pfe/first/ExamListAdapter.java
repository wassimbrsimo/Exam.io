package pro.pfe.first;


import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import static pro.pfe.first.Teacher.db;
import static pro.pfe.first.Teacher_Tab1.getExamIndexByID;


public class ExamListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private List<Exam> Examlist;

    public class ExamViewHolder extends RecyclerView.ViewHolder {
        public TextView Title,Module,question;
        public RecyclerView r;
        public View question_panel;
        public Button Questions,Delete,add_question,host;
        public Switch answer_toggle;

        public ExamViewHolder(View view) {
            super(view);
            Title = (TextView) view.findViewById(R.id.titre);
            Module = (TextView) view.findViewById(R.id.module);
            add_question=(Button)view.findViewById(R.id.add_question);
            answer_toggle=(Switch) view.findViewById(R.id.toggleButton);
            host = (Button) view.findViewById(R.id.host);

            question=(TextView)view.findViewById(R.id.question_text);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
            r=(RecyclerView) view.findViewById(R.id.recyclerview_question);
            r.setLayoutManager(layoutManager);
            question_panel= view.findViewById(R.id.question_panel);
            Questions=(Button) view.findViewById(R.id.edit);
            Delete =(Button) view.findViewById(R.id.delete);
        }
    }

    public class HostedExamViewHolder extends RecyclerView.ViewHolder {
        public TextView Title,nom,matricule,note,answers;

        public HostedExamViewHolder(View view) {
            super(view);
            Title =  view.findViewById(R.id.title);
            matricule = view.findViewById(R.id.matricule);
            nom=view.findViewById(R.id.name);
            note= view.findViewById(R.id.note_int);
            answers= view.findViewById(R.id.answers);

        }
    }

    public ExamListAdapter(List<Exam> List){
        this.Examlist=List;
    }

    @Override
    public int getItemViewType(int position) {
       /* if(Teacher.VIEWHOSTEDEXAM)
            return 1;
        else
            return 0;*/
    return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView ;
        if(viewType==0) {
            itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.exam_row, parent, false);
            return new ExamViewHolder(itemView);
        }
        else
        {
            itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.hosted_exam_row, parent, false);
            return  new HostedExamViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case 0:
            final Exam exam = Examlist.get(position);
            final ExamViewHolder hold = (ExamViewHolder) holder;
            hold.Title.setText(exam.getTitre());
            hold.Module.setText(exam.getModule());
            hold.Delete.setId(exam.getId());

            final QuestionAdapter qAdapter = new QuestionAdapter(exam.questions);
            hold.r.setAdapter(qAdapter);
            qAdapter.notifyDataSetChanged();


            hold.Questions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShowQuestions(hold, exam);
                }
            });

            hold.Delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DeleteExam(exam.getId());
                }
            });

            hold.add_question.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AddQuestion(hold.question.getText().toString(), hold.answer_toggle.isChecked(), exam.getId());
                    qAdapter.notifyItemInserted(Examlist.get(getExamIndexByID(exam.getId())).getQuestions().size());
                }
            });
            hold.host.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HostThisExam(view ,exam.getId());

                }
            });
            break;
            case 1:
                final Exam hostedexam = Examlist.get(position);
                final HostedExamViewHolder hostedholder = (HostedExamViewHolder) holder;
                hostedholder.Title.setText(hostedexam.getTitre());
                hostedholder.matricule.setText(db.getDatabaseName());// get the dude data
                hostedholder.nom.setText("");
                hostedholder.note.setText("");
                hostedholder.answers.setText("");
                break;
        }
    }
    void HostThisExam(View v,int ID){
        // launch the Hosting activity after passing data
        Intent HostingIntent=new Intent(v.getContext(),DuringHostingActivity.class);
       // HostingIntent.putExtra("Exam_ID",ID);
        v.getContext().startActivity(HostingIntent);
    }
    public  void AddQuestion(String question,Boolean reponse,int id){
        Question q= new Question(0,question,reponse,-1,id);
        int identity=(int)db.create(q);
        q.setId(identity);
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
