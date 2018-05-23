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

    public static Exam toExam(String s) {
        String[] split = s.split("]");
        Exam e = new Exam(split[1], split[2], Integer.valueOf(split[3]), Integer.valueOf(split[4]));
        e.setQuestions(Question.toQuestions(split));
        return e;
    }
}