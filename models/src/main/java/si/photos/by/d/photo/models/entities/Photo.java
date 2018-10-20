package si.photos.by.d.photo.models.entities;

import javax.persistence.*;

@Entity(name = "photo")
@NamedQueries(value =
    {
        @NamedQuery(name = "Photo.getAll", query = "SELECT p FROM photo p")
    }
)
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title")
    private String title;

    //TODO add blob or location of picture
    @Column(name = "user_id")
    private Integer userId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
