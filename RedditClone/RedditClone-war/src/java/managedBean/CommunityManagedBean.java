/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBean;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author p.tm
 */
@Named(value = "communityManagedBean")
@ViewScoped
public class CommunityManagedBean implements Serializable {

  private String cName;

  /**
   * Creates a new instance of CommunityManagedBean
   */
  public CommunityManagedBean() {
  }

  @PostConstruct
  public void init() {
    HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    cName = req.getAttribute("cName").toString();
  }

  public String getcName() {
    return cName;
  }

  public void setcName(String cName) {
    this.cName = cName;
  }

}
