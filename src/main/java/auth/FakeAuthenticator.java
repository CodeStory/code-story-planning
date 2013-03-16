package auth;

import java.net.URI;

public class FakeAuthenticator implements Authenticator {
  @Override
  public URI getAuthenticateURI() {
    return URI.create("/user/fakeauthenticate");
  }

  @Override
  public User authenticate(String oauthToken, String oauthVerifier) {
    return new User(42L, "arnold", "ring", "girl");
  }
}
