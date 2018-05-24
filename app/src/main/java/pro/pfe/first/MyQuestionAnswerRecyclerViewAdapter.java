package pro.pfe.first;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MyQuestionAnswerRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Question> mValues;
    private String Answers;

    public MyQuestionAnswerRecyclerViewAdapter(List<Question> items,String Answers) {
        mValues = items;
        this.Answers=Answers;
    }

    @Override
    public int getItemViewType(int position) {
        return mValues.get(position).getType();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_questionanswer, parent, false);
        if(viewType==1)
        return new MultiQuestAnswerHolder(view);
        else
            return  new TFQuestAnswerHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Question mIt=mValues.get(position);
        if(holder.getItemViewType()==0){

            TFQuestAnswerHolder hold=(TFQuestAnswerHolder)holder;
            String rep="";
            if(Answers.split(Student_Lobby.ANSWERS_SEPARATOR).length>position)
            rep=Answers.split(Student_Lobby.ANSWERS_SEPARATOR)[position];
            hold.question.setText(mIt.getQuestion().get(0));
            hold.mIdView.setText(rep);
            hold.mContentView.setText(mIt.getAnswer());


            if(rep.equals(mIt.getAnswer())){
                hold.mView.setBackgroundColor(Color.GREEN);
            }else
                hold.mView.setBackgroundColor(Color.RED);
        }


        else{
            MultiQuestAnswerHolder hold=(MultiQuestAnswerHolder)holder;
            String rep=Answers.split(Student_Lobby.ANSWERS_SEPARATOR)[position];
            hold.mIdView.setText(rep);
            hold.mContentView.setText(mIt.getAnswer());
        }
    }



    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class TFQuestAnswerHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView,mIdView,question;


        public TFQuestAnswerHolder(View view) {
            super(view);
            question=view.findViewById(R.id.question_text);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }
    }
    public class MultiQuestAnswerHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;

        public MultiQuestAnswerHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }
    }
}
