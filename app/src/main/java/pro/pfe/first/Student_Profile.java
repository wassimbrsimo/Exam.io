package pro.pfe.first;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;



public class Student_Profile extends AppCompatActivity {
    EditText matricule,name;
    Button save;
    DB db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student__profile);
        save = findViewById(R.id.save);
        name=findViewById(R.id.edit_name);
        matricule=findViewById(R.id.edit_matricule);
        db =new DB(getApplicationContext());
        if(!db.isStudentExists(false,"")){
            save.setText("Sauvegarder");
        }else {
            save.setText("Modifier");
        }

        }
 public void saveStudent(View v){
         db.insertStudent(name.getText().toString(),matricule.getText().toString());
     Intent StudentMainActivity = new Intent(this,StudentActivity.class);
     startActivity(StudentMainActivity);

 }
}

