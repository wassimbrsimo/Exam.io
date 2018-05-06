
package pro.pfe.first;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.wifi.WifiManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import net.glxn.qrgen.android.QRCode;

public class StudentActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wm.setWifiEnabled(false);

    }

    public void passExam(View view){
        Intent Lobby=new Intent(getApplicationContext(),Student_Lobby.class);
        startActivity(Lobby);
        //todo: Animation ..
    }

}
