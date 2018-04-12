package pro.pfe.first;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {


    //TODO : completer les models de questions ( les autres types )
    //TODO   : DB Students avec les notes (DONE)
    //TODO   : WIFI tests
    //TODO :    Landscape mode fix (DONE)
    Intent side;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(false){ // entered the app before
            if(true)        // teacher
            {
                side = new Intent(this,Teacher.class);
            }
            else{  // student
                side = new Intent(this,Student.class);
            }
            startActivity(side);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button Teacher=(Button)findViewById(R.id.Teacher);
        Button Student=(Button)findViewById(R.id.Student);

        Teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            onTeacher(v);
            }
        });
        Student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStudent(v);
            }
        });
    }
    public void onTeacher(View view){
        side = new Intent(this,Teacher.class);
        startActivity(side);
    }
    public void onStudent(View view){
        side = new Intent(this,Student.class);
        startActivity(side);
    }
}
