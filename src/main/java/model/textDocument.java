package model;

public class textDocument {
    private final int id;
    private final String place;
    private final String title;
    private final String body;

    public textDocument(int id, String place, String title, String body) {
        this.id = id;
        this.place = place;
        this.title = title;
        this.body = body;
    }

    public int getId() {
        return id;
    }

    public String getPlace() {
        return place;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getFullText() {
        return (title == null ? "" : title + " ") + (body == null ? "" : body);
    }

    @Override
    public String toString() {
        return "TextDocument{" +
                "id=" + id +
                ", place='" + place + '\'' +
                ", title='" + title + '\'' +
                ", bodyLength=" + (body == null ? 0 : body.length()) +
                '}';
    }
}
