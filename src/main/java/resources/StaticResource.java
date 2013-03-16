package resources;

import com.google.inject.Singleton;
import org.jcoffeescript.JCoffeeScriptCompileException;
import org.jcoffeescript.JCoffeeScriptCompiler;
import org.jcoffeescript.Option;
import org.lesscss.LessCompiler;
import org.lesscss.LessException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

@Path("/static/{version : [^/]*}/")
@Singleton
public class StaticResource extends AbstractResource {
  private final LessCompiler lessCompiler;
  private final JCoffeeScriptCompiler coffeeScriptCompiler;

  public StaticResource() {
    lessCompiler = new LessCompiler();
    coffeeScriptCompiler = new JCoffeeScriptCompiler(Arrays.asList(Option.BARE));
  }

  @GET
  @Path("{path : .*}.css")
  @Produces("text/css;charset=UTF-8")
  public Response cssOrLess(@PathParam("path") String path) throws IOException, LessException {
    // Css
    if (exists(path + ".css")) {
      File css = file(path + ".css");
      return okTemplatize(css);
    }

    // Less
    File output = new File("target", path + ".css");
    synchronized (lessCompiler) {
      lessCompiler.compile(file(path + ".less"), output, false);
    }
    return okTemplatize(output);
  }

  @GET
  @Path("{path : .*}.js")
  @Produces("application/javascript;charset=UTF-8")
  public Response js(@PathParam("path") String path) throws JCoffeeScriptCompileException {
    // Js
    if (exists(path + ".js")) {
      File js = file(path + ".js");
      return ok(js);
    }

    // Coffee
    File coffee = file(path + ".coffee");
    String js;
    synchronized (coffeeScriptCompiler) {
      js = coffeeScriptCompiler.compile(read(coffee));
    }
    return ok(js, coffee.lastModified());
  }

  @GET
  @Path("{path : .*\\.png}")
  @Produces("image/png")
  public Response png(@PathParam("path") String path) {
    return ok(file(path));
  }

  @GET
  @Path("{path : .*\\.jpg}")
  @Produces("image/jpeg")
  public Response jpeg(@PathParam("path") String path) {
    return ok(file(path));
  }
}
