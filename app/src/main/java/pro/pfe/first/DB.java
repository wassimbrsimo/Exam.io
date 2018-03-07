package pro.pfe.first;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wassi on 3/1/2018.
 */

public class DB extends SQLiteOpenHelper{


    private static final String
            ID_QUESTION_EXAM="id_qe",
            ID_QUESTION="id_q",
            ID_EXAM="id_e",
            EXAM_TITRE="Titre",
            EXAM_MODULE="Module",
            QUESTION_TYPE="Type",
            QUESTION_TEXT="Question",
            QUESTION_REPONSE="Reponse",
            TABLE_EXAMS="exams",
                    CREATE_TABLE_EXAM=
                            "CREATE TABLE "+TABLE_EXAMS+"("
                                    +ID_EXAM+" INTEGER,"
                                    +EXAM_TITRE+" TEXT,"
                                    +EXAM_MODULE+" TEXT)",

            TABLE_QUESTIONS="questions",
                    CREATE_TABLE_QUESTION=
                            "CREATE TABLE "+TABLE_QUESTIONS+"("
                                    +ID_QUESTION+" INTEGER,"
                                    +ID_QUESTION_EXAM+" INTEGER,"
                                    +QUESTION_TYPE+" INTEGER,"
                                    +QUESTION_TEXT+" TEXT,"
                                    +QUESTION_REPONSE+" TEXT)"
           ;


    public DB(Context context) {
        super(context, "ExaManager", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_EXAM);
        db.execSQL(CREATE_TABLE_QUESTION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXAMS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTIONS);
        // create new tables
        onCreate(db);
    }

    public List<Exam> getAllExams(){
        List<Exam> exams = new ArrayList<Exam>();


        String selectExamQuery = "SELECT * FROM "+TABLE_EXAMS;




        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectExamQuery, null);


        if(c.moveToFirst()){
            do {
                Exam e = new Exam(c.getString(c.getColumnIndex(EXAM_TITRE)), c.getString(c.getColumnIndex(EXAM_MODULE)), c.getInt(c.getColumnIndex(ID_EXAM)));


                if(e.getId()>Teacher.EXAM_ID_MANAGER)//////////////////////////////
                    Teacher.EXAM_ID_MANAGER=e.getId();/////////////////////////////


                String selectQuestionQuery = "SELECT * FROM "+TABLE_QUESTIONS+" WHERE "+ID_QUESTION_EXAM+" = "+c.getInt(c.getColumnIndex(ID_EXAM));
                Cursor q = db.rawQuery(selectQuestionQuery, null);
                ArrayList<Question> questions = new ArrayList<Question>();
                if (q.moveToFirst()) {
                    do {
                        Question question = new Question(q.getInt(q.getColumnIndex(QUESTION_TYPE)), q.getString(q.getColumnIndex(QUESTION_TEXT)), Boolean.valueOf(q.getString(q.getColumnIndex(QUESTION_REPONSE))), q.getInt(q.getColumnIndex(ID_QUESTION)), q.getInt(q.getColumnIndex(ID_QUESTION_EXAM)));
                        questions.add(question);

                        if(question.getId()>Teacher.QUESTION_ID_MANAGER) /////////////////////////////
                            Teacher.QUESTION_ID_MANAGER=question.getId();/////////////////////////////

                    } while (q.moveToNext());
                }

                    e.setQuestions(questions);
                    exams.add(e);
                }
                while (c.moveToNext()) ;
            }
        return exams;
    }
    public void FormatExams(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+TABLE_EXAMS);
    }


    public void create(Exam e){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ID_EXAM,e.getId());
        values.put(EXAM_TITRE,e.getTitre());
        values.put(EXAM_MODULE,e.getModule());
        db.insert(TABLE_EXAMS,null,values);
    }
    public void create(Question q){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ID_QUESTION,q.getId());
        values.put(ID_QUESTION_EXAM,q.getE_id());
        values.put(QUESTION_TYPE,q.getType());
        values.put(QUESTION_TEXT,q.getQuestion());
        values.put(QUESTION_REPONSE,q.getAnswer());
        db.insert(TABLE_QUESTIONS,null,values);
    }

    public Exam getExam(int GetThisID){
        SQLiteDatabase db=this.getReadableDatabase();
        String selectQuery = "SELECT * FROM "+TABLE_EXAMS+" WHERE id = "+GetThisID;
        Cursor c =db.rawQuery(selectQuery,null);
        if(c!=null)
            c.moveToFirst();
        Exam grabbedExam = new Exam(c.getString(c.getColumnIndex(EXAM_TITRE)),c.getString(c.getColumnIndex(EXAM_MODULE)),GetThisID);
        return grabbedExam;
    }

    public void DeleteExam(int id ){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EXAMS,ID_EXAM +"= ?",new String[] { String.valueOf(id)});
        db.delete(TABLE_QUESTIONS,ID_QUESTION_EXAM +"= ?",new String[] { String.valueOf(id)});
    }
    public void DeleteQuestion( int id_question ,int id_exam){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_QUESTIONS,ID_QUESTION_EXAM+" = ? AND "+ID_QUESTION+" = ?",new String[] {String.valueOf(id_exam), String.valueOf(id_question)});
    }
    public int updateQuestion(Question q) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(QUESTION_REPONSE,q.getAnswer());

                
        return db.update(TABLE_QUESTIONS, values, ID_QUESTION_EXAM +"= ? AND "+ID_QUESTION+" = ?",
                new String[] { String.valueOf(q.getE_id()),String.valueOf(q.getId()) });
    }
}
