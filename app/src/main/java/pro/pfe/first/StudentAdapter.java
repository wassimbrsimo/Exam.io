package pro.pfe.first;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import static pro.pfe.first.Teacher.db;

public class StudentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<Student> Student_List;
    Teacher_Done_Exam mActivity;
    int examID;
    public StudentAdapter(ArrayList<Student> list,int id,Teacher_Done_Exam mActivity){
        this.Student_List=list;
        this.examID=id;
        this.mActivity=mActivity;
    }
    public class NoteStudentViewHolder extends RecyclerView.ViewHolder{
        TextView nom;
        View v;
        public NoteStudentViewHolder(View itemView) {
            super(itemView);
            nom=itemView.findViewById(R.id.nom);
            v=itemView.findViewById(R.id.v);
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NoteStudentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.answer_student_row, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Student student = Student_List.get(position);
        NoteStudentViewHolder hold = (NoteStudentViewHolder) holder;
        hold.nom.setText(student.getName());
        hold.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("clicked","clicked");
                mActivity.setupRv(mActivity.examin.getQuestions(),db.getStudentAnswer(student.getID(),examID),student.getName());
        }});
    }
    @Override
    public int getItemCount() {
        return Student_List.size();
    }
}
