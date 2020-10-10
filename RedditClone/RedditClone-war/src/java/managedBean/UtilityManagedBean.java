/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBean;

import exception.NotFoundException;
import java.util.Map;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import session.RedditSessionLocal;

/**
 *
 * @author p.tm
 */
@Named(value = "utilityManagedBean")
@RequestScoped
public class UtilityManagedBean {

  @EJB
  private RedditSessionLocal redditSessionLocal;

  @Inject
  private AuthenticationManagedBean authenticationManagedBean;

  /**
   * Creates a new instance of UtilityManagedBean
   */
  public UtilityManagedBean() {
  }

  public void upvote() {
    FacesContext context = FacesContext.getCurrentInstance();
    ExternalContext ec = context.getExternalContext();

    Map<String, String> params = ec.getRequestParameterMap();
    Long pId = Long.parseLong(params.get("pId"));

    try {
      redditSessionLocal.upvotePost(authenticationManagedBean.getrId(), pId);
    } catch (NotFoundException e) {
      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
    }
  }

  public void downvote() {
    FacesContext context = FacesContext.getCurrentInstance();
    ExternalContext ec = context.getExternalContext();

    Map<String, String> params = ec.getRequestParameterMap();
    Long pId = Long.parseLong(params.get("pId"));

    try {
      redditSessionLocal.downVotePost(authenticationManagedBean.getrId(), pId);
    } catch (NotFoundException e) {
      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
    }
  }

  public void removeVote() {
    FacesContext context = FacesContext.getCurrentInstance();
    ExternalContext ec = context.getExternalContext();

    Map<String, String> params = ec.getRequestParameterMap();
    Long pId = Long.parseLong(params.get("pId"));

    try {
      redditSessionLocal.removeVote(authenticationManagedBean.getrId(), pId);
    } catch (NotFoundException e) {
      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
    }
  }

}
