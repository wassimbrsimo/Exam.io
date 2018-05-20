package pro.pfe.first;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DB extends SQLiteOpenHelper{


    private static final String
            ID_QUESTION_EXAM="id_qe",
            ID_QUESTION="id_q",
            ID_EXAM="id_e",
            EXAM_TITRE="Titre",
            EXAM_MODULE="Module",
            EXAM_DURATION="Duration",
            QUESTION_TYPE="Type",
            QUESTION_TEXT="Question",
            QUESTION_REPONSE="Reponse",
            ID_ANSWER_EXAM="id_ae",
            ID_ANSWER_STUDENT="id_as",
            ID_STUDENT="id_s"     ,
            ANSWER_STRING="Answer",
            STUDENT_NAME="Name",
            STUDENT_MATRICULE="Matricule",

    TABLE_ANSWER="answers",
            CREATE_TABLE_ANSWER=
                    "CREATE TABLE "+TABLE_ANSWER+"("
                            +ID_ANSWER_EXAM+" INTEGER,"
                            +ID_ANSWER_STUDENT+" INTEGER,"
                            +ANSWER_STRING+" TEXT)",

            TABLE_STUDENT="Etudiants",
                    CREATE_TABLE_STUDENT=
                        "CREATE TABLE "+TABLE_STUDENT+"("
                                +ID_STUDENT+" Integer PRIMARY KEY AUTOINCREMENT,"
                                +STUDENT_NAME +" TEXT,"
                                +STUDENT_MATRICULE+" INTEGER)",

            TABLE_EXAMS="exams",
                    CREATE_TABLE_EXAM=
                            "CREATE TABLE "+TABLE_EXAMS+"("
                                    +ID_EXAM+" Integer PRIMARY KEY AUTOINCREMENT,"
                                    +EXAM_TITRE+" TEXT,"
                                    +EXAM_MODULE+" TEXT,"
                                    +EXAM_DURATION+" INTEGER)",

            TABLE_QUESTIONS="questions",
                    CREATE_TABLE_QUESTION=
                            "CREATE TABLE "+TABLE_QUESTIONS+"("
                                    +ID_QUESTION+" Integer PRIMARY KEY AUTOINCREMENT,"
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
        db.execSQL(CREATE_TABLE_ANSWER);
        db.execSQL(CREATE_TABLE_STUDENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXAMS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTIONS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ANSWER);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENT);
            onCreate(db);
    }
    public String getStudentAnswer(int student_Id ,int exam_id){
        String answer="";
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor c= db.rawQuery("SELECT * FROM "+TABLE_ANSWER+" WHERE "+ID_ANSWER_STUDENT+" = "+student_Id + " AND "+ID_ANSWER_EXAM+" = "+exam_id,null);
        if(c.moveToFirst()) {
            do {
               answer = c.getString(c.getColumnIndex(ANSWER_STRING));

            }while(c.moveToNext());
        }
        return answer;
    }
    public Student getStudent(int id){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor c= db.rawQuery("SELECT * FROM "+TABLE_STUDENT+" WHERE "+ID_STUDENT+" = "+id,null);
        Student grabbedStudent=null;
        if(c.moveToFirst()) {
            do {
                grabbedStudent = new Student(c.getString(c.getColumnIndex(STUDENT_NAME)), c.getString(c.getColumnIndex(STUDENT_MATRICULE)), c.getInt(c.getColumnIndex(ID_STUDENT)));

        }while(c.moveToNext());
    }
    return grabbedStudent;
    }
    public ArrayList<Student> getStudentWithExam(int id){
        SQLiteDatabase db=this.getReadableDatabase();
        String selectQuery = "SELECT * FROM "+TABLE_ANSWER+" WHERE "+ID_ANSWER_EXAM+" = "+id;
        Cursor c =db.rawQuery(selectQuery,null);
        ArrayList<Student> students=new ArrayList<>();
        if(c.moveToFirst())
            do {
                students.add(getStudent(c.getInt(c.getColumnIndex(ID_ANSWER_STUDENT))));
            }while(c.moveToNext());


        return students;
    }
    public ArrayList<Exam> getHostedExams(){
        ArrayList<Exam> exams = new ArrayList<Exam>();
        String selectExamQuery = "SELECT * FROM "+TABLE_ANSWER+ " GROUP BY "+ID_ANSWER_EXAM;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectExamQuery, null);


        if(c.moveToFirst()){
            do {
                exams.add(getExam(c.getInt(c.getColumnIndex(ID_ANSWER_EXAM))));
            }
            while (c.moveToNext()) ;
        }
        if(exams.size()!=0)
        Log.e("get HOSTED ","got a hosted : "+exams.get(0).getTitre());
        return exams;
    }

    public Boolean isExamHosted(int ID){

        SQLiteDatabase db=this.getReadableDatabase();
        String selectQuery = "SELECT * FROM "+TABLE_ANSWER+" WHERE "+ID_ANSWER_EXAM+" = "+ID;
        Cursor c =db.rawQuery(selectQuery,null);

        if(c.getCount()>0)
            return true;
        else
             return false;
    }
    public List<Exam> getAllExams(){
        List<Exam> exams = new ArrayList<Exam>();
        String selectExamQuery = "SELECT * FROM "+TABLE_EXAMS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectExamQuery, null);


        if(c.moveToFirst()){
            do {
                Exam e = new Exam(c.getString(c.getColumnIndex(EXAM_TITRE)), c.getString(c.getColumnIndex(EXAM_MODULE)), c.getInt(c.getColumnIndex(ID_EXAM)),c.getInt(c.getColumnIndex(EXAM_DURATION)));

                String selectQuestionQuery = "SELECT * FROM "+TABLE_QUESTIONS+" WHERE "+ID_QUESTION_EXAM+" = "+c.getInt(c.getColumnIndex(ID_EXAM));
                Cursor q = db.rawQuery(selectQuestionQuery, null);
                ArrayList<Question> questions = new ArrayList<Question>();
                if (q.moveToFirst()) {
                    do {

                        Question question = new Question(Question.toQuestionArray(q.getString(q.getColumnIndex(QUESTION_TEXT))),  q.getString(q.getColumnIndex(QUESTION_REPONSE)), q.getInt(q.getColumnIndex(ID_QUESTION)), q.getInt(q.getColumnIndex(ID_QUESTION_EXAM)));
                        questions.add(question);

                    } while (q.moveToNext());
                }

                    e.setQuestions(questions);
                    exams.add(e);
                }
                while (c.moveToNext()) ;
            }
        return exams;
    }
    public Exam getExam(int GetThisID){
        SQLiteDatabase db=this.getReadableDatabase();
        String selectQuery = "SELECT * FROM "+TABLE_EXAMS+" WHERE "+ID_EXAM+" = "+GetThisID;
        Cursor c =db.rawQuery(selectQuery,null);
        if(c!=null)
            c.moveToFirst();
        Exam grabbedExam = new Exam(c.getString(c.getColumnIndex(EXAM_TITRE)),c.getString(c.getColumnIndex(EXAM_MODULE)),GetThisID,c.getInt(c.getColumnIndex(EXAM_DURATION)));
        String selectQuestionQuery = "SELECT * FROM "+TABLE_QUESTIONS+" WHERE "+ID_QUESTION_EXAM+" = "+grabbedExam.getId();
        Cursor q = db.rawQuery(selectQuestionQuery, null);
        ArrayList<Question> questions = new ArrayList<Question>();
        if (q.moveToFirst()) {
            do {
                Question question = new Question(Question.toQuestionArray(q.getString(q.getColumnIndex(QUESTION_TEXT))),  q.getString(q.getColumnIndex(QUESTION_REPONSE)), q.getInt(q.getColumnIndex(ID_QUESTION)), q.getInt(q.getColumnIndex(ID_QUESTION_EXAM)));
                questions.add(question);

            } while (q.moveToNext());
        }

        grabbedExam.setQuestions(questions);

        return grabbedExam;
    }

    public void FormatExams(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+TABLE_EXAMS);
    }


    public long create(Exam e){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EXAM_TITRE,e.getTitre());
        values.put(EXAM_MODULE,e.getModule());
        values.put(EXAM_DURATION,e.getDuration());
        return db.insert(TABLE_EXAMS,null,values);
    }
    public long create(Question q){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ID_QUESTION_EXAM,q.getE_id());
        values.put(QUESTION_TYPE,q.getType());
        values.put(QUESTION_TEXT,Question.toString(q));
        values.put(QUESTION_REPONSE,q.getAnswer());
        return db.insert(TABLE_QUESTIONS,null,values);
    }


    public void pushAnswer(String answers,int exam_id,int student_id ){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ID_ANSWER_EXAM,exam_id);
        values.put(ID_ANSWER_STUDENT,student_id);
        values.put(ANSWER_STRING,answers);
        db.insert(TABLE_ANSWER,null,values);
    }
    public List<Student> getAllStudents(){
        SQLiteDatabase db=this.getReadableDatabase();
        List<Student> students=new ArrayList<Student>();
            Cursor c = db.rawQuery("SELECT * FROM " + TABLE_STUDENT , null);
            if (c.moveToFirst())
                do {
                students.add(new Student(c.getString(c.getColumnIndex(STUDENT_NAME)),c.getString(c.getColumnIndex(STUDENT_MATRICULE)),c.getInt(c.getColumnIndex(ID_STUDENT))));
                } while (c.moveToNext());
            return  students;
    }
    public boolean isStudentExists(Boolean parMatricule,String matricule){
        SQLiteDatabase db=this.getReadableDatabase();
        if(parMatricule) {
            Cursor c = db.rawQuery("SELECT * FROM " + TABLE_STUDENT + " WHERE " + STUDENT_MATRICULE + " = " + matricule, null);
            if (c.moveToFirst())
                do {
                    return true;
                } while (c.moveToNext());
        }else {
            Cursor c = db.rawQuery("SELECT * FROM " + TABLE_STUDENT , null);
            if (c.moveToFirst())
                do {
                    return true;
                } while (c.moveToNext());
        }
        return false;
    }
    public long insertStudent(String name,String matricule){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(STUDENT_NAME,name);
        values.put(STUDENT_MATRICULE,matricule);
        return db.insert(TABLE_STUDENT,null,values);
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
        values.put(QUESTION_TEXT,Question.toString(q));


        return db.update(TABLE_QUESTIONS, values, ID_QUESTION_EXAM +"= ? AND "+ID_QUESTION+" = ?",
                new String[] { String.valueOf(q.getE_id()),String.valueOf(q.getId()) });
    }
}
