import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class HomePageTest extends AbstractPageTest {
  @Test
  public void should_display_homepage() {
    goTo("/");

    assertThat(title()).isEqualTo("CodeStory - Devoxx Planning");
    assertThat(url()).contains("/planning.html");
  }

  @Test
  public void should_display_toc() {
    goTo("/planning.html");

    assertThat(text(".toc a")).contains("09:30");
  }

  @Test
  public void should_display_sessions() {
    goTo("/planning.html");

    assertThat(text("#talk-1253 h2")).contains("CodeStory 2013 (Conference)");
    assertThat(text("#talk-1253 .speaker")).contains("David Gageot, Jean-laurent De morlhon @Auditorium Vendredi de 17:00 à 17:50");
    assertThat(text("#talk-1253 p")).contains("CodeStory 2013)");
  }

  @Test
  public void should_redirect_to_authenticate_when_user_stars() {
    goTo("/planning.html");

    click("#talk-1253 .star");

    assertThat(text("#auth a")).contains("Déconnexion");
    assertThat(url()).contains("/planning.html");
  }

  @Test
  public void should_show_no_star_id_not_logged_in() {
    goTo("/planning.html");

    assertThat(text("#auth a")).contains("Se connecter");
    assertThat(getCookie("userId")).isNull();
    assertThat(getCookie("screenName")).isNull();
    assertThat(find("#talk-1253 .star")).isNotEmpty();
    assertThat(find("#talk-1241 .star")).isNotEmpty();
    assertThat(find("#talk-1242 .star")).isNotEmpty();
    assertThat(find("#talk-1253 .starred")).isEmpty();
    assertThat(find("#talk-1241 .starred")).isEmpty();
    assertThat(find("#talk-1242 .starred")).isEmpty();
  }

  @Test
  public void should_log_in() {
    goTo("/planning.html");

    click("#login");

    assertThat(text("#auth a")).contains("Déconnexion");
    assertThat(text("#screenName")).contains("@arnold");
    assertThat(getCookie("userId").getValue()).isEqualTo("42");
    assertThat(getCookie("screenName").getValue()).isEqualTo("arnold");
  }

  @Test
  public void should_log_out() {
    goTo("/planning.html");

    click("#login");
    click("#logout");

    assertThat(getCookie("userId")).isNull();
    assertThat(text("#auth a")).contains("Se connecter");
  }

  @Test
  public void should_star() {
    goTo("/planning.html");

    click("#login");
    assertThat(find("#talk-1242 .starred")).isEmpty();

    click("#talk-1242 .star");
    assertThat(find("#talk-1242 .starred")).isNotEmpty();

    click("#talk-1242 .starred");
    assertThat(find("#talk-1242 .starred")).isEmpty();
  }

  @Test
  public void should_filter_with_url() {
    goTo("/planning.html?q=foo");

    assertThat(find("#search_box").getValue()).isEqualTo("foo");
  }
}