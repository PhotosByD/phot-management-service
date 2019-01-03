package si.photos.by.d.photo.models.entities;

import si.photos.by.d.photo.models.dtos.Comment;

import javax.persistence.*;
import java.util.List;

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

    @Column(name = "photo_url")
    private String photoURL;

    @Transient
    private List<Comment> comments;

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

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
