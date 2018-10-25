package si.photos.by.d.photo.services.beans;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import si.photos.by.d.photo.models.entities.Photo;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class PhotoBean {
    private Logger log = Logger.getLogger(PhotoBean.class.getName());

    @Inject
    private EntityManager em;

    public List<Photo> getPhotos(UriInfo uriInfo) {

        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery())
                .defaultOffset(0)
                .build();

        return JPAUtils.queryEntities(em, Photo.class, queryParameters);

    }

    public Photo getPhoto(Integer photoId) {
        Photo photo = em.find(Photo.class, photoId);

        if(photo == null) throw new NotFoundException();

        return photo;
    }

    public Photo createPhoto(Photo photo) {
        try{
            beginTx();
            em.persist(photo);
            commitTx();
        } catch (Exception e) {
            log.warning("There was a problem with saving new photo");
            rollbackTx();
        }
        log.info("Successfully saved new photo");
        return photo;
    }

    public Photo updatePhoto(Integer photoId, Photo photo) {
        Photo u = em.find(Photo.class, photoId);

        if(u == null) return null;

        try {
            beginTx();
            photo.setId(photoId);
            em.merge(photo);
            commitTx();
        } catch (Exception e) {
            log.warning("There was a problem with updating photo");
            rollbackTx();
        }
        log.info("Successfully updated photo");
        return photo;
    }

    public boolean deletePhoto(Integer userId) {
        Photo photo = em.find(Photo.class, userId);

        if(photo != null) {
            try {
                beginTx();
                em.remove(photo);
                commitTx();
            } catch (Exception e) {
                rollbackTx();
            }
        } else {
            return false;
        }

        return true;
    }

    private void beginTx() {
        if (!em.getTransaction().isActive())
            em.getTransaction().begin();
    }

    private void commitTx() {
        if (em.getTransaction().isActive())
            em.getTransaction().commit();
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive())
            em.getTransaction().rollback();
    }
}
