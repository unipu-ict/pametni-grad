package hr.unipu.inf.ma.pametnigrad.post;

/**
 * Created by petra on 24.5.2018..
 */

public class PostClass {

    private int id;
    private String image;
    private String title;
    private String description;
    private String location;
    private boolean label;

    public PostClass(int id, String image, String title, String description, String location, boolean label){
        this.id = id;
        this.image = image;
        this.title = title;
        this.description = description;
        this.location = location;
        this.label = label;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }


    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }


    public String getLocation(){
        return location;
    }

    public void setLocation(String location){
        this.location = location;
    }


    public String getImage() {
        return image;
    }

    public void setImage(int String) {
        this.image = image;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public boolean isLabel() {
        return label;
    }

    public void setLabel(boolean label) {
        this.label = label;
    }
}
