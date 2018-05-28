package pro.pfe.first;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static pro.pfe.first.Teacher.db;

public class QuestionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private List<Question> Questlist;
    public QuestionAdapter(List<Question> List){
        this.Questlist=List;
    }

    public class TFQuestionViewHolder extends RecyclerView.ViewHolder {
        public TextView question,notetxt;
        public EditText question_edit,notev;
        public Switch t;
        public ImageView save,delete;

        public TFQuestionViewHolder(View view) {
            super(view);
            question = (TextView) view.findViewById(R.id.question_text);
            t=(Switch) view.findViewById(R.id.t);
            save=(ImageView) view.findViewById(R.id.save);
            delete= (ImageView) view.findViewById(R.id.delete);
            question_edit=(EditText) view.findViewById(R.id.question_edit);
            notetxt=view.findViewById(R.id.notetxt);
            notev=view.findViewById(R.id.notev);

        }
    }
    public class MultiQuestionViewHolder extends RecyclerView.ViewHolder{
        Spinner spinner;
        TextView question,txtchoi,txtnote;
        EditText question_edit,nspinner;;
        TextView[] choices_Text= new TextView[5];
        EditText[] choices_Edit=new EditText[5];
        CheckBox[] choices_Check=new CheckBox[5];
        View[] layouts = new View[5];
        ImageView edit,delete;

        public MultiQuestionViewHolder(View itemView) {
            super(itemView);
            spinner = (Spinner) itemView.findViewById(R.id.spinner);
            nspinner=itemView.findViewById(R.id.spinner2);
            txtchoi=itemView.findViewById(R.id.txtchoi);
            txtnote=itemView.findViewById(R.id.txtnote);
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
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_row_multi, null, false);
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
                hold.question.setText(question.getQuestion().get(0));
                hold.question_edit.setText(question.getQuestion().get(0));
                hold.question_edit.setVisibility(GONE);
                hold.notetxt.setText("Note : "+question.getNote());
                hold.notev.setText(String.valueOf(question.getNote()));
                hold.notev.setVisibility(GONE);
                hold.question.setVisibility(View.VISIBLE);
            }
            if(question.getQuestion().get(0).equals(""))
            {
                hold.question_edit.setHint("veuilliez remplire la question");
                hold.notetxt.setText("Note :");
                hold.notev.setVisibility(View.VISIBLE);
                hold.question_edit.setVisibility(View.VISIBLE);
                hold.question.setVisibility(GONE);
            }
            hold.t.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (question.getAnswer() == String.valueOf(hold.t.isChecked()))
                    {}
                    else{
                        hold.save.setBackground(hold.question.getContext().getResources().getDrawable(R.drawable.save_button_shape));
                        hold.question_edit.setVisibility(View.VISIBLE);
                        hold.notetxt.setText("Note :");
                        hold.notev.setVisibility(View.VISIBLE);
                        hold.question.setVisibility(GONE);
                    }
                }
            });

            hold.save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(hold.question.getVisibility()== GONE){
                    hold.question.setVisibility(View.VISIBLE);
                    hold.question_edit.setVisibility(GONE);
                        hold.notetxt.setText("Note : "+hold.notev.getText());
                        hold.notev.setVisibility(GONE);
                    ArrayList<String> temp= new ArrayList<String>();
                    temp.add(hold.question_edit.getText().toString());
                    hold.save.setBackground(hold.question.getContext().getResources().getDrawable(R.drawable.blue_button_shape));
                    EditQuestion(new Question(temp,String.valueOf(hold.t.isChecked()),Float.valueOf(hold.notev.getText().toString()),question.getId(),question.getE_id()) );
                }
                else {
                        Edit_Exam.rv.scrollToPosition(position);
                        hold.save.setBackground(hold.question.getContext().getResources().getDrawable(R.drawable.save_button_shape));
                        hold.question.setVisibility(GONE);
                        hold.question_edit.setVisibility(View.VISIBLE);
                        hold.notetxt.setText("Note :");
                        hold.notev.setVisibility(View.VISIBLE);
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
                    MultiToggleEditMode(multiholder.choices_Edit[0].getVisibility()==View.VISIBLE,multiholder.choices_Check,multiholder.choices_Edit,multiholder.choices_Text,multiholder.spinner,multiholder.nspinner,multiholder.txtchoi,multiholder.txtnote,multiholder.edit,multiholder.question_edit.getText().toString(),multiholder.question_edit,multiholder.question,question);
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

            multiholder.nspinner.setText(String.valueOf(question.getNote()));


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

               // MultiToggleEditMode(false,multiholder.choices_Check,multiholder.choices_Edit,multiholder.choices_Text,multiholder.spinner,multiholder.nspinner,multiholder.txtchoi,multiholder.txtnote,multiholder.edit,multiholder.question_edit.getText().toString(),multiholder.question_edit,multiholder.question,question);
            multiholder.spinner.setVisibility(GONE);
            multiholder.question_edit.setVisibility(GONE);
            multiholder.txtnote.setVisibility(GONE);
            multiholder.nspinner.setVisibility(GONE);
            multiholder.txtchoi.setVisibility(GONE);
            multiholder.txtnote.setText("Note : "+multiholder.nspinner.getText());
            multiholder.txtnote.setVisibility(View.VISIBLE);

            multiholder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
                {
                    for(int i=0;i<5;i++){
                        if(multiholder.spinner.getSelectedItemPosition()+2>i)
                        multiholder.layouts[i].setVisibility(View.VISIBLE);
                        else
                            multiholder.layouts[i].setVisibility(GONE);
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }
            });
        }
    }
    public void MultiToggleEditMode(Boolean edit,CheckBox[] choices_Check,EditText[] choices_Edit,TextView[] choices_Text,Spinner sp,EditText spnote,TextView sptxt,TextView txtnote,ImageView Toggeler,String q,EditText editQ,TextView questio,Question ques){
        if(!edit){//si pour edité
        for(int i=0;i<5;i++){

            choices_Check[i].setVisibility(View.VISIBLE);
            choices_Edit[i].setVisibility(View.VISIBLE);
            sp.setVisibility(View.VISIBLE);
            sptxt.setVisibility(View.VISIBLE);
            spnote.setVisibility(View.VISIBLE);
            txtnote.setText("Note : ");
            txtnote.setVisibility(View.VISIBLE);
            questio.setVisibility(GONE);
            choices_Text[i].setVisibility(GONE);
            editQ.setVisibility(View.VISIBLE);

            Toggeler.setBackground(sp.getContext().getResources().getDrawable(R.drawable.save_button_shape));
            }
        }
        else {
            String collected_Answer="";
            for(int i=0;i<5;i++){
                choices_Check[i].setVisibility(GONE);
                choices_Edit[i].setVisibility(GONE);
                sp.setVisibility(View.VISIBLE);
                sptxt.setVisibility(GONE);
                spnote.setVisibility(GONE);
                txtnote.setText("Note : "+spnote.getText());
                choices_Text[i].setVisibility(View.VISIBLE);
                editQ.setVisibility(GONE);
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
            EditQuestion(new Question(temp,collected_Answer,Float.valueOf(spnote.getText().toString()),ques.getId(),ques.getE_id()));
            Log.e("FLOAT","TESLA FLOAT IS : "+spnote.getText());
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
        Questlist.get(index).setNote(q.getNote());

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
