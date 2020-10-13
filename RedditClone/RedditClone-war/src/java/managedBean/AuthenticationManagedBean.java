/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBean;

import entity.Redditor;
import exception.NotFoundException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import session.RedditSessionLocal;

/**
 *
 * @author p.tm
 */
@Named(value = "authenticationManagedBean")
@SessionScoped
public class AuthenticationManagedBean implements Serializable {

  // hashing variables
  private final byte[] salt = "is3106".getBytes();
  private final int iterations = 1000;
  private final int keyLength = 512;

  private String displayName;
  private String about;
  private String username;
  private String password;
  private String password2;
  private Long rId = new Long(-1);

  @EJB
  private RedditSessionLocal redditSessionLocal;

  /**
   * Creates a new instance of AuthenticationManagedBean
   */
  public AuthenticationManagedBean() {
  }

  public String register() {
    FacesContext context = FacesContext.getCurrentInstance();
    context.getExternalContext().getFlash().setKeepMessages(true);

    if (!password.equals(password2)) {
      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Passwords do not match"));
      return null;
    }

    byte[] hashedPwBytes = hashPassword(password.toCharArray(), salt, iterations, keyLength);
    String hashedPassword = new String(hashedPwBytes, StandardCharsets.UTF_8);

    Redditor r = new Redditor();
    r.setUsername(username.trim());
    r.setPassword(hashedPassword);
    r.setDisplayName(username.trim());
    r.setDateJoined(new Date());

    try {
      redditSessionLocal.createRedditor(r);
      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Welcome to reddit, " + username));

      username = null;
      password = null;
      password2 = null;
      return "/login.xhtml?faces-redirect=true";
    } catch (Exception e) {
      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));

      username = null;
      password = null;
      password2 = null;
      return null;
    }

  } //end register

  public String login() {
    FacesContext context = FacesContext.getCurrentInstance();
    context.getExternalContext().getFlash().setKeepMessages(true);

    try {
      Redditor r = redditSessionLocal.getRedditor(username);

      byte[] hashedPwBytes = hashPassword(password.toCharArray(), salt, iterations, keyLength);
      String hashedPassword = new String(hashedPwBytes, StandardCharsets.UTF_8);

      if (r.getPassword().equals(hashedPassword)) {
        rId = r.getId();
        displayName = r.getDisplayName();
        about = r.getAbout();
        password = null;

        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Welcome back, " + username));
        return "/home.xhtml?faces-redirect=true";
      } else {
        throw new NotFoundException("Invalid credentials");
      }

    } catch (Exception e) {
      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
      return "/login.xhtml?faces-redirect=true";
    }

  } //end login

  public String logout() {
    FacesContext context = FacesContext.getCurrentInstance();
    context.getExternalContext().getFlash().setKeepMessages(true);
    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Logged out", "See you soon, " + username));

    rId = new Long(-1);
    displayName = null;
    username = null;
    password = null;

    return "/login.xhtml?faces-redirect=true";
  } //end logout

  public void updateProfile() {
    FacesContext context = FacesContext.getCurrentInstance();
    context.getExternalContext().getFlash().setKeepMessages(true);

    try {
      Redditor r = redditSessionLocal.getRedditor(rId);

      r.setAbout(about);
      r.setDisplayName(displayName);

      redditSessionLocal.updateRedditor(r);
      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Profile updated"));
    } catch (Exception e) {
      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
    }

  } //end login

  public void updatePassword() {
    FacesContext context = FacesContext.getCurrentInstance();
    context.getExternalContext().getFlash().setKeepMessages(true);

    if (!password.equals(password2)) {
      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Passwords do not match"));
    }

    byte[] hashedPwBytes = hashPassword(password.toCharArray(), salt, iterations, keyLength);
    String hashedPassword = new String(hashedPwBytes, StandardCharsets.UTF_8);

    try {
      Redditor r = redditSessionLocal.getRedditor(rId);

      r.setPassword(hashedPassword);

      password = null;
      password2 = null;

      redditSessionLocal.updateRedditor(r);
      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Password updated"));
    } catch (Exception e) {
      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));

      password = null;
      password2 = null;
    }

  } //end update password

  public byte[] hashPassword(final char[] password, final byte[] salt, final int iterations, final int keyLength) {

    try {
      SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
      PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
      SecretKey key = skf.generateSecret(spec);
      byte[] res = key.getEncoded();
      return res;
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new RuntimeException(e);
    }
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public String getPassword2() {
    return password2;
  }

  public void setPassword2(String password2) {
    this.password2 = password2;
  }

  public Long getrId() {
    return rId;
  }

  public void setrId(Long rId) {
    this.rId = rId;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getAbout() {
    return about;
  }

  public void setAbout(String about) {
    this.about = about;
  }

}
