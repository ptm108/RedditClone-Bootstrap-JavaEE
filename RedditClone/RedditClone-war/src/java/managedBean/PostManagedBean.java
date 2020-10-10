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
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
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
@Named(value = "postManagedBean")
@ViewScoped
public class PostManagedBean implements Serializable {
  
  private String title;
  private String body;
  private String cName;
  
  @EJB
  private RedditSessionLocal redditSessionLocal;
  
  @Inject
  private AuthenticationManagedBean authenticationManagedBean;

  /**
   * Creates a new instance of PostManagedBean
   */
  public PostManagedBean() {
  }
  
  public void createPost() throws IOException {
    FacesContext context = FacesContext.getCurrentInstance();
    ExternalContext ec = context.getExternalContext();
    
    Map<String, String> params = ec.getRequestParameterMap();
    this.cName = params.get("cName");
    
    try {
      Community c = redditSessionLocal.getCommunity(cName);
      Redditor r = redditSessionLocal.getRedditor(authenticationManagedBean.getrId());
      
      Post p = new Post();
      p.setAuthor(r);
      p.setCommunity(c);
      p.setTimeCreated(new Date());
      p.setTimeEdited(p.getTimeCreated());
      p.setTitle(title);
      p.setBody(body);
      p.setComments(new ArrayList<>());
      p.setUpvoters(new ArrayList<>());
      p.setDownvoters(new ArrayList<>());
      
      redditSessionLocal.createPost(p);

      // update community
      c.addPost(p);
      redditSessionLocal.updateCommunity(c);

      // update redditor
      r.addPost(p);
      
      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Post created"));

      // redirect back to subreddit
      ec.redirect("/RedditClone-war/r/" + cName);
    } catch (NotFoundException e) {
      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
    }
    
  }
  
  public String getTitle() {
    return title;
  }
  
  public void setTitle(String title) {
    this.title = title;
  }
  
  public String getBody() {
    return body;
  }
  
  public void setBody(String body) {
    this.body = body;
  }
  
  public String getcName() {
    return cName;
  }
  
  public void setcName(String cName) {
    this.cName = cName;
  }
  
}
