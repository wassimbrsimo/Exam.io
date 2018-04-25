
package pro.pfe.first;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import net.glxn.qrgen.android.QRCode;

public class Student extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

    }

    public void passExam(View view){
        Intent Lobby=new Intent(getApplicationContext(),Student_Lobby.class);
        startActivity(Lobby);
        //todo: Animation ..
    }

}
