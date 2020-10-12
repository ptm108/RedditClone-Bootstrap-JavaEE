/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBean;

import entity.Post;
import entity.Redditor;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import session.RedditSessionLocal;

/**
 *
 * @author p.tm
 */
@Named(value = "userManagedBean")
@ViewScoped
public class UserManagedBean implements Serializable {

  private Long rId;
  private Redditor r;
  private List<Post> posts;

  private boolean search = false;
  private String searchTerm;

  @EJB
  private RedditSessionLocal redditSessionLocal;

  @Inject
  private AuthenticationManagedBean authenticationManagedBean;

  /**
   * Creates a new instance of UserManagedBean
   */
  public UserManagedBean() {
  }

  @PostConstruct
  public void init() {
    // check if user id param is avail, break otherwise
    FacesContext context = FacesContext.getCurrentInstance();
    ExternalContext ec = context.getExternalContext();

    Map<String, String> params = ec.getRequestParameterMap();
    String rIdString = params.get("rId");

    if (rIdString == null) {
      return;
    }

    try {
      this.rId = Long.parseLong(rIdString);
      this.r = redditSessionLocal.getRedditor(rId);
      this.posts = redditSessionLocal.getUserPosts(rId, "");
    } catch (Exception e) {
      try {
        ec.redirect(ec.getRequestContextPath());
      } catch (IOException err) {
        // do nothing
      }
    }
  }

  public void upvote() throws IOException {
    FacesContext context = FacesContext.getCurrentInstance();
    ExternalContext ec = context.getExternalContext();

    Map<String, String> params = ec.getRequestParameterMap();
    Long pId = Long.parseLong(params.get("pId"));

    if (authenticationManagedBean == null || authenticationManagedBean.getrId() < 0) {
      ec.redirect(ec.getRequestContextPath() + "/login.xhtml?faces-redirect=true");
    }

    try {
      Post p = redditSessionLocal.getPost(pId);
      int index = this.posts.indexOf(p);

      p = redditSessionLocal.upvotePost(authenticationManagedBean.getrId(), pId);
      this.posts.set(index, p);
    } catch (Exception e) {
      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
    }

  }

  public void downvote() throws IOException {
    FacesContext context = FacesContext.getCurrentInstance();
    ExternalContext ec = context.getExternalContext();

    Map<String, String> params = ec.getRequestParameterMap();
    Long pId = Long.parseLong(params.get("pId"));

    if (authenticationManagedBean == null || authenticationManagedBean.getrId() < 0) {
      ec.redirect(ec.getRequestContextPath() + "/login.xhtml?faces-redirect=true");
    }

    try {
      Post p = redditSessionLocal.getPost(pId);
      int index = this.posts.indexOf(p);

      p = redditSessionLocal.downVotePost(authenticationManagedBean.getrId(), pId);
      this.posts.set(index, p);
    } catch (Exception e) {
      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
    }

  }

  public void removeVote() throws IOException {
    FacesContext context = FacesContext.getCurrentInstance();
    ExternalContext ec = context.getExternalContext();

    Map<String, String> params = ec.getRequestParameterMap();
    Long pId = Long.parseLong(params.get("pId"));

    if (authenticationManagedBean == null || authenticationManagedBean.getrId() < 0) {
      ec.redirect(ec.getRequestContextPath() + "/login.xhtml?faces-redirect=true");
    }

    try {
      Post p = redditSessionLocal.getPost(pId);
      int index = this.posts.indexOf(p);

      p = redditSessionLocal.removeVote(authenticationManagedBean.getrId(), pId);
      this.posts.set(index, p);
    } catch (Exception e) {
      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
    }

  }

  public boolean isUpvoted(Long pId) {
    try {
      Post p = redditSessionLocal.getPost(pId);
      Redditor r = redditSessionLocal.getRedditor(authenticationManagedBean.getrId());

      return p.getUpvoters().contains(r);
    } catch (Exception e) {
      // do nothing
    }
    return false;
  }

  public boolean isDownvoted(Long pId) {
    try {
      Post p = redditSessionLocal.getPost(pId);
      Redditor r = redditSessionLocal.getRedditor(authenticationManagedBean.getrId());

      return p.getDownvoters().contains(r);
    } catch (Exception e) {
      // do nothing
    }
    return false;
  }

  public void toggleSearch() {
    this.search = !this.search;
  }

  public void searchPosts() {
    FacesContext context = FacesContext.getCurrentInstance();

    try {
      this.posts = redditSessionLocal.getUserPosts(rId, searchTerm);
    } catch (Exception e) {
      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
    }
  }

  public Long getrId() {
    return rId;
  }

  public void setrId(Long rId) {
    this.rId = rId;
  }

  public Redditor getR() {
    return r;
  }

  public void setR(Redditor r) {
    this.r = r;
  }

  public List<Post> getPosts() {
    return posts;
  }

  public void setPosts(List<Post> posts) {
    this.posts = posts;
  }

  public boolean isSearch() {
    return search;
  }

  public void setSearch(boolean search) {
    this.search = search;
  }

  public String getSearchTerm() {
    return searchTerm;
  }

  public void setSearchTerm(String searchTerm) {
    this.searchTerm = searchTerm;
  }

}
