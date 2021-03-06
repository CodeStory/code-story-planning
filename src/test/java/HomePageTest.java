import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class HomePageTest extends AbstractPageTest {
  @Test
  public void should_display_homepage() {
    goTo("/");

    assertThat(title()).isEqualTo("CodeStory - Devoxx Planning");
  }

  @Test
  public void should_display_toc() {
    goTo("/");

    assertThat(text(".toc a")).contains("09:30");
  }

  @Test
  public void should_display_sessions() {
    goTo("/");

    assertThat(text("#talk-1253 h2")).contains("Android Bad Practices : comment foirer son app (Conference)");
    assertThat(text("#talk-1253 .speaker")).contains("Pierre-yves Ricau @Auditorium Vendredi de 17:00 à 17:50");
    assertThat(text("#talk-1253 p")).contains("Comment écrire du code incompréhensible ? Comment garantir l'incompatibilité avec les différentes versions d'Android ? Comment faire planter une appli en cas de rotation, d'interaction avec la backstack ou de retour à la Home ? Comment construire un build non reproductible ? Venez découvrir de nombreuses erreurs, comment les expliquer et comment les éviter à l'avenir. Nous couvrirons des problématiques allant du build à l'archi, avec au milieu beaucoup de code. J'en profiterais pour vous présenter la stack technique de Square.)");
  }

  @Test
  public void should_show_no_star_if_not_logged_in() {
    goTo("/");

    assertThat(find("#talk-1253 .star")).isNotEmpty();
    assertThat(find("#talk-1242 .star")).isNotEmpty();
    assertThat(find("#talk-1253 .starred")).isEmpty();
    assertThat(find("#talk-1242 .starred")).isEmpty();
  }

  @Test
  public void should_redirect_to_authenticate_when_user_stars() {
    goTo("/");

    assertThat(text("#auth a")).contains("Se connecter");
    assertThat(text("#screenName")).isEmpty();
    assertThat(getCookie("userId")).isNull();
    assertThat(getCookie("screenName")).isNull();

    click("#talk-1253 .star");

    assertThat(text("#auth a")).contains("Déconnexion");
    assertThat(url()).endsWith("/");
  }

  @Test
  public void should_log_in() {
    goTo("/");

    click("#login");

    assertThat(text("#auth a")).contains("Déconnexion");
    assertThat(text("#screenName")).contains("@arnold");
    assertThat(getCookie("userId").getValue()).isEqualTo("42");
    assertThat(getCookie("screenName").getValue()).isEqualTo("arnold");
  }

  @Test
  public void should_log_out() {
    goTo("/");

    click("#login");
    click("#logout");

    assertThat(text("#auth a")).contains("Se connecter");
    assertThat(text("#screenName")).isEmpty();
    assertThat(getCookie("userId")).isNull();
    assertThat(getCookie("#screenName")).isNull();
  }

  @Test
  public void should_star() {
    goTo("/");

    click("#login");
    assertThat(find("#talk-1242 .starred")).isEmpty();

    click("#talk-1242 .star");
    assertThat(find("#talk-1242 .starred")).isNotEmpty();

    click("#talk-1242 .starred");
    assertThat(find("#talk-1242 .starred")).isEmpty();
  }

  @Test
  public void should_filter_with_url() {
    goTo("/?q=foo");

    assertThat(find("#search_box").getValue()).isEqualTo("foo");
  }
}