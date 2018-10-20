package si.photos.by.d.photo.api.v1.resources;

import si.photos.by.d.photo.models.entities.Photo;
import si.photos.by.d.photo.services.PhotoBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@ApplicationScoped
@Path("/photos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PhotoResources {
    @Context
    private UriInfo uriInfo;

    @Inject
    private PhotoBean photoBean;

    @GET
    public Response getPhotos() {
        List<Photo> photos = photoBean.getPhotos(uriInfo);

        return Response.ok(photos).build();
    }
}
