package pro.pfe.first;


import android.util.Log;

import java.util.ArrayList;

public class Question {
    int type;
    static final String Separator="&&";

    public Question(ArrayList<String> quest,String answer,int  id,int e_id){
        this.question=quest;
        this.answer=answer;
        this.id=id;
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
    public static  String toString(Question q){
        if(q.getType()==0)
            return q.getQuestion().get(0);
        else {
            String questions = "";
            for (int i = 0; i < q.getQuestion().size(); i++)
                questions += q.getQuestion().get(i) + Separator;
            return questions;
        }
    }
    public static String toStrings(ArrayList<Question> qlist){
        String questionString="]";
        for(int i=0;i<qlist.size();i++){
            questionString+=toString(qlist.get(i))+"]";
        }
        return questionString;
    }
    public static ArrayList<String> toQuestionArray(String questionString){
        ArrayList<String> temp=new ArrayList<String>();
        if(questionString.split(Separator).length>1){
            for(String s : questionString.split(Separator))
                temp.add(s);
        return temp;
        }
        else{
            temp.add(questionString.split(Separator)[0]);
            return temp;
        }
    }
    public static ArrayList<Question> toQuestions(String[] split){
        ArrayList<Question> questions=new ArrayList<Question>();
        for(int i =0;i<Integer.valueOf(split[5]);i++) {
            questions.add(new Question(toQuestionArray(split[6 + i]),"",0,0));
        }

        return questions;
    }

}
