package pro.pfe.first;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;


public class StudentExamAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<Question> liste_des_questions;
    StudentExamAdapter(List<Question> list){
        this.liste_des_questions=list;
    }

    public class TFHolder extends RecyclerView.ViewHolder{
        TextView question;
        View question_row;
        public TFHolder(View view) {
            super(view);
            this.question=view.findViewById(R.id.question_text);
            this.question_row= view.findViewById(R.id.question_row);

        }
    }

    public class MultiHolder extends RecyclerView.ViewHolder{
        TextView question;
        TextView[] choices;
        CheckBox[] checks;
        View[] lays;
        public MultiHolder(View view) {
            super(view);
            this.question=view.findViewById(R.id.question_text);
            for(int i=0;i<5;i++){
                int ID_text =itemView.getResources().getIdentifier("c"+i, "id",itemView.getContext().getPackageName());
                int ID_check =itemView.getResources().getIdentifier("ch"+i, "id",itemView.getContext().getPackageName());
                int ID_lay =itemView.getResources().getIdentifier("cl"+i, "id",itemView.getContext().getPackageName());

                choices[i]=itemView.findViewById(ID_text);
                checks[i]=itemView.findViewById(ID_check);
                lays[i]=itemView.findViewById(ID_lay);
            }
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if(viewType==0){
       itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_to_answer_row, parent, false);
        return new TFHolder(itemView);
    }  else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_multi_to_answer_row, parent, false);
            return new MultiHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Question quest = liste_des_questions.get(position);
        if(holder.getItemViewType()==0) {
            TFHolder hold = (TFHolder) holder;
            hold.question.setText(Question.toString(quest) + " / " + Student_Lobby.TypedAnswers.get(position));
            AnsweringManager(hold, position);
        }
        else {
            MultiHolder hold=(MultiHolder) holder;
            hold.question.setText(quest.getQuestion().get(0));
            for(int i=0;i<quest.getQuestion().size();i++){
                hold.lays[i].setVisibility(View.VISIBLE);
                if(i!=0)// skip the element 0 of ques
                hold.choices[i-1].setText(quest.getQuestion().get(i));
            }
        }
    }

    @Override
    public int getItemCount() {
        return liste_des_questions.size();
    }

    private void AnsweringManager(TFHolder hold , int position){
        final Boolean Answer,isAnswerEmpty;
        if(Student_Lobby.TypedAnswers.get(position).equals("true"))
        { Answer=true;isAnswerEmpty=false;}
        else if(Student_Lobby.TypedAnswers.get(position).equals("false"))
        {Answer=false; isAnswerEmpty=false;}
        else
        {Answer=false; isAnswerEmpty=true;}


        InitColors(hold,Answer,isAnswerEmpty);
        ListenersManager(hold,position,isAnswerEmpty,Answer);


    }
    private void InitColors(final TFHolder hold, final Boolean answer, final Boolean isAnswerEmpty){
        if(isAnswerEmpty)
            hold.question_row.setBackgroundColor(Color.WHITE);
        else if(answer)
            hold.question_row.setBackgroundColor(Color.GREEN);
        else
            hold.question_row.setBackgroundColor(Color.RED);
    }

    private void ListenersManager(final TFHolder hold , final int position, final Boolean isAnswerEmpty, final Boolean Answer){
        hold.question_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isAnswerEmpty || !Answer){
                    Student_Lobby.TypedAnswers.set(position,"true");
                }
                else{
                    Student_Lobby.TypedAnswers.set(position,"false");
                }
                notifyItemChanged(position);
            }
        });
    }
}
