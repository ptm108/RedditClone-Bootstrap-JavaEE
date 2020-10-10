/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBean;

import entity.Community;
import entity.Post;
import entity.Redditor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
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

    try {
      // get community name from url path
      // r/<cName>
      cName = req.getAttribute("cName").toString();

      Community c = redditSessionLocal.getCommunity(cName);
      cId = c.getId();
      title = c.getTitle();
      description = c.getDescription();
      posts = c.getPosts();
      members = c.getMembers();
      posts.size();

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
    ec.getFlash().setKeepMessages(true);

    try {
      Redditor currRedditor = redditSessionLocal.getRedditor(authenticationManagedBean.getrId());

      Community c = new Community();
      c.setName(cName);
      c.setTitle(cName);
      c.setDescription(description);
      c.setPosts(new ArrayList<>());

      members = new ArrayList<>();
      members.add(currRedditor);
      c.setMembers(members);
      c.setModerators(members);

      Community newC = redditSessionLocal.createCommunity(c);

      // add community to redditor side
      currRedditor.addCommunity(newC);
      redditSessionLocal.updateRedditor(currRedditor);

      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Community created"));
      ec.redirect(ec.getRequestContextPath() + "/r/" + cName);
    } catch (Exception e) {
      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
    }
  } //end createCommunity

  public String joinCommunity() {
    FacesContext context = FacesContext.getCurrentInstance();
    context.getExternalContext().getFlash().setKeepMessages(true);

    if (authenticationManagedBean == null || authenticationManagedBean.getrId() < 0) {
      return "/login.xhtml/faces-redirect=true";
    }

    try {
      Redditor currRedditor = redditSessionLocal.getRedditor(authenticationManagedBean.getrId());
      Community c = redditSessionLocal.getCommunity(cId);
      c.addMember(currRedditor);

      // add user to community side
      Community newC = redditSessionLocal.updateCommunity(c);

      // add community to redditor side
      currRedditor.addCommunity(newC);
      redditSessionLocal.updateRedditor(currRedditor);

      joined = true;

      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Joined " + cName));
      return null;
    } catch (Exception e) {
      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
      return null;
    }
  } // end joinCommunity

  public String leaveCommunity() {
    FacesContext context = FacesContext.getCurrentInstance();
    context.getExternalContext().getFlash().setKeepMessages(true);

    if (authenticationManagedBean == null || authenticationManagedBean.getrId() < 0) {
      return "/login.xhtml/faces-redirect=true";
    }

    try {
      Redditor currRedditor = redditSessionLocal.getRedditor(authenticationManagedBean.getrId());
      Community c = redditSessionLocal.getCommunity(cId);
      c.removeMember(currRedditor);

      // remove user on community side
      Community newC = redditSessionLocal.updateCommunity(c);

      // remove community on redditor side
      currRedditor.removeCommunity(newC);
      redditSessionLocal.updateRedditor(currRedditor);

      joined = false;

      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Joined " + cName));
      return null;
    } catch (Exception e) {
      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
      return null;
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

}
