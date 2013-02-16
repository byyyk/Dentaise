package pl.edu.agh.mkulpa.dentaise.server;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.springframework.context.ApplicationContext;

@Path("/notes")
@Produces({"application/xml"})
public class DentaiseRestWebService {
    @GET
    public String list() {
        return "<b>Hello World, I still need some work to be useful!</b>";
    }

}
