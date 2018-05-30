package pro.pfe.first;


import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import static pro.pfe.first.Student_Lobby.ANSWERS_SEPARATOR;
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
        public TextView Title, description,students;
        View layout;


        public HostedExamViewHolder(View view) {
            super(view);
            layout=view.findViewById(R.id.layout);
            Title =  view.findViewById(R.id.title);
            description = view.findViewById(R.id.rmatricule);
            students = view.findViewById(R.id.std);


        }
    }
    public class HistoryExamView extends RecyclerView.ViewHolder {
        public TextView Title,nom,matricule,note;
        View layout;


        public HistoryExamView(View view) {
            super(view);
            layout=view.findViewById(R.id.layout);
            Title =  view.findViewById(R.id.title);
            matricule = view.findViewById(R.id.rmatricule);
            note=view.findViewById(R.id.rnote);


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

                    historyholder.Title.setText(historyexam.getTitre());
                    historyholder.matricule.setText(historyexam.getModule());
                    String source="";
                    for(Question q:historyexam.getQuestions()){
                        source+=q.getAnswer()+ ANSWERS_SEPARATOR;
                    }

                    DB db = new DB(historyholder.layout.getContext());
                    float totalQuestions=historyexam.getNoteTotal();
                    float bonneReponses=Exam.CalculerNote(historyexam,db.getStudentAnswer(0,historyexam.getId()));
                    historyholder.note.setText((int)(bonneReponses/totalQuestions*100)+"% ("+bonneReponses+"/"+totalQuestions+")");
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

                if(hostedexam!=null)
                {
                    final HostedExamViewHolder hostedholder = (HostedExamViewHolder) holder;
                    hostedholder.Title.setText(hostedexam.getTitre());
                    DB bdd=new DB(hostedholder.layout.getContext());
                    hostedholder.description.setText(hostedexam.getModule());
                    hostedholder.students.setText(bdd.getStudentWithExam(hostedexam.getId()).size()+" éleves");
                    hostedholder.layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent hostedActivity = new Intent(view.getContext(), Teacher_Done_Exam.class);
                            hostedActivity.putExtra("id", hostedexam.getId());
                            view.getContext().startActivity(hostedActivity);
                        }

                    });}
                break;
        }
    }


    void HostThisExam(View v,Exam e){
        // launch the Hosting activity after passing data
        Intent HostingIntent=new Intent(v.getContext(),DuringHostingActivity.class);
        HostingIntent.putExtra("Exam",e.getId());
        HostingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
        if(Examlist.size()==0)
            Teacher_Tab1.number.setText("Il n ya pas d'examins");
        else if(Examlist.size()==1)
            Teacher_Tab1.number.setText("1 Examin");
        else
            Teacher_Tab1.number.setText(Examlist.size()+" Examins");
        notifyItemRemoved(index);
    }
    @Override
    public int getItemCount() {
       return Examlist.size();
    }


}
