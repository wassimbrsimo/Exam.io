package pro.pfe.first;


import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static pro.pfe.first.Teacher.db;
import static pro.pfe.first.Teacher_Tab1.getExamIndexByID;


public class ExamListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private List<Exam> Examlist;
    Boolean HOSTED;
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
            Title = (TextView) view.findViewById(R.id.titre);
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
            matricule = view.findViewById(R.id.matricule);
            nom=view.findViewById(R.id.name);
            note= view.findViewById(R.id.note_int);
            rv = view.findViewById(R.id.students_list);


        }
    }

    public ExamListAdapter(List<Exam> List,Boolean hosted) {
        this.Examlist = List;
        this.HOSTED = hosted;
    }

    @Override
    public int getItemViewType(int position) {
       if(HOSTED)
            return 1;
        else
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
            hold.duration.setText(exam.getDuration()+" Mins");
            hold.question.setText(exam.getQuestions().size()+" Questions");
//            hold.Delete.setId(exam.getId());
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
                                    transitionName
                            );
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
                final Exam hostedexam = Examlist.get(position);
                final HostedExamViewHolder hostedholder = (HostedExamViewHolder) holder;
                hostedholder.Title.setText(hostedexam.getTitre());
                hostedholder.matricule.setText(hostedexam.getModule() );// get the dude data
                hostedholder.nom.setText(hostedexam.getTitre());
                hostedholder.note.setText("note moyenne : ..");
                StudentAdapter sAdapter=new StudentAdapter(db.getStudentWithExam(hostedexam.getId()),hostedexam.getId());
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(hostedholder.Title.getContext());
                hostedholder.rv.setLayoutManager(layoutManager);
                hostedholder.rv.setAdapter(sAdapter);
                sAdapter.notifyDataSetChanged();
                hostedholder.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(hostedholder.rv.getVisibility()==View.VISIBLE)
                        hostedholder.rv.setVisibility(View.GONE);
                        else
                            hostedholder.rv.setVisibility(View.VISIBLE);
                        /*  Intent Teacher_Done_Exam = new Intent(view.getContext(),Teacher_Done_Exam.class);
                        Teacher_Done_Exam.putExtra("id",hostedexam.getId());
                        view.getContext().startActivity(Teacher_Done_Exam);
                    */}
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
