package controllers;

import static play.data.Form.form;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.index;
import views.html.login;

public class Application extends Controller {

	@Security.Authenticated(Secured.class)
    public static Result index() {
        return ok(index.render("For the swarm."));
    }
  
    public static Result login() {
        return ok(login.render(form(Login.class)));
    }
    
    public static Result authenticate() {
        Form<Login> loginForm = form(Login.class).bindFromRequest();
        if (loginForm.hasErrors()) {
        	return badRequest(login.render(loginForm));
        } else {
        	session().clear();
        	session().put("username", loginForm.get().username);
        	return redirect(routes.Application.index());
        }
    }
    
    public static class Login {

        public String username;
        public String password;
        
        public String validate() {
            if (!"admin".equals(username) || !"dupa123".equals(password)) {
              return "Invalid username or password";
            }
            return null;
        }

    }
}
