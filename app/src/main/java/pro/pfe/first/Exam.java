package pro.pfe.first;

import android.util.Log;

import java.util.ArrayList;

import static pro.pfe.first.Student_Lobby.ANSWERS_SEPARATOR;

public class Exam {
    public String getTitre() {
        return Titre;
    }

    public String getModule() {
        return Module;
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    private String Titre;
    private String Module;
    private int id;

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    private int duration;

    public int getId() {
        return id;
    }

    ArrayList<Question> questions;

    public Exam(String titre, String module, int id, int duration) {
        this.id = id;
        this.Titre = titre;
        this.Module = module;
        this.questions = new ArrayList<Question>();
        this.duration = duration;
    }
    public String getAnswers(){
        String answers = "";
        for(int i=0;i<getQuestions().size();i++) {
            answers += getQuestions().get(i).getAnswer() + (i<getQuestions().size()-1?ANSWERS_SEPARATOR:"");
        }
        return answers;
    }

    public static String toString(Exam e) {
        return "1]" + e.getTitre() + "]" + e.getModule() + "]" + e.getId() + "]" + e.getDuration() + "]" + e.getQuestions().size() + Question.toStrings(e.getQuestions());

    }
    public static float CalculerNote(Exam examin,String answers){
        float score = 0;
        String[] TypedAnswer = answers.split(ANSWERS_SEPARATOR);
        String student_answer="";
        int div=1;
        for(int i =0;i<TypedAnswer.length && i<examin.getQuestions().size();i++) {
            student_answer += TypedAnswer[i] + (i < TypedAnswer.length - 1 ? ANSWERS_SEPARATOR : "");

            //combien de bons choix
            Log.e("Log"," student answer="+student_answer+" exam questions "+examin.getAnswers());
            if (examin.getQuestions().get(i).getType() == 0){

                if (TypedAnswer[i].equals(examin.getQuestions().get(i).getAnswer())){Log.e("Log","score = "+score+" plus "+examin.getQuestions().get(i).getNote());
                    score += examin.getQuestions().get(i).getNote();}
        }


            else {
                div = examin.getQuestions().get(i).getAnswer().length() - examin.getQuestions().get(i).getAnswer().replaceAll("1", "").length();
                for(int j=0;j<examin.getQuestions().get(i).getQuestion().size() && j<TypedAnswer[i].length();j++){

                    if(TypedAnswer[i].charAt(j)=='1' && examin.getQuestions().get(i).getAnswer().charAt(j)=='1')
                    {
                        Log.e("Log ","Typed :"+"score = "+score+" plus "+examin.getQuestions().get(i).getNote()/div);
                        score+=examin.getQuestions().get(i).getNote()/div;
                    }
                    else if(examin.getQuestions().get(i).getAnswer().charAt(j)=='1' && TypedAnswer[i].charAt(j)=='0'){
                        Log.e("Log","score = "+score+" minus "+examin.getQuestions().get(i).getNote()/div);
                        score-=examin.getQuestions().get(i).getNote()/div;
                    }
                    else if(examin.getQuestions().get(i).getAnswer().charAt(j)=='0' && TypedAnswer[i].charAt(j)=='1' ){
                        Log.e("Log","score = "+score+" minus "+examin.getQuestions().get(i).getNote()/div);
                        score-=examin.getQuestions().get(i).getNote()/div;
                    }
                }
            }
        }
        // principe c retourné les lbonnes notes recu hnaya c a dir les bonne cochage valent chaqune un point en sorte k si toutes son coché elle feront getNote()
        if(score<0)
            score=0;
        return score;
    }


    public int getNoteTotal(){
        int size=0;
        for(int i = 0; i<getQuestions().size(); i++){
            size+=getQuestions().get(i).getNote();
        }
        return  size;
    }
    public static Exam toExam(String s) {
        String[] split = s.split("]");
        Exam e = new Exam(split[1], split[2], Integer.valueOf(split[3]), Integer.valueOf(split[4]));
        e.setQuestions(Question.toQuestions(split));
        return e;
    }
}