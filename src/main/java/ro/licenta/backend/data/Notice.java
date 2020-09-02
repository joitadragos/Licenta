package ro.licenta.backend.data;

public class Notice {
    private int id;
    private String name;
    private String text;

    public Notice(){
        this.id = id;
        this.name = name;
        this.text = text;
    }

    public Notice(String name, String text) {
        super();
        this.name = name;
        this.text = text;
    }


    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName(){ return name; }
    public void setName(String name){ this.name = name;}

    public String getText() {
        return text;
    }
    public void setText(String Text) {
        this.text = Text;
    }

}
