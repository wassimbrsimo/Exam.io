package pro.pfe.first;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import static pro.pfe.first.Teacher.db;

public class StudentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<Student> Student_List;
    int examID;
    public StudentAdapter(ArrayList<Student> list,int id){
        this.Student_List=list;
        this.examID=id;
    }
    public class NoteStudentViewHolder extends RecyclerView.ViewHolder{
        TextView nom,note;
        public NoteStudentViewHolder(View itemView) {
            super(itemView);
            nom=itemView.findViewById(R.id.nom);
            note=itemView.findViewById(R.id.rnote);
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NoteStudentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.answer_student_row, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Student student = Student_List.get(position);
        NoteStudentViewHolder hold = (NoteStudentViewHolder) holder;
        hold.nom.setText(student.getName());
        hold.note.setText(db.getStudentAnswer(student.getID(),examID));
    }

    @Override
    public int getItemCount() {
        return Student_List.size();
    }
}
