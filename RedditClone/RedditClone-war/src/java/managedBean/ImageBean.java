/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBean;

import entity.Post;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import session.RedditSessionLocal;

/**
 *
 * @author p.tm
 */
@Named(value = "imageBean")
@ApplicationScoped
public class ImageBean {

  @EJB
  private RedditSessionLocal redditSessionLocal;

  public StreamedContent getImage() throws IOException {
    FacesContext context = FacesContext.getCurrentInstance();

    if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
      // So, we're rendering the HTML. Return a stub StreamedContent so that it will generate right URL.
      return new DefaultStreamedContent();
    } else {
      // So, browser is requesting the image. Return a real StreamedContent with the image bytes.
      String pId = context.getExternalContext().getRequestParameterMap().get("pId");
      try {
        Post p = redditSessionLocal.getPost(Long.valueOf(pId));
        return new DefaultStreamedContent(new ByteArrayInputStream(p.getImage()));
      } catch (Exception e) {
        // do nothing
      }
    }

    return null;
  }

}
