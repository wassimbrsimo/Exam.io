package pro.pfe.first;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import static pro.pfe.first.Teacher_Tab1.getExamIndexByID;


public class ExamListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private List<Exam> Examlist;
    private int isHistory;
    public class ExamViewHolder extends RecyclerView.ViewHolder {
        public TextView Title,Module,question,duration;
        public RecyclerView r;
        public View question_panel;
        public Button host;
        ImageButton Delete;
        public Switch answer_toggle;
        public CardView card;
        public ExamViewHolder(View view) {
            super(view);
            Title = (TextView) view.findViewById(R.id.rtitre);
            Module = (TextView) view.findViewById(R.id.module);
            host =  view.findViewById(R.id.host);
            card = view.findViewById(R.id.card);
            question=(TextView)view.findViewById(R.id.nquest);
            duration=view.findViewById(R.id.duration);
            Delete =(ImageButton) view.findViewById(R.id.delete);
        }
    }

    public class HostedExamViewHolder extends RecyclerView.ViewHolder {
        public TextView Title,nom,matricule,note;
        View layout;
        RecyclerView rv;


        public HostedExamViewHolder(View view) {
            super(view);
            layout=view.findViewById(R.id.layout);
            Title =  view.findViewById(R.id.title);
            matricule = view.findViewById(R.id.rmatricule);
            nom=view.findViewById(R.id.name);
            note= view.findViewById(R.id.note_int);
            rv = view.findViewById(R.id.students_list);


        }
    }
    public class HistoryExamView extends RecyclerView.ViewHolder {
        public TextView Title,nom,matricule,note,desc;
        View layout;
        RecyclerView rv;


        public HistoryExamView(View view) {
            super(view);
            layout=view.findViewById(R.id.layout);
            Title =  view.findViewById(R.id.title);
            desc=view.findViewById(R.id.desc);
            matricule = view.findViewById(R.id.rmatricule);
            nom=view.findViewById(R.id.rname);
            note= view.findViewById(R.id.rnote);
            rv = view.findViewById(R.id.students_list);


        }
    }

    public ExamListAdapter(List<Exam> List,int isHistory) {
        this.Examlist = List;
        this.isHistory = isHistory;
    }

    @Override
    public int getItemViewType(int position) {
      return isHistory;
    }

    public static float CalculNote(String source,String reponse){
        String[] s=source.split(Student_Lobby.ANSWERS_SEPARATOR);
        String[] r=reponse.split(Student_Lobby.ANSWERS_SEPARATOR);
        float score=0;
        for(int i=0;i<s.length;i++){
            if(r.length>i && !r[i].equals("") && s[i].equals(r[i]))
                score++;
        }
        score=score/(s.length)*100;
return score;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView ;
        if(viewType==0) {
            itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.exam_row, parent, false);
            return new ExamViewHolder(itemView);
        }
        else if(viewType==1)
        {
            //RETURN STUDENT HISTORY
            itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.history_exam_row, parent, false);
            return  new HistoryExamView(itemView);
        }
        else
        {   //RETURN TEACHER RESULTS ACTIVITY
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
                    hold.duration.setText(exam.getDuration()+" Mins");
                    hold.question.setText(exam.getQuestions().size()+" Questions");
                    hold.card.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(view.getContext(),Edit_Exam.class);
                            intent.putExtra("id",exam.getId());
                            String transitionName = "hello";
                            View viewStart = hold.card;
                            ActivityOptionsCompat options =
                                    ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) view.getContext(),
                                            viewStart,
                                            transitionName);
                            ActivityCompat.startActivity(view.getContext(), intent, options.toBundle());
                        }
                    });
                 hold.Delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            DeleteExam(exam.getId());
                        }
                    });
                 hold.host.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            HostThisExam(view ,exam);

                        }
                    });
            break;
            case 1:
                final Exam historyexam = Examlist.get(position);
                    final HistoryExamView historyholder = (HistoryExamView) holder;

                    historyholder.desc.setText(String.valueOf(historyexam.getQuestions().size()));
                    historyholder.Title.setText(historyexam.getTitre());
                    historyholder.matricule.setText(historyexam.getModule() );
                    historyholder.nom.setText(historyexam.getTitre());
                    String source="";
                    for(Question q:historyexam.getQuestions()){
                        source+=q.getAnswer()+Student_Lobby.ANSWERS_SEPARATOR;
                    }

                    DB db = new DB(historyholder.layout.getContext());
                    String score = String.format("%.2f", CalculNote(source,db.getStudentAnswer(0,historyexam.getId())));
                    historyholder.note.setText(score+"%");
                    historyholder.layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent historyActivity=new Intent(view.getContext(),Student_Result.class);
                            historyActivity.putExtra("id",historyexam.getId());
                            view.getContext().startActivity(historyActivity);
                        }
                    });

                break;
            case 2:
                final Exam hostedexam = Examlist.get(position);
                    final HostedExamViewHolder hostedholder = (HostedExamViewHolder) holder;
                    hostedholder.Title.setText(hostedexam.getTitre());
                    hostedholder.matricule.setText(hostedexam.getModule());
                    hostedholder.nom.setText(hostedexam.getTitre());
                    hostedholder.note.setText("Questions : "+hostedexam.getQuestions().size());
                    hostedholder.layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent hostedActivity=new Intent(view.getContext(),Teacher_Done_Exam.class);
                            hostedActivity.putExtra("id",hostedexam.getId());
                            view.getContext().startActivity(hostedActivity);
                        }
                    });
                break;
        }
    }


    void HostThisExam(View v,Exam e){
        // launch the Hosting activity after passing data
        Intent HostingIntent=new Intent(v.getContext(),DuringHostingActivity.class);
        HostingIntent.putExtra("Exam",e.getId());
        v.getContext().startActivity(HostingIntent);
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
