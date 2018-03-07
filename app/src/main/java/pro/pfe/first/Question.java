package pro.pfe.first;


public class Question {
    int type;

    public void setType(int type) {
        this.type = type;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setE_id(int e_id) {
        this.e_id = e_id;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setAnswer(Boolean answer) {
        this.answer = answer;
    }

    public int getType() {
        return type;
    }

    public String getQuestion() {
        return question;
    }

    public Boolean getAnswer() {
        return answer;
    }
    int id,e_id;

    public int getE_id() {
        return e_id;
    }

    public int getId() {
        return id;
    }

    String question;
    Boolean answer;

    public Question(int Type,String quest,Boolean answer,int  id,int e_id){
        this.type=Type;
        this.question=quest;
        this.answer=answer;
        this.id=id;
        this.e_id=e_id;
    }
}
