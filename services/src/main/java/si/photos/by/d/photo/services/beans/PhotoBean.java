package si.photos.by.d.photo.services.beans;

import com.kumuluz.ee.discovery.annotations.DiscoverService;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.metrics.annotation.Timed;
import si.photos.by.d.photo.models.dtos.Comment;
import si.photos.by.d.photo.models.entities.Photo;
import si.photos.by.d.photo.services.configuration.AppProperties;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.UriInfo;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@ApplicationScoped
public class PhotoBean {
    private Logger log = Logger.getLogger(PhotoBean.class.getName());

    @Inject
    private EntityManager em;

    @Inject
    private AppProperties appProperties;

    private Client httpClient;

    @Inject
    @DiscoverService("comment-management-service")
    private Optional<String> commentUrl;

    @PostConstruct
    private void init() {
        httpClient = ClientBuilder.newClient();
    }

    public List<Photo> getPhotos(UriInfo uriInfo) {

        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery())
                .defaultOffset(0)
                .build();

        return JPAUtils.queryEntities(em, Photo.class, queryParameters);

    }

    public Photo getPhoto(Integer photoId) {
        Photo photo = em.find(Photo.class, photoId);

        if(photo == null) throw new NotFoundException();
        photo.setComments(getCommentsForPhoto(photoId));
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

    @Timed
    @CircuitBreaker(requestVolumeThreshold = 3)
    @Timeout(value = 2, unit = ChronoUnit.SECONDS)
    @Fallback(fallbackMethod = "getCommentsFallback")
    private List<Comment> getCommentsForPhoto(Integer photoId) {
        if(appProperties.isExternalServicesEnabled() && commentUrl.isPresent()) {
            try {
                return httpClient
                        .target(commentUrl.get() + "/v1/comments/photo/" + photoId)
                        .request().get(new GenericType<List<Comment>>() {
                        });
            } catch (WebApplicationException | ProcessingException e) {
                log.severe(e.getMessage());
                throw new InternalServerErrorException(e);
            }
        }
        return null;
    }

    private List<Comment> getCommentsFallback(Integer photoId) {
        return Collections.emptyList();
    }
}
