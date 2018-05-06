package pro.pfe.first;

/**
 * Created by wassi on 5/5/2018.
 */

public class Student {

    String name,matricule;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    int ID;
    public Student(String name,String matricule,int ID){
        this.name=name;
        this.matricule=matricule;
        this.ID=ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMatricule() {
        return matricule;
    }

    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }

}
