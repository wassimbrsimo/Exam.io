package pro.pfe.first;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.List;
import static pro.pfe.first.Teacher.db;
import static pro.pfe.first.Teacher.getExamIndexByID;
import static pro.pfe.first.Teacher.getQuestionIndexByID;

public class QuestionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private List<Question> Questlist;

    public class QuestionViewHolder extends RecyclerView.ViewHolder {
        public TextView question;
        public RadioButton t,f;
        public Button save,delete;

        public QuestionViewHolder(View view) {
            super(view);
            question = (TextView) view.findViewById(R.id.question_text);
            t=(RadioButton) view.findViewById(R.id.t);
            f=(RadioButton) view.findViewById(R.id.f);
            save=(Button) view.findViewById(R.id.save);
            delete=(Button) view.findViewById(R.id.delete);

        }
    }

    public QuestionAdapter(List<Question> List){
        this.Questlist=List;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_row, parent, false);
        return new QuestionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Question question = Questlist.get(position);
        final QuestionViewHolder hold =(QuestionViewHolder)holder;
        final int pos=position;
        hold.question.setText(question.getQuestion());
        hold.delete.setId(question.getId());

            hold.t.setChecked(!question.getAnswer());
            hold.f.setChecked(question.getAnswer());

        hold.save.setEnabled(false);

        hold.t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(question.getAnswer())
                    hold.save.setEnabled(false);
                else
                    hold.save.setEnabled(true);
            }
        });
        hold.f.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(question.getAnswer())
                    hold.save.setEnabled(true);
                else
                    hold.save.setEnabled(false);
            }
        });
        hold.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditQuestion(question,hold.t.isChecked());
            }
        });
        hold.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteQuestion(question);
            }
        });
    }



    public void EditQuestion(Question q,Boolean answer){
        int index=0;
        for(int i=0;i<Questlist.size();i++) {
            Log.e("DEBUG","- "+i+" Queslist dude "+Questlist.get(i).getId());
            if (Questlist.get(i).getId() == q.getId())
                index = i;
        }
        Log.e("DEBUG","- "+index+" index & we foung the answer "+Questlist.get(index).getAnswer());

        Questlist.get(index).setAnswer(answer);
        db.updateQuestion(Questlist.get(index));
        notifyItemChanged(index);
    }
    public void DeleteQuestion(Question q){
        db.DeleteQuestion(q.getId(),q.getE_id());
        int index=0;
        for(int i=0;i<Questlist.size();i++) {
            if (Questlist.get(i).getId() == q.getId())
                index = i;
        }
        Questlist.remove(index);
        notifyItemRemoved(index);
    }
    @Override
    public int getItemCount() {
        return Questlist.size();
    }


}