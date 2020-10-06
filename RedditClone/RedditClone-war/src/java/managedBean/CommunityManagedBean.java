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
  private List<Post> posts;
  private List<Redditor> members;

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
      cName = req.getAttribute("cName").toString();

      Community c = redditSessionLocal.getCommunity(cName);
      cId = c.getId();
      posts = c.getPosts();
      members = c.getMembers();
    } catch (Exception e) {
      // do nothing
    }
  } //end init

  public String createCommunity() {
    FacesContext context = FacesContext.getCurrentInstance();
    context.getExternalContext().getFlash().setKeepMessages(true);

    try {
      Redditor currRedditor = redditSessionLocal.getRedditor(authenticationManagedBean.getrId());

      Community c = new Community();
      c.setName(cName);
      c.setDescription(description);
      c.setPosts(new ArrayList<>());

      members = new ArrayList<>();
      members.add(currRedditor);
      c.setMembers(members);

      Community newC = redditSessionLocal.createCommunity(c);
      currRedditor.addCommunity(newC);

      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Community created"));
      return "/RedditClone-war/r/" + cName;
    } catch (Exception e) {
      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
      return null;
    }
  } //end createCommunity

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

}
