/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBean;

import entity.Comment;
import entity.Community;
import entity.Post;
import entity.Redditor;
import exception.NotFoundException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import session.RedditSessionLocal;

/**
 *
 * @author p.tm
 */
@Named(value = "postManagedBean")
@ViewScoped
public class PostManagedBean implements Serializable {

  private Long pId;
  private String title;
  private String body;
  private String cName;

  private List<Redditor> upvoters;
  private List<Redditor> downvoters;

  private List<Comment> comments;

  private Community community;
  private Redditor author;

  private Date timeCreated;

  private UploadedFile uploadedFile;

  @EJB
  private RedditSessionLocal redditSessionLocal;

  @Inject
  private AuthenticationManagedBean authenticationManagedBean;

  /**
   * Creates a new instance of PostManagedBean
   */
  public PostManagedBean() {
  }

  @PostConstruct
  public void init() {
    // check if post id param is avail, break otherwise
    FacesContext context = FacesContext.getCurrentInstance();
    ExternalContext ec = context.getExternalContext();

    Map<String, String> params = ec.getRequestParameterMap();
    this.cName = params.get("cName");
    String pIdString = params.get("pId");

    if (pIdString == null) {
      return;
    }

    try {
      this.pId = Long.parseLong(pIdString);
      Post p = redditSessionLocal.getPost(pId);
      this.title = p.getTitle();
      this.body = p.getBody();
      this.upvoters = p.getUpvoters();
      this.downvoters = p.getDownvoters();
      this.comments = p.getComments();
      this.community = p.getCommunity();
      this.author = p.getAuthor();
      this.timeCreated = p.getTimeCreated();

    } catch (Exception e) {
      // do nothing
    }

  }

  public void createPost() throws IOException {
    FacesContext context = FacesContext.getCurrentInstance();
    ExternalContext ec = context.getExternalContext();

    Map<String, String> params = ec.getRequestParameterMap();
    this.cName = params.get("cName");

    if (cName != null) {
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

        // convert image to blob
        InputStream input = uploadedFile.getInputStream();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[10240];
        for (int length = 0; (length = input.read(buffer)) > 0;) {
          output.write(buffer, 0, length);
        }
        p.setImage(output.toByteArray());

        p = redditSessionLocal.createPost(p);

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
    } else {
      try {
        Redditor r = redditSessionLocal.getRedditor(authenticationManagedBean.getrId());

        Post p = new Post();
        p.setAuthor(r);
        p.setCommunity(null);
        p.setTimeCreated(new Date());
        p.setTimeEdited(p.getTimeCreated());
        p.setTitle(title);
        p.setBody(body);
        p.setComments(new ArrayList<>());
        p.setUpvoters(new ArrayList<>());
        p.setDownvoters(new ArrayList<>());

        // convert image to blob
        InputStream input = uploadedFile.getInputStream();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[10240];
        for (int length = 0; (length = input.read(buffer)) > 0;) {
          output.write(buffer, 0, length);
        }
        p.setImage(output.toByteArray());

        p = redditSessionLocal.createPost(p);
        // update redditor
        r.addPost(p);

        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Post created"));

        // redirect back to subreddit
        ec.redirect("/RedditClone-war/profile.xhtml?rId=" + authenticationManagedBean.getrId());
      } catch (NotFoundException e) {
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
      }
    }

  }

  public void editPost() throws IOException {
    FacesContext context = FacesContext.getCurrentInstance();
    ExternalContext ec = context.getExternalContext();

    try {
      Post p = redditSessionLocal.getPost(pId);
      p.setTitle(title);
      p.setBody(body);
      p.setTimeEdited(new Date());
      redditSessionLocal.updatePost(p);

      ec.redirect(ec.getRequestContextPath() + "/postPage.xhtml?cName=" + cName + "&pId=" + pId);
    } catch (Exception e) {
      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
    }
  }

  public void deletePost() throws IOException {
    FacesContext context = FacesContext.getCurrentInstance();
    ExternalContext ec = context.getExternalContext();

    try {
      redditSessionLocal.deletePost(pId);
      // redirect back to subreddit
      ec.redirect("/RedditClone-war/r/" + cName);
    } catch (Exception e) {
      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
    }
  }

  public void upvote() throws IOException {
    FacesContext context = FacesContext.getCurrentInstance();
    ExternalContext ec = context.getExternalContext();

    if (authenticationManagedBean == null || authenticationManagedBean.getrId() < 0) {
      ec.redirect(ec.getRequestContextPath() + "/login.xhtml?faces-redirect=true");
    }

    try {
      Post p = redditSessionLocal.upvotePost(authenticationManagedBean.getrId(), pId);
      this.upvoters = p.getUpvoters();
      this.downvoters = p.getDownvoters();
    } catch (Exception e) {
      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
    }
  }

  public void downvote() throws IOException {
    FacesContext context = FacesContext.getCurrentInstance();
    ExternalContext ec = context.getExternalContext();

    if (authenticationManagedBean == null || authenticationManagedBean.getrId() < 0) {
      ec.redirect(ec.getRequestContextPath() + "/login.xhtml?faces-redirect=true");
    }

    try {
      Post p = redditSessionLocal.downVotePost(authenticationManagedBean.getrId(), pId);
      this.upvoters = p.getUpvoters();
      this.downvoters = p.getDownvoters();
    } catch (Exception e) {
      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
    }
  }

  public void removeVote() throws IOException {
    FacesContext context = FacesContext.getCurrentInstance();
    ExternalContext ec = context.getExternalContext();

    if (authenticationManagedBean == null || authenticationManagedBean.getrId() < 0) {
      ec.redirect(ec.getRequestContextPath() + "/login.xhtml?faces-redirect=true");
    }

    try {
      Post p = redditSessionLocal.removeVote(authenticationManagedBean.getrId(), pId);
      this.upvoters = p.getUpvoters();
      this.downvoters = p.getDownvoters();
    } catch (Exception e) {
      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
    }
  }

  public boolean isUpvoted() {
    try {
      Post p = redditSessionLocal.getPost(pId);
      Redditor r = redditSessionLocal.getRedditor(authenticationManagedBean.getrId());

      return p.getUpvoters().contains(r);
    } catch (Exception e) {
      // do nothing
    }
    return false;
  }

  public boolean isDownvoted() {
    try {
      Post p = redditSessionLocal.getPost(pId);
      Redditor r = redditSessionLocal.getRedditor(authenticationManagedBean.getrId());

      return p.getDownvoters().contains(r);
    } catch (Exception e) {
      // do nothing
    }
    return false;
  }

  public void fileUploadListener(FileUploadEvent event) {
    FacesContext context = FacesContext.getCurrentInstance();
    this.uploadedFile = event.getFile();

    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Image uploaded"));
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

  public Long getpId() {
    return pId;
  }

  public void setpId(Long pId) {
    this.pId = pId;
  }

  public List<Redditor> getUpvoters() {
    return upvoters;
  }

  public void setUpvoters(List<Redditor> upvoters) {
    this.upvoters = upvoters;
  }

  public List<Redditor> getDownvoters() {
    return downvoters;
  }

  public void setDownvoters(List<Redditor> downvoters) {
    this.downvoters = downvoters;
  }

  public List<Comment> getComments() {
    return comments;
  }

  public void setComments(List<Comment> comments) {
    this.comments = comments;
  }

  public Community getCommunity() {
    return community;
  }

  public void setCommunity(Community community) {
    this.community = community;
  }

  public Redditor getAuthor() {
    return author;
  }

  public void setAuthor(Redditor author) {
    this.author = author;
  }

  public Date getTimeCreated() {
    return timeCreated;
  }

  public void setTimeCreated(Date timeCreated) {
    this.timeCreated = timeCreated;
  }

  public UploadedFile getUploadedFile() {
    return uploadedFile;
  }

  public void setUploadedFile(UploadedFile uploadedFile) {
    this.uploadedFile = uploadedFile;
  }

}
