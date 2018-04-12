
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
        Bitmap myBitmap = QRCode.from("Wassim").withSize(1000, 1000).bitmap();
        ImageView myImage = (ImageView) findViewById(R.id.imageView);
        myImage.setImageBitmap(myBitmap);
    }

    public void StartExam(View view){
        Intent exam_activity = new Intent(this, DuringExamActivity.class);
        startActivity(exam_activity);
    }
}
