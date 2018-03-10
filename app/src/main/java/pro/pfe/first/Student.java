
package pro.pfe.first;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;

public class Student extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
    }

    public void StartExam(View view){
        Intent exam_activity = new Intent(this, DuringExamActivity.class);
        startActivity(exam_activity);
    }
}
