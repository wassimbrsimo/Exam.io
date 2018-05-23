package pro.pfe.first;

/**
 * Created by wassi on 5/18/2018.
 */

public class StudentSocket extends Student {
    DuringHostingActivity.SocketConnexion sr;
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


    public DuringHostingActivity.SocketConnexion getSr() {return sr;}
    public void setSr(DuringHostingActivity.SocketConnexion sr) {this.sr = sr;}

    public StudentSocket(String name, String matricule, int ID,String MAC, DuringHostingActivity.SocketConnexion sr) {
        super(name, matricule, ID);
        this.sr=sr;
        this.MAC=MAC;
        this.state=0;
    }
    public StudentSocket(Student st,String MAC, DuringHostingActivity.SocketConnexion sr) {
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
