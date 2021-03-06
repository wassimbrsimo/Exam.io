package pro.pfe.first;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import static pro.pfe.first.Teacher.db;
import static pro.pfe.first.Teacher_Tab1.eAdapter;


public class Teacher_Tab2 extends Fragment {

    RecyclerView rv;
    ArrayList<Exam> Examlist = new ArrayList<>();

    private OnFragmentInteractionListener mListener;

    public Teacher_Tab2() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(db!=null)
        Examlist = db.getHostedExams();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_teacher__tab2, container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        rv = (RecyclerView) view.findViewById(R.id.hosted_list);
        TextView titre = view.findViewById(R.id.rtitre);
        eAdapter=new ExamListAdapter(Examlist,2);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(eAdapter);
        rv.setNestedScrollingEnabled(false);
        eAdapter.notifyDataSetChanged();
        titre.setText("Examin passé :"+Examlist.size());
    }
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public void onBack(View v){
        Teacher.vp.setCurrentItem(1,true);
    }
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
