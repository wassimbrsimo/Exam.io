package pro.pfe.first;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import static pro.pfe.first.Student_Lobby.TypedAnswers;


public class StudentExamAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<Question> liste_des_questions;
    StudentExamAdapter(List<Question> list){
        this.liste_des_questions=list;
    }

    public class TFHolder extends RecyclerView.ViewHolder{
        TextView question;
        CardView question_row;
        ImageView t,f;
        public TFHolder(View view) {
            super(view);
            this.question=view.findViewById(R.id.question_text);
            this.question_row= view.findViewById(R.id.question_row);
            this.t=view.findViewById(R.id.vrai);
            this.f=view.findViewById(R.id.faux);

        }
    }

    public class MultiHolder extends RecyclerView.ViewHolder{
        TextView question;
        TextView[] choices=new TextView[5];
        CheckBox[] checks=new CheckBox[5];
        View[] lays=new View[5];
        CardView card;
        View holderLayout;
        public MultiHolder(View view) {
            super(view);
            this.question=view.findViewById(R.id.question_text);
            for(int i=1;i<6;i++){
                int ID_text =itemView.getResources().getIdentifier("c"+i, "id",itemView.getContext().getPackageName());
                int ID_check =itemView.getResources().getIdentifier("ch"+i, "id",itemView.getContext().getPackageName());
                int ID_lay =itemView.getResources().getIdentifier("cl"+i, "id",itemView.getContext().getPackageName());

                choices[i-1]=itemView.findViewById(ID_text);
                checks[i-1]=itemView.findViewById(ID_check);
                lays[i-1]=itemView.findViewById(ID_lay);
            }
            holderLayout=itemView.findViewById(R.id.holder);
            card=itemView.findViewById(R.id.card);
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Question quest = liste_des_questions.get(position);

        if(holder.getItemViewType()==0) {
            TFHolder hold = (TFHolder) holder;
            hold.question.setText(Question.toString(quest));
            AnsweringManager(hold, position);
        }


        else {
            final MultiHolder hold=(MultiHolder) holder;
            hold.question.setText(quest.getQuestion().get(0));
            for(int i=0;i<quest.getQuestion().size()-1;i++){
                hold.lays[i].setVisibility(View.VISIBLE);
                if(i!=0)// skip the element 0 of ques
                hold.choices[i-1].setText(quest.getQuestion().get(i));
            }

            for(int i =0;i<5;i++){
                final int finalI = i;
                hold.lays[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        hold.checks[finalI].setChecked(!hold.checks[finalI].isChecked());
                        String temp="";
                        for(int j=0;j<5;j++){
                            temp+=hold.checks[j].isChecked()?"1":0;
                        }
                        TypedAnswers.remove(position);
                        TypedAnswers.add(position,temp);
                    }
                });
                hold.checks[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String temp="";
                        for(int j=0;j<5;j++){
                            temp+=hold.checks[j].isChecked()?"1":0;
                        }
                        TypedAnswers.remove(position);
                        TypedAnswers.add(position,temp);
                    }
                });
            }

            hold.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(hold.holderLayout.getVisibility()==View.VISIBLE)
                    hold.holderLayout.setVisibility(View.GONE);
                    else
                        hold.holderLayout.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return liste_des_questions.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return liste_des_questions.size();
    }

    private void AnsweringManager(TFHolder hold , int position){
        final Boolean Answer,isAnswerEmpty;
        if(TypedAnswers.get(position).equals("true"))
        { Answer=true;isAnswerEmpty=false;}
        else if(TypedAnswers.get(position).equals("false"))
        {Answer=false; isAnswerEmpty=false;}
        else
        {Answer=false; isAnswerEmpty=true;}


        InitColors(hold,Answer,isAnswerEmpty);
        ListenersManager(hold,position,isAnswerEmpty,Answer);


    }
    private void InitColors(final TFHolder hold, final Boolean answer, final Boolean isAnswerEmpty){
        if(isAnswerEmpty)
            hold.question_row.setCardBackgroundColor(Color.WHITE);
        else if(answer) {
            hold.t.setVisibility(View.VISIBLE);
            hold.f.setVisibility(View.GONE);
            hold.question_row.setCardBackgroundColor(hold.question_row.getContext().getResources().getColor(R.color.colorAccent));
        }
        else{
            hold.f.setVisibility(View.VISIBLE);
            hold.t.setVisibility(View.GONE);
            hold.question_row.setCardBackgroundColor(hold.question_row.getContext().getResources().getColor(R.color.colorNega));
        }
    }

    private void ListenersManager(final TFHolder hold , final int position, final Boolean isAnswerEmpty, final Boolean Answer){
        hold.question_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isAnswerEmpty || !Answer){
                    TypedAnswers.set(position,"true");
                }
                else{
                    TypedAnswers.set(position,"false");
                }
                notifyItemChanged(position);
            }
        });
    }
}
