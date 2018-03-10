package pro.pfe.first;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class ExamAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<Question> liste_des_questions;

    public class ExamHolder extends RecyclerView.ViewHolder{
        TextView question;
        View question_row;
        public ExamHolder(View view) {
            super(view);
            this.question=view.findViewById(R.id.question_text);
            this.question_row= view.findViewById(R.id.question_row);

        }
    }
    ExamAdapter(List<Question> list){
        this.liste_des_questions=list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_to_answer_row, parent, false);
        return new ExamHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Question quest=liste_des_questions.get(position);
        ExamHolder hold = (ExamHolder) holder;
        hold.question.setText(quest.getQuestion()+" / "+DuringExamActivity.TypedAnswers.get(position));

        AnsweringManager(hold,position);
    }

    @Override
    public int getItemCount() {
        return liste_des_questions.size();
    }

    private void AnsweringManager(ExamHolder hold ,int position){
        final Boolean Answer,isAnswerEmpty;
        if(DuringExamActivity.TypedAnswers.get(position).equals("true"))
        { Answer=true;isAnswerEmpty=false;}
        else if(DuringExamActivity.TypedAnswers.get(position).equals("false"))
        {Answer=false; isAnswerEmpty=false;}
        else
        {Answer=false; isAnswerEmpty=true;}


        InitColors(hold,Answer,isAnswerEmpty);
        ListenersManager(hold,position,isAnswerEmpty,Answer);


    }
    private void InitColors(final ExamHolder hold,final Boolean answer,final Boolean isAnswerEmpty){
        if(isAnswerEmpty)
            hold.question_row.setBackgroundColor(Color.WHITE);
        else if(answer)
            hold.question_row.setBackgroundColor(Color.GREEN);
        else
            hold.question_row.setBackgroundColor(Color.RED);
    }

    private void ListenersManager(final ExamHolder hold ,final int position,final Boolean isAnswerEmpty,final Boolean Answer){
        hold.question_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isAnswerEmpty || !Answer){
                    DuringExamActivity.TypedAnswers.set(position,"true");
                }
                else{
                    DuringExamActivity.TypedAnswers.set(position,"false");
                }
                notifyItemChanged(position);
            }
        });
    }
}
