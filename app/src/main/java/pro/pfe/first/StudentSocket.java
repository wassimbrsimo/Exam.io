package pro.pfe.first;

/**
 * Created by wassi on 5/18/2018.
 */

public class StudentSocket extends Student {
    DuringHostingActivity.Attente_Connexion sr;
    String MAC;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    int state;

    public String getMAC() {return MAC;}

    public void setMAC(String MAC) {this.MAC = MAC;}


    public DuringHostingActivity.Attente_Connexion getSr() {return sr;}
    public void setSr(DuringHostingActivity.Attente_Connexion sr) {this.sr = sr;}

    public StudentSocket(String name, String matricule, int ID,String MAC, DuringHostingActivity.Attente_Connexion sr) {
        super(name, matricule, ID);
        this.sr=sr;
        this.MAC=MAC;
        this.state=0;
    }
    public StudentSocket(Student st,String MAC, DuringHostingActivity.Attente_Connexion sr) {
        super(st.getName(), st.getMatricule(), st.getID());
        this.sr=sr;
        this.MAC=MAC;
        this.state=0;
    }
    public boolean isConnected(){
        if(sr!=null)
            return true;
        else
            return false;
    }
}
