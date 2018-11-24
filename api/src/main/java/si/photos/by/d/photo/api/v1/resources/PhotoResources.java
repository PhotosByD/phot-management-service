package si.photos.by.d.photo.api.v1.resources;

import si.photos.by.d.photo.models.entities.Photo;
import si.photos.by.d.photo.services.beans.PhotoBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
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

    @POST
    public Response createPhoto(Photo photo) {
        if((photo.getTitle() == null || photo.getTitle().isEmpty())
        || (photo.getUserId() == null)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        else {
            photo = photoBean.createPhoto(photo);
        }

        if(photo.getId() != null) {
            return Response.status(Response.Status.CREATED).entity(photo).build();
        } else {
            return Response.status(Response.Status.CONFLICT).entity(photo).build();
        }
    }

    @DELETE
    @Path("{photoId}")
    public Response deleteCustomer(@PathParam("photoId") Integer photoId) {

        boolean deleted = photoBean.deletePhoto(photoId);

        if (deleted) {
            return Response.status(Response.Status.GONE).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
