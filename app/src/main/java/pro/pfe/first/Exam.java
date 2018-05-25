package pro.pfe.first;

import android.util.Log;

import java.util.ArrayList;

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
        for(Question q : getQuestions()) {
            answers += q.getAnswer() + Student_Lobby.ANSWERS_SEPARATOR;
        }
        return answers;
    }

    public static String toString(Exam e) {
        return "1]" + e.getTitre() + "]" + e.getModule() + "]" + e.getId() + "]" + e.getDuration() + "]" + e.getQuestions().size() + Question.toStrings(e.getQuestions());

    }
    public static float CalculerNote(Exam examin,String answers){
        float score = 0;
        String[] TypedAnswer = answers.split(Student_Lobby.ANSWERS_SEPARATOR);
        String student_answer="";
        for(int i =0;i<TypedAnswer.length;i++){
            student_answer+=TypedAnswer[i]+Student_Lobby.ANSWERS_SEPARATOR;

            if(examin.getQuestions().get(i).getType()==0 && TypedAnswer[i].equals(examin.getQuestions().get(i).getAnswer()))
                score++;
            else if(TypedAnswer.length>0)
                for(int j=0;j<examin.getQuestions().get(i).getQuestion().size()-1 && j<TypedAnswer[i].length();j++){
                    if(TypedAnswer[i].charAt(j)==examin.getQuestions().get(i).getAnswer().charAt(j))
                    {
                        score++;
                    }
                }
            }
        //todo: chrono design student and prof , late student timer ,buttons on/off design
        return score;
    }
    public int getQuestionsSize(){
        int size=0;
        for(int i =0;i<getAnswers().split(Student_Lobby.ANSWERS_SEPARATOR).length;i++){

            if(getQuestions().get(i).getType()==0)
                size++;
            else
                size+=getQuestions().get(i).getQuestion().size()-1;
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