package pro.pfe.first;

import java.net.Socket;

/**
 * Created by wassi on 5/18/2018.
 */

public class StudentSocket extends Student {
    DuringHostingActivity.SendRecieve sr;
    String MAC;

    public String getMAC() {return MAC;}

    public void setMAC(String MAC) {this.MAC = MAC;}


    public DuringHostingActivity.SendRecieve getSr() {return sr;}
    public void setSr(DuringHostingActivity.SendRecieve sr) {this.sr = sr;}

    public StudentSocket(String name, String matricule, int ID,String MAC, DuringHostingActivity.SendRecieve sr) {
        super(name, matricule, ID);
        this.sr=sr;
        this.MAC=MAC;
    }
    public StudentSocket(Student st,String MAC, DuringHostingActivity.SendRecieve sr) {
        super(st.getName(), st.getMatricule(), st.getID());
        this.sr=sr;
        this.MAC=MAC;
    }
    public boolean isConnected(){
        if(sr!=null)
            return true;
        else
            return false;
    }
}
