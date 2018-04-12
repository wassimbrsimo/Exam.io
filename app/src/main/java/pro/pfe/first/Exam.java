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
    public Exam(String titre,String module,int id,int duration){
        this.id=id;
        this.Titre=titre;
        this.Module=module;
        this.questions= new ArrayList<Question>();
        this.duration=duration;
    }

}
