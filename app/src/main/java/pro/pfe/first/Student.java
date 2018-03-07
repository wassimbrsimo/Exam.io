
package pro.pfe.first;

import android.support.v4.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Student extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        loadFragment((Fragment)new ExamDisplayer());
    }
    private void loadFragment(Fragment fragment) {
// create a FragmentManager
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
// create a FragmentTransaction to begin the transaction and replace the Fragment
        android.support.v4.app.FragmentTransaction fragmentTransaction = fm.beginTransaction();
// replace the FrameLayout with new Fragment
        fragmentTransaction.replace(R.id.fragment, fragment);
        fragmentTransaction.commit(); // save the changes
    }
}
