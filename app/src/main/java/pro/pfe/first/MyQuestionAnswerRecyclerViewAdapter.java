package pro.pfe.first;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import static pro.pfe.first.Student_Lobby.ANSWERS_SEPARATOR;

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
        if(viewType==1) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_multiquestionanswer, parent, false);
            return new MultiQuestAnswerHolder(view);
        }
        else{

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_questionanswer, parent, false);
            return  new TFQuestAnswerHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Question mIt=mValues.get(position);
        if(holder.getItemViewType()==0){

            TFQuestAnswerHolder hold=(TFQuestAnswerHolder)holder;
            String rep="";
            if(Answers.split(ANSWERS_SEPARATOR).length>position)
            rep=Answers.split(ANSWERS_SEPARATOR)[position];
            hold.question.setText(mIt.getQuestion().get(0));
            if(rep.equals("true"))
                hold.t.setVisibility(View.VISIBLE);
            else
                hold.f.setVisibility(View.VISIBLE);

            if(rep.equals(mIt.getAnswer())){
                hold.mView.setBackgroundColor(hold.mView.getContext().getResources().getColor(R.color.colorAccent));
                hold.pts.setText("+"+mIt.getNote()+"pts");
                hold.pts.setTextColor(hold.mView.getContext().getResources().getColor(R.color.colorAccent));
            }else{
                hold.mView.setBackgroundColor(hold.mView.getContext().getResources().getColor(R.color.colorNega));
                hold.pts.setText("0 /"+mIt.getNote()+"pts");
                hold.pts.setTextColor(hold.mView.getContext().getResources().getColor(R.color.colorNega));
            }
        }


        else if(holder.getItemViewType()==1){
            MultiQuestAnswerHolder hold=(MultiQuestAnswerHolder)holder;
            String rep="";
            if(Answers.split(ANSWERS_SEPARATOR).length>position)
            rep=Answers.split(ANSWERS_SEPARATOR)[position];
            hold.question.setText(mIt.getQuestion().get(0));
            int  div = mIt.getAnswer().length() -mIt.getAnswer().replaceAll("1", "").length();
            for(int i=1;i<mIt.getQuestion().size();i++)
            {
                hold.lays[i-1].setVisibility(View.VISIBLE);
                hold.choices[i-1].setText(mIt.getQuestion().get(i));
                if(rep.charAt(i-1)=='1' ||mIt.getAnswer().charAt(i-1)=='1') {
                    if (rep.length() >= i && rep.charAt(i - 1) == mIt.getAnswer().charAt(i - 1)) {
                        hold.checks[i - 1].setChecked(rep.charAt(i - 1) == '1');
                        Log.e("COLOR SETUP", " PUTING GREEN color " + mIt.getQuestion().size());
                        hold.lays[i - 1].setBackgroundColor(hold.mView.getContext().getResources().getColor(R.color.colorAccent));
                        hold.ptsList[i-1].setText("+" + mIt.getNote()/div + "pts");
                        hold.ptsList[i-1].setTextColor(hold.mView.getContext().getResources().getColor(R.color.colorAccent));
                    } else {
                        hold.checks[i - 1].setChecked(rep.charAt(i - 1) == '1');
                        Log.e("COLOR SETUP", " PUTING RED color" + mIt.getQuestion().size());
                        hold.lays[i - 1].setBackgroundColor(hold.mView.getContext().getResources().getColor(R.color.colorNega));
                        hold.ptsList[i-1].setText("-" + mIt.getNote()/div + "pts");
                        hold.ptsList[i-1].setTextColor(hold.mView.getContext().getResources().getColor(R.color.colorNega));
                    }
                }
            }
            hold.pts.setText("total note ");
        }
    }



    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class TFQuestAnswerHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView question,pts;
        ImageView t,f;

        public TFQuestAnswerHolder(View view) {
            super(view);
            pts=view.findViewById(R.id.pts);
            question=view.findViewById(R.id.question_text);
            mView = view.findViewById(R.id.card);
            t=view.findViewById(R.id.vrai);
            f=view.findViewById(R.id.faux);
        }
    }
    public class MultiQuestAnswerHolder extends RecyclerView.ViewHolder {
        public final View mView;
        TextView question,pts;
        TextView[] choices=new TextView[5];
        CheckBox[] checks=new CheckBox[5];
        View[] lays=new View[5];
        TextView[] ptsList=new TextView[5];

        public MultiQuestAnswerHolder(View view) {
            super(view);
            mView = view;
            question = (TextView) view.findViewById(R.id.question_text);
            pts=view.findViewById(R.id.ptstotal);
            for(int i=1;i<6;i++){
                int ID_text =itemView.getResources().getIdentifier("c"+i, "id",itemView.getContext().getPackageName());
                int ID_check =itemView.getResources().getIdentifier("checkBox"+i, "id",itemView.getContext().getPackageName());
                int ID_lay =itemView.getResources().getIdentifier("choix"+i, "id",itemView.getContext().getPackageName());
                int ID_pts=itemView.getResources().getIdentifier("pts"+i,"id",itemView.getContext().getPackageName());

                choices[i-1]=itemView.findViewById(ID_text);
                checks[i-1]=itemView.findViewById(ID_check);
                lays[i-1]=itemView.findViewById(ID_lay);
                ptsList[i-1]=itemView.findViewById(ID_pts);
            }

        }
    }
}
