package pro.pfe.first;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    public static String PREF_NAME="preferences",VALUE_NAME="valeur";
    Intent side;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        int value=prefs.getInt(VALUE_NAME,-1) ;
        if(value!=-1){ // entered the app before
            if(value==0)
            {
                side = new Intent(this,Teacher.class);
            }
            else{  // student
                side = new Intent(this,StudentActivity.class);
            }
            side.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(side);
            finish();
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
                SharedPreferences.Editor editor = getSharedPreferences(PREF_NAME, MODE_PRIVATE).edit();
                editor.putInt(VALUE_NAME, 0);
                editor.apply();
            }
        });
        Student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStudent(v);
                SharedPreferences.Editor editor = getSharedPreferences(PREF_NAME, MODE_PRIVATE).edit();
                editor.putInt(VALUE_NAME, 1);
                editor.apply();
            }
        });
    }
    public void onTeacher(View view){
        side = new Intent(this,Teacher.class);
        startActivity(side);
    }
    public void onStudent(View view){
        side = new Intent(this,StudentActivity.class);
        startActivity(side);
    }
}
