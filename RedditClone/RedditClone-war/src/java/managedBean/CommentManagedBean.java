/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBean;

import entity.Comment;
import entity.Post;
import entity.Redditor;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import session.RedditSessionLocal;

/**
 *
 * @author p.tm
 */
@Named(value = "commentManagedBean")
@SessionScoped
public class CommentManagedBean implements Serializable {

  private Long selectedCommentId = new Long(-1);
  private Long selectedReplyId = new Long(-1);
  private String comment;

  private String editComment;

  @EJB
  private RedditSessionLocal redditSessionLocal;

  @Inject
  private AuthenticationManagedBean authenticationManagedBean;

  @Inject
  private PostManagedBean postManagedBean;

  /**
   * Creates a new instance of CommentManagedBean
   */
  public CommentManagedBean() {
  }

  public void createComment() throws IOException {
    FacesContext context = FacesContext.getCurrentInstance();
    ExternalContext ec = context.getExternalContext();

    if (authenticationManagedBean == null || authenticationManagedBean.getrId() < 0) {
      ec.redirect(ec.getRequestContextPath() + "/login.xhtml?faces-redirect=true");
    }

    try {
      Redditor r = redditSessionLocal.getRedditor(authenticationManagedBean.getrId());
      Post p = redditSessionLocal.getPost(postManagedBean.getpId());

      Comment c = new Comment();
      c.setAuthor(r);
      c.setPost(p);
      c.setBody(comment);
      c.setTimeCreated(new Date());
      c.setTimeEdited(new Date());
      c.setChildren(new ArrayList<>());

      c = redditSessionLocal.createComment(c);

      postManagedBean.getComments().add(c);
      comment = null;
    } catch (Exception e) {
      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
    }
  }

  public void deleteComment() {
    FacesContext context = FacesContext.getCurrentInstance();
    ExternalContext ec = context.getExternalContext();

    Map<String, String> params = ec.getRequestParameterMap();
    Long cId = Long.parseLong(params.get("cId"));

    try {
      Comment c = redditSessionLocal.deleteComment(cId);
      postManagedBean.getComments().remove(c);
    } catch (Exception e) {
      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
    }
  }

  public void deleteReply() {
    FacesContext context = FacesContext.getCurrentInstance();
    ExternalContext ec = context.getExternalContext();

    Map<String, String> params = ec.getRequestParameterMap();
    Long cId = Long.parseLong(params.get("cId"));
    Long parentCId = Long.parseLong(params.get("parentCId"));

    try {
      Comment c = redditSessionLocal.deleteComment(cId);
      Comment parent = redditSessionLocal.getComment(parentCId);

      int index = postManagedBean.getComments().indexOf(parent);

      postManagedBean.getComments().get(index).getChildren().remove(c);
    } catch (Exception e) {
      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
    }
  }

  public void selectComment() {
    FacesContext context = FacesContext.getCurrentInstance();
    ExternalContext ec = context.getExternalContext();

    Map<String, String> params = ec.getRequestParameterMap();
    this.selectedCommentId = Long.parseLong(params.get("cId"));

    try {
      this.editComment = redditSessionLocal.getComment(selectedCommentId).getBody();
    } catch (Exception e) {
      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
    }
  }

  public void selectReply() {
    FacesContext context = FacesContext.getCurrentInstance();
    ExternalContext ec = context.getExternalContext();

    Map<String, String> params = ec.getRequestParameterMap();
    this.selectedReplyId = Long.parseLong(params.get("cId"));
  }

  public void unselectComment() {
    this.selectedCommentId = new Long(-1);
    this.selectedReplyId = new Long(-1);
  }

  public void updateComment() {
    FacesContext context = FacesContext.getCurrentInstance();

    try {
      Comment c = redditSessionLocal.getComment(selectedCommentId);
      int index = postManagedBean.getComments().indexOf(c);

      c.setBody(editComment);
      c = redditSessionLocal.updateComment(c);

      // reset selectedCommentId
      unselectComment();
      // update comments 
      postManagedBean.getComments().set(index, c);
    } catch (Exception e) {
      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
    }
  }

  public void replyComment() {
    FacesContext context = FacesContext.getCurrentInstance();

    try {
      Redditor r = redditSessionLocal.getRedditor(authenticationManagedBean.getrId());
      Comment c = redditSessionLocal.getComment(selectedReplyId);
      int index = postManagedBean.getComments().indexOf(c);

      Comment reply = new Comment();
      reply.setAuthor(r);
      reply.setParent(c);
      reply.setBody(editComment);
      reply.setTimeCreated(new Date());
      reply.setTimeEdited(new Date());
      reply.setChildren(new ArrayList<>());

      reply = redditSessionLocal.createComment(reply);

      // editComment
      editComment = null;
      // reset selectedCommentId
      unselectComment();
      // update comments 
      c.getChildren().add(reply);
      postManagedBean.getComments().set(index, c);
    } catch (Exception e) {
      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
    }
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public Long getSelectedCommentId() {
    return selectedCommentId;
  }

  public void setSelectedCommentId(Long selectedCommentId) {
    this.selectedCommentId = selectedCommentId;
  }

  public String getEditComment() {
    return editComment;
  }

  public void setEditComment(String editComment) {
    this.editComment = editComment;
  }

  public Long getSelectedReplyId() {
    return selectedReplyId;
  }

  public void setSelectedReplyId(Long selectedReplyId) {
    this.selectedReplyId = selectedReplyId;
  }

}
