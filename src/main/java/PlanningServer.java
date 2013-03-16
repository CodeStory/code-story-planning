import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.sun.jersey.api.container.filter.GZIPContentEncodingFilter;
import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.net.httpserver.HttpServer;
import resources.AuthenticationResource;
import resources.PlanningResource;
import resources.StaticResource;

import java.io.File;
import java.io.IOException;

import static com.google.common.base.Objects.firstNonNull;
import static com.sun.jersey.api.core.ResourceConfig.PROPERTY_CONTAINER_REQUEST_FILTERS;
import static com.sun.jersey.api.core.ResourceConfig.PROPERTY_CONTAINER_RESPONSE_FILTERS;
import static java.lang.Integer.parseInt;
import static java.lang.String.format;

public class PlanningServer {
  private final Injector injector;
  private HttpServer server;

  public PlanningServer(Module module) {
    this.injector = Guice.createInjector(module);
  }

  public static void main(String[] args) throws IOException {
    int port = parseInt(firstNonNull(System.getenv("PORT"), "8080"));
    String workingDir = firstNonNull(System.getenv("PLANNING_ROOT"), "data");

    new PlanningServer(new PlanningServerModule(new File(workingDir))).start(port);
  }

  public void start(int port) throws IOException {
    System.out.println("Starting server on port: " + port);

    server = HttpServerFactory.create(format("http://localhost:%d/", port), configuration());
    server.start();
  }

  private ResourceConfig configuration() {
    DefaultResourceConfig config = new DefaultResourceConfig();
    config.getSingletons().add(injector.getInstance(AuthenticationResource.class));
    config.getSingletons().add(injector.getInstance(StaticResource.class));
    config.getSingletons().add(injector.getInstance(PlanningResource.class));

    config.getProperties().put(PROPERTY_CONTAINER_REQUEST_FILTERS, GZIPContentEncodingFilter.class);
    config.getProperties().put(PROPERTY_CONTAINER_RESPONSE_FILTERS, GZIPContentEncodingFilter.class);

    return config;
  }
}
