package pro.pfe.first;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


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
    ImageButton close,validate,delete;
    Button add;
    public static TextView number;



    public static List<Exam> Examlist=new ArrayList<Exam>();
    public static ExamListAdapter eAdapter;
    RecyclerView rv;

    private OnFragmentInteractionListener mListener;

    public Teacher_Tab1() {
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
                addExamDialog();
            }
        });
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        addPanel=view.findViewById(R.id.add_epanel);
        close= (ImageButton) view.findViewById(R.id.add_qbtn);
        add=view.findViewById(R.id.create);
        validate= (ImageButton) view.findViewById(R.id.add_btn);
        number= view.findViewById(R.id.module);

        Examlist= Teacher.db.getNonHostedExams();
        if(Examlist.size()==0)
            number.setText("Il n ya pas d'examins");
        else if(Examlist.size()==1)
        number.setText("1 Examin");
        else
            number.setText(Examlist.size()+" Examins");

        rv = (RecyclerView) view.findViewById(R.id.recyclerview_Teacher);
        eAdapter=new ExamListAdapter(Examlist,0);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(eAdapter);
        rv.setNestedScrollingEnabled(false);
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

    public void addExamDialog(){
        LayoutInflater factory = LayoutInflater.from(this.getContext());
        final View deleteDialogView = factory.inflate(R.layout.dialog_add_exam, null);
        final AlertDialog deleteDialog = new AlertDialog.Builder(this.getContext()).create();
        deleteDialog.setView(deleteDialogView);
        deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextView titre,module;
        final NumberPicker np;
        final String[] values= {"15 mins","30 mins", "45 mins", "1 heure", "2 heures"};

        titre= deleteDialogView.findViewById(R.id.add_titre);
        module=deleteDialogView.findViewById(R.id.add_module);
        np=deleteDialogView.findViewById(R.id.numberPicker);

        titre.setText("");
        module.setText("");
        np.setMinValue(0);
        np.setValue(0);
        np.setMaxValue(values.length-1);
        np.setDisplayedValues(values);
        np.setWrapSelectorWheel(true);

        deleteDialogView.findViewById(R.id.add_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //your business logic
                Log.e("dialog","titre ("+titre.getText().toString()+")");
                String temp=titre.getText().toString();
                if(!temp.matches("")){
                AddExam(np.getValue(),titre.getText().toString(),module.getText().toString());
                deleteDialog.dismiss();
                }
                else
                    Toast.makeText(getContext(),"Veuillez saisir un titre",Toast.LENGTH_SHORT).show();
            }
        });
        deleteDialogView.findViewById(R.id.add_qbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.dismiss();
            }
        });

        deleteDialog.show();
    }

    void AddExam(int v,String titre,String module){
        int dur=0;
        switch (v){
            case 0:
                dur=15;
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

        Exam e = new Exam(titre,module,-1,dur);
        long id=Teacher.db.create(e);
        e.setId((int)id);
        Examlist.add(e);
        number.setText(Examlist.size()+" Examins");
        eAdapter.notifyDataSetChanged();
        rv.scrollToPosition(0);
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
