/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBean;

import entity.Community;
import entity.Post;
import entity.Redditor;
import exception.NotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.inject.Inject;
import session.RedditSessionLocal;

/**
 *
 * @author p.tm
 */
@Named(value = "homeManagedBean")
@ViewScoped
public class HomeManagedBean implements Serializable {

  private List<Post> posts;
  private List<Community> communities;

  private String searchTerm;
  private boolean searchPosts = false;
  private boolean searchCommunities = false;

  @EJB
  private RedditSessionLocal redditSessionLocal;

  @Inject
  private AuthenticationManagedBean authenticationManagedBean;

  /**
   * Creates a new instance of HomeManagedBean
   */
  public HomeManagedBean() {
  }

  @PostConstruct
  public void init() {
    this.posts = redditSessionLocal.getAllPosts("");
    this.communities = redditSessionLocal.searchCommunity("");
  }

  public void refreshPosts() throws NotFoundException {
    for (int i = 0; i < this.posts.size(); i++) {
      Post p = this.posts.get(i);
      p = redditSessionLocal.getPost(p.getId());
      this.posts.set(i, p);
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
      redditSessionLocal.upvotePost(authenticationManagedBean.getrId(), pId);
      refreshPosts();
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
      redditSessionLocal.downVotePost(authenticationManagedBean.getrId(), pId);
      refreshPosts();
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
      redditSessionLocal.removeVote(authenticationManagedBean.getrId(), pId);
      refreshPosts();
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

  public void toggleSearchPost() {
    this.searchTerm = "";
    this.searchCommunities = false;
    this.searchPosts = !this.searchPosts;
  }

  public void toggleSearchCommunity() {
    this.searchTerm = "";
    this.searchPosts = false;
    this.searchCommunities = !this.searchPosts;
  }

  public void searchPosts() {
    this.posts = redditSessionLocal.getAllPosts(searchTerm);
    this.searchTerm = "";
  }

  public void searchCommunities() {
    this.communities = redditSessionLocal.searchCommunity(searchTerm);
    this.searchTerm = "";
  }

  // getters setters
  public List<Post> getPosts() {
    return posts;
  }

  public void setPosts(List<Post> posts) {
    this.posts = posts;
  }

  public List<Community> getCommunities() {
    return communities;
  }

  public void setCommunities(List<Community> communities) {
    this.communities = communities;
  }

  public boolean isSearchPosts() {
    return searchPosts;
  }

  public void setSearchPosts(boolean searchPosts) {
    this.searchPosts = searchPosts;
  }

  public boolean isSearchCommunities() {
    return searchCommunities;
  }

  public void setSearchCommunities(boolean searchCommunities) {
    this.searchCommunities = searchCommunities;
  }

  public String getSearchTerm() {
    return searchTerm;
  }

  public void setSearchTerm(String searchTerm) {
    this.searchTerm = searchTerm;
  }

}
