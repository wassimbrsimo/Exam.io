package pro.pfe.first;


import android.renderscript.Float3;
import android.util.Log;

import java.util.ArrayList;

public class Question {
    int type;

    public float getNote() {
        return note;
    }

    public void setNote(float note) {
        this.note = note;
    }

    float note;
    static final String Separator="&&";

    public Question(ArrayList<String> quest,String answer,float note,int  id,int e_id){
        this.question=quest;
        this.answer=answer;
        this.id=id;
        this.note=note;
        this.e_id=e_id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setE_id(int e_id) {
        this.e_id = e_id;
    }

    public void setQuestion(String question) {
        this.question.add(question);
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getType() {
        return getQuestion()!=null?getQuestion().size()>1?1:0:0;
    }

    public ArrayList<String>  getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }
    int id,e_id;

    public int getE_id() {
        return e_id;
    }

    public int getId() {
        return id;
    }

    ArrayList<String> question;
    String answer;
    public static  String toString(Question q,boolean DB){
        if(q.getType()==0)
            return (!DB?q.getNote()+Separator:"")+q.getQuestion().get(0);
        else {
            String questions = !DB?q.getNote()+Separator:"";
            for (int i = 0; i < q.getQuestion().size(); i++)
                questions += q.getQuestion().get(i)+ ((i<q.getQuestion().size()-1)? Separator:"");
            return questions;
        }
    }
    public static String toStrings(ArrayList<Question> qlist){
        String questionString="]";
        for(int i=0;i<qlist.size();i++){
            questionString+=toString(qlist.get(i),false)+((i<qlist.size()-1)?"]":"");
        }
        return questionString;
    }
    public static Question toQuestion(String questionString,boolean DB){
        Question tempQuestion = new Question(new ArrayList<String>(),"",0,0,0);
        if(!DB){
        if(questionString.split(Separator).length>2){
            for(int i=1;i<questionString.split(Separator).length;i++){

                tempQuestion.setQuestion(questionString.split(Separator)[i]);
            }
            tempQuestion.setNote(Float.valueOf(questionString.split(Separator)[0]));
            return tempQuestion;
        }
        else {
            tempQuestion.setNote(Float.valueOf(questionString.split(Separator)[0]));
            tempQuestion.setQuestion(questionString.split(Separator)[1]);
            return tempQuestion;
        }
        }
        else{
            if(questionString.split(Separator).length>1){
                for(int i=0;i<questionString.split(Separator).length;i++){

                    tempQuestion.setQuestion(questionString.split(Separator)[i]);
                }
                return tempQuestion;
            }
            else {
                tempQuestion.setQuestion(questionString.split(Separator)[0]);
                return tempQuestion;
            }
        }
    }

    public static ArrayList<Question> toQuestions(String[] split){
        ArrayList<Question> questions=new ArrayList<Question>();
        for(int i =0;i<Integer.valueOf(split[5]);i++) {
            questions.add(toQuestion(split[6+i],false));
        }

        return questions;
    }

}
