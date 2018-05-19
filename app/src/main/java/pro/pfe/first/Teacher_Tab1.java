package pro.pfe.first;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static pro.pfe.first.Teacher.db;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Teacher_Tab1.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Teacher_Tab1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Teacher_Tab1 extends Fragment {

    View addPanel,addqPanel ;
    ImageButton add,close,validate,delete;
    TextView titre,module,question;
    RadioButton t,f;
    NumberPicker np;



    public static List<Exam> Examlist=new ArrayList<Exam>();
    public static ExamListAdapter eAdapter;
    RecyclerView rv;

    private OnFragmentInteractionListener mListener;

    public Teacher_Tab1() {
        // Required empty public constructor
    }

    public static Teacher_Tab1 newInstance(String param1, String param2) {
        Teacher_Tab1 fragment = new Teacher_Tab1();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    void Listeners(){
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddExamToggler();
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddExamToggler();
            }
        });
        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddExam();
            }
        });

    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        final String[] values= {"15 mins","30 mins", "45 mins", "1 heure", "2 heures"};

        addPanel=view.findViewById(R.id.add_epanel);
        close= (ImageButton) view.findViewById(R.id.add_qbtn);
        add= (ImageButton) view.findViewById(R.id.create);
        validate= (ImageButton) view.findViewById(R.id.add_btn);
        titre= view.findViewById(R.id.add_titre);
        module=view.findViewById(R.id.add_module);


        //question=view.findViewById(R.id.add_question);
        rv = (RecyclerView) view.findViewById(R.id.recyclerview_Teacher);
        np=view.findViewById(R.id.numberPicker);
        np.setMinValue(0);
        np.setValue(0);
        np.setMaxValue(values.length-1);
        np.setDisplayedValues(values);
        np.setWrapSelectorWheel(true);

        Examlist= Teacher.db.getAllExams();
        Log.e("EXAMILIST","DONE WE HAVE "+Examlist.size());
        eAdapter=new ExamListAdapter(Examlist,false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(eAdapter);
        eAdapter.notifyDataSetChanged();
        Listeners();

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

     return inflater.inflate(R.layout.fragment_teacher__tab1, container, false);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public void AddExamToggler(){
        if(addPanel.getVisibility()==View.GONE) {
            addPanel.setVisibility(View.VISIBLE);
            add.setVisibility(View.GONE);
        }
        else {
            addPanel.setVisibility(View.GONE);
            titre.setText("");module.setText("");
            add.setVisibility(View.VISIBLE);
        }
    }

    void AddExam(){
        int dur=0;
        switch (np.getValue()){
            case 0:
                dur=1;
                break;
            case 1:
                dur=30;
                break;
            case 2:
                dur=45;
                break;
            case 3:
                dur=60;
                break;
            case 4:
                dur=120;
                break;
        }

        Exam e = new Exam(titre.getText().toString(),module.getText().toString(),-1,dur);
        long id=Teacher.db.create(e);
        e.setId((int)id);
        Examlist.add(e);
        eAdapter.notifyItemInserted(Examlist.size());
        rv.scrollToPosition(0);
        AddExamToggler();
    }


    public void FormatExams(View view){
        Teacher.db.FormatExams();
        eAdapter.notifyDataSetChanged();
    }

    public int grabExamNextAvailableID(){
        int ID = Examlist.size();
        for(int j =0;j<Examlist.size();j++)
            for(int i=0;i<Examlist.size();i++){
                if(j==Examlist.get(i).getId())
                {
                    break;
                }
                ID=j;
            }
        return ID;
    }

    public static int getExamIndexByID(int ID) {
        int index=-1;
        for (int i = 0; i < Examlist.size(); i++)
            if (Examlist.get(i).getId() == ID){
                index=i;
                break;
            }
        return index;
    }
    public static int getQuestionIndexByID(int ID,int ExamIndex){
        int index=-1;
        for (int j = 0; j < Examlist.get(ExamIndex).getQuestions().size(); j++){
            if (Examlist.get(ExamIndex).getQuestions().get(j).getId() ==ID){
                index=j;
                break;
            }}
        return index;
    }

}
