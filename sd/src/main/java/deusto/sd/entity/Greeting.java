package deusto.sd.entity;

public class Greeting {
    private Long id;
    private String content = "Hello, ";
    
    public Greeting(Long id, String content) {
        this.id = id;
        this.content += content;
    }
    public Greeting() {
        this.id = Long.valueOf(0);
        this.content = "Sin contenido";
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
}
