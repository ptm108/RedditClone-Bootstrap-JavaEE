/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBean;

import entity.Community;
import entity.Post;
import entity.Redditor;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import javax.servlet.http.HttpServletRequest;
import session.RedditSessionLocal;

/**
 *
 * @author p.tm
 */
@Named(value = "communityManagedBean")
@ViewScoped
public class CommunityManagedBean implements Serializable {

  private Long cId = new Long(-1);
  private String cName;
  private String description = "";
  private String title;
  private List<Post> posts;
  private List<Redditor> members;

  private boolean joined = false;

  private String searchTerm;

  @EJB
  private RedditSessionLocal redditSessionLocal;

  @Inject
  private AuthenticationManagedBean authenticationManagedBean;

  // custom sorter
  static class DateSorter implements Comparator<Post> {

    @Override
    public int compare(Post p1, Post p2) {
      return p1.getTimeCreated().compareTo(p2.getTimeCreated()) * -1;
    }
  }

  /**
   * Creates a new instance of CommunityManagedBean
   */
  public CommunityManagedBean() {
  }

  @PostConstruct
  public void init() {
    ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
    HttpServletRequest req = (HttpServletRequest) ec.getRequest();

    Map<String, String> params = ec.getRequestParameterMap();
    String newName = params.get("cName");

    if (newName != null) {
      this.cName = newName;
    }

    try {
      Community c;
      // get community name from url path
      // r/<cName>
      if (cName == null) {
        cName = req.getAttribute("cName").toString();
        c = redditSessionLocal.getCommunity(cName);
      } else {
        c = redditSessionLocal.getCommunity(cId);
      }

      cId = c.getId();
      title = c.getTitle();
      description = c.getDescription();
      posts = c.getPosts();
      members = c.getMembers();

      // eager fetch
      posts.size();
      members.size();

      // check if current user is member already
      Redditor currRedditor = redditSessionLocal.getRedditor(authenticationManagedBean.getrId());

      joined = members.contains(currRedditor);

    } catch (Exception e) {
      // do nothing
    }

  } //end init

  public void createCommunity() {
    FacesContext context = FacesContext.getCurrentInstance();
    ExternalContext ec = context.getExternalContext();

    try {
      Redditor currRedditor = redditSessionLocal.getRedditor(authenticationManagedBean.getrId());

      Community c = new Community();
      c.setName(cName.trim().toLowerCase());
      c.setTitle(cName.trim());
      c.setDescription(description.trim());
      c.setPosts(new ArrayList<>());

      members = new ArrayList<>();
      members.add(currRedditor);
      c.setMembers(members);
      c.setModerators(members);

      Community newC = redditSessionLocal.createCommunity(c);

      // add community to redditor side
      currRedditor.addCommunity(newC);

      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Community created"));
      ec.redirect(ec.getRequestContextPath() + "/r/" + cName);
    } catch (Exception e) {
      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
    }
  } //end createCommunity

  public String joinCommunity() {
    FacesContext context = FacesContext.getCurrentInstance();

    if (authenticationManagedBean == null || authenticationManagedBean.getrId() < 0) {
      return "/login.xhtml?faces-redirect=true";
    }

    try {
      Redditor currRedditor = redditSessionLocal.getRedditor(authenticationManagedBean.getrId());
      Community c = redditSessionLocal.getCommunity(cId);

      // add user to community side
      members.add(currRedditor);
      c.addMember(currRedditor);
      Community newC = redditSessionLocal.updateCommunity(c);

      // add community to redditor side
      currRedditor.addCommunity(newC);

      joined = true;

      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Joined " + cName));
    } catch (Exception e) {
      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
    }

    // reload page data
    init();
    return null;

  } // end joinCommunity

  public String leaveCommunity() {
    FacesContext context = FacesContext.getCurrentInstance();
    context.getExternalContext().getFlash().setKeepMessages(true);

    if (authenticationManagedBean == null || authenticationManagedBean.getrId() < 0) {
      return "/login.xhtml?faces-redirect=true";
    }

    try {
      Redditor currRedditor = redditSessionLocal.getRedditor(authenticationManagedBean.getrId());
      Community c = redditSessionLocal.getCommunity(cId);

      // remove user on community side
      members.remove(currRedditor);
      c.removeMember(currRedditor);
      Community newC = redditSessionLocal.updateCommunity(c);

      // remove community on redditor side
      currRedditor.removeCommunity(newC);

      joined = false;

      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Joined " + cName));
    } catch (Exception e) {
      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
    }

    // reload page data
    init();
    return null;
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
    } catch (Exception e) {
      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
    }

    // reload data on page
    init();

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
    } catch (Exception e) {
      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
    }

    // reload data on page
    init();

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
    } catch (Exception e) {
      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
    }

    // reload data on page
    init();

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

  public void searchPosts() {
    FacesContext context = FacesContext.getCurrentInstance();

    try {
      this.posts = redditSessionLocal.getCommunityPosts(this.cId, searchTerm);
      Collections.sort(posts, new DateSorter());
    } catch (Exception e) {
      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
    }
  }

  public String getcName() {
    return cName;
  }

  public void setcName(String cName) {
    this.cName = cName;
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

  public Long getcId() {
    return cId;
  }

  public void setcId(Long cId) {
    this.cId = cId;
  }

  public List<Redditor> getMembers() {
    return members;
  }

  public void setMembers(List<Redditor> members) {
    this.members = members;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public boolean isJoined() {
    return joined;
  }

  public void setJoined(boolean joined) {
    this.joined = joined;
  }

  public String getSearchTerm() {
    return searchTerm;
  }

  public void setSearchTerm(String searchTerm) {
    this.searchTerm = searchTerm;
  }

}
