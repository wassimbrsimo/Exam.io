package pro.pfe.first;


import android.app.Application;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import static pro.pfe.first.Teacher.db;

public class QuestionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private List<Question> Questlist;
    public QuestionAdapter(List<Question> List){
        this.Questlist=List;
    }

    public class TFQuestionViewHolder extends RecyclerView.ViewHolder {
        public TextView question;
        public EditText question_edit;
        public Switch t;
        public ImageView save,delete;

        public TFQuestionViewHolder(View view) {
            super(view);
            question = (TextView) view.findViewById(R.id.question_text);
            t=(Switch) view.findViewById(R.id.t);
            save=(ImageView) view.findViewById(R.id.save);
            delete= (ImageView) view.findViewById(R.id.delete);
            question_edit=(EditText) view.findViewById(R.id.question_edit);

        }
    }
    public class MultiQuestionViewHolder extends RecyclerView.ViewHolder{
        Spinner spinner;
        TextView question;
        EditText question_edit;
        TextView[] choices_Text= new TextView[5];
        EditText[] choices_Edit=new EditText[5];
        CheckBox[] choices_Check=new CheckBox[5];
        View[] layouts = new View[5];
        ImageView edit,delete;

        public MultiQuestionViewHolder(View itemView) {
            super(itemView);
            spinner = (Spinner) itemView.findViewById(R.id.spinner);
            question=itemView.findViewById(R.id.question_text);
            question_edit=itemView.findViewById(R.id.question_edit);
            edit=(ImageView)itemView.findViewById(R.id.toggleedit);
            delete=itemView.findViewById(R.id.delete);
            for(int i=1;i<6;i++){
                int ID_edit =itemView.getResources().getIdentifier("ce"+i, "id",itemView.getContext().getPackageName());
                int ID_text =itemView.getResources().getIdentifier("c"+i, "id",itemView.getContext().getPackageName());
                int ID_check =itemView.getResources().getIdentifier("checkBox"+i, "id",itemView.getContext().getPackageName());
                int ID_lay =itemView.getResources().getIdentifier("choix"+i+"layout", "id",itemView.getContext().getPackageName());

                choices_Edit[i-1]=itemView.findViewById(ID_edit);
                choices_Text[i-1]=itemView.findViewById(ID_text);
                choices_Check[i-1]=itemView.findViewById(ID_check);
                layouts[i-1]=itemView.findViewById(ID_lay);




            }

        }
    }

    @Override
    public int getItemViewType(int position) {
        return Questlist.get(position).getType();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if(viewType==0) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_row, parent, false);
            return new TFQuestionViewHolder(itemView);

        }
        else
        {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_row_multi, parent, false);
            return new MultiQuestionViewHolder(itemView);
        }
}

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final Question question = Questlist.get(position);


        if (holder.getItemViewType() == 0) {
            final TFQuestionViewHolder hold = (TFQuestionViewHolder) holder;
            if (question.getQuestion() != null) {
                if (Boolean.valueOf(question.getAnswer()))
                    hold.t.setChecked(true);
                else
                    hold.t.setChecked(false);
                hold.question.setText(Question.toString(question));
                hold.question_edit.setText(Question.toString(question));
                hold.question_edit.setVisibility(View.GONE);
                hold.question.setVisibility(View.VISIBLE);
            }
            if(question.getQuestion().get(0).equals(""))
            {
                hold.question_edit.setHint("remplire question");
                hold.question_edit.setVisibility(View.VISIBLE);
                hold.question.setVisibility(View.GONE);
            }
            hold.t.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (question.getAnswer() == String.valueOf(hold.t.isChecked()))
                    {}
                    else{
                        hold.save.setBackground(hold.question.getContext().getResources().getDrawable(R.drawable.save_button_shape));
                        hold.question_edit.setVisibility(View.VISIBLE);
                        hold.question.setVisibility(View.GONE);
                    }
                }
            });

            hold.save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(hold.question.getVisibility()==View.GONE){
                    hold.question.setVisibility(View.VISIBLE);
                    hold.question_edit.setVisibility(View.GONE);
                    ArrayList<String> temp= new ArrayList<String>();
                    temp.add(hold.question_edit.getText().toString());
                    hold.save.setBackground(hold.question.getContext().getResources().getDrawable(R.drawable.blue_button_shape));
                    EditQuestion(new Question(temp,String.valueOf(hold.t.isChecked()),question.getId(),question.getE_id()) );
                }
                else {
                        Edit_Exam.rv.scrollToPosition(position);
                        hold.save.setBackground(hold.question.getContext().getResources().getDrawable(R.drawable.save_button_shape));
                        hold.question.setVisibility(View.GONE);
                        hold.question_edit.setVisibility(View.VISIBLE);
                    }
                }
            });
            hold.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DeleteQuestion(question);
                }
            });


        }


        else {
            final MultiQuestionViewHolder multiholder = (MultiQuestionViewHolder) holder;
            multiholder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DeleteQuestion(question);
                }
            });
            multiholder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MultiToggleEditMode(multiholder.choices_Edit[0].getVisibility()==View.VISIBLE,multiholder.choices_Check,multiholder.choices_Edit,multiholder.choices_Text,multiholder.spinner,multiholder.edit,multiholder.question_edit.getText().toString(),multiholder.question_edit,multiholder.question,question);
                }
            });
            List<String> list = new ArrayList<String>();
            list.add("2");
            list.add("3");
            list.add("4");
            list.add("5");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(multiholder.spinner.getContext(),
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            multiholder.spinner.setAdapter(dataAdapter);
            multiholder.spinner.setSelection(question.getQuestion().size()-3);



            multiholder.question.setText(question.getQuestion().get(0));
            multiholder.question_edit.setText(question.getQuestion().get(0));

            for(int i=1;i<question.getQuestion().size();i++){
                   multiholder.choices_Text[i-1].setText(question.getQuestion().get(i));
                    multiholder.choices_Edit[i-1].setText(question.getQuestion().get(i));
                    multiholder.layouts[i-1].setVisibility(View.VISIBLE);

            }
            for(int i=0;i<5;i++){

                multiholder.choices_Check[i].setChecked(question.getAnswer().charAt(i) == '1');
                multiholder.question.setVisibility(View.VISIBLE);
                multiholder.choices_Text[i].setVisibility(View.VISIBLE);
                multiholder.edit.setBackground(multiholder.edit.getContext().getResources().getDrawable(R.drawable.blue_button_shape));
            }
            if(question.getQuestion().get(0).equals(""))
                MultiToggleEditMode(false,multiholder.choices_Check,multiholder.choices_Edit,multiholder.choices_Text,multiholder.spinner,multiholder.edit,multiholder.question_edit.getText().toString(),multiholder.question_edit,multiholder.question,question);


            multiholder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
                {
                    Log.e("Alors Alors "," ca a cliké : " +position+" "+id);
                    for(int i=0;i<5;i++){
                        if(multiholder.spinner.getSelectedItemPosition()+2>i)
                        multiholder.layouts[i].setVisibility(View.VISIBLE);
                        else
                            multiholder.layouts[i].setVisibility(View.GONE);
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }
            });
        }
    }
    public void MultiToggleEditMode(Boolean edit,CheckBox[] choices_Check,EditText[] choices_Edit,TextView[] choices_Text,Spinner sp,ImageView Toggeler,String q,EditText editQ,TextView questio,Question ques){
        if(!edit){//si pour edité
        for(int i=0;i<5;i++){

            Log.e("le I du debut"," i= "+i);
            choices_Check[i].setVisibility(View.VISIBLE);
            choices_Edit[i].setVisibility(View.VISIBLE);
            sp.setVisibility(View.VISIBLE);
            questio.setVisibility(View.GONE);
            choices_Text[i].setVisibility(View.GONE);
            editQ.setVisibility(View.VISIBLE);

            Toggeler.setBackground(sp.getContext().getResources().getDrawable(R.drawable.save_button_shape));
            }
        }
        else {
            String collected_Answer="";
            for(int i=0;i<5;i++){
                choices_Check[i].setVisibility(View.GONE);
                choices_Edit[i].setVisibility(View.GONE);
                sp.setVisibility(View.GONE);
                choices_Text[i].setVisibility(View.VISIBLE);
                editQ.setVisibility(View.GONE);
                questio.setVisibility(View.VISIBLE);
                Toggeler.setBackground(sp.getContext().getResources().getDrawable(R.drawable.blue_button_shape));
                if(i<sp.getSelectedItemPosition()+2)
                collected_Answer+=choices_Check[i].isChecked()?"1":"0";
                else
                    collected_Answer+="0";
            }
            Log.e("Answer collected ","answer : "+collected_Answer);
            ArrayList<String> temp = new ArrayList<String >();
            temp.add(q);
            for(int i=0;i<5;i++)
                if(!choices_Edit[i].getText().toString().equals("")&& i<sp.getSelectedItemPosition()+2) {
                    temp.add(choices_Edit[i].getText().toString());
                    Log.e("added question ","q : "+choices_Edit[i].getText().toString());
            }
            EditQuestion(new Question(temp,collected_Answer,ques.getId(),ques.getE_id()));
            }
                // enable chekboxs & edit text , disable textview set

        //si pour save
                    // save db & object than notify

    }
    void VFToggleEditMode(){

    }
    public void EditQuestion(Question q){
        int index=0;
        for(int i=0;i<Questlist.size();i++) {
            if (Questlist.get(i).getId() == q.getId())
                index = i;
        }
        Questlist.get(index).question=q.getQuestion();

        Questlist.get(index).setAnswer(q.getAnswer());
        db.updateQuestion(q);
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
