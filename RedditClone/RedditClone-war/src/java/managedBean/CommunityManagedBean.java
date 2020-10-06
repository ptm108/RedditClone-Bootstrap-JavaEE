/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBean;

import entity.Community;
import entity.Post;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import session.RedditSessionLocal;

/**
 *
 * @author p.tm
 */
@Named(value = "communityManagedBean")
@ViewScoped
public class CommunityManagedBean implements Serializable {

  private String cName;
  private String title;
  private String description;
  private List<Post> posts;

  @EJB
  private RedditSessionLocal redditSessionLocal;

  @Inject
  private AuthenticationManagedBean authenticationManagedBean;

  /**
   * Creates a new instance of CommunityManagedBean
   */
  public CommunityManagedBean() {
  }

  @PostConstruct
  public void init() {
    ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
    HttpServletRequest req = (HttpServletRequest) ec.getRequest();
    HttpServletResponse res = (HttpServletResponse) ec.getResponse();

    cName = req.getAttribute("cName").toString();

    try {
      Community c = redditSessionLocal.getCommunity(cName);
      posts = c.getPosts();
    } catch (Exception e) {

      try {
        req.getRequestDispatcher("NewCommunity.xhtml").forward(req, res);
      } catch (Exception err) {
        // do nothing
      }

    }
  }

  public String getcName() {
    return cName;
  }

  public void setcName(String cName) {
    this.cName = cName;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<Post> getPosts() {
    return posts;
  }

  public void setPosts(List<Post> posts) {
    this.posts = posts;
  }

  public AuthenticationManagedBean getAuthenticationManagedBean() {
    return authenticationManagedBean;
  }

  public void setAuthenticationManagedBean(AuthenticationManagedBean authenticationManagedBean) {
    this.authenticationManagedBean = authenticationManagedBean;
  }

}
