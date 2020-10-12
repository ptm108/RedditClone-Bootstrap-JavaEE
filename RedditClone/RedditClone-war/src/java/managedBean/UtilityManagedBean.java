/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBean;

import entity.Comment;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;

/**
 *
 * @author p.tm
 */
@Named(value = "utilityManagedBean")
@RequestScoped
public class UtilityManagedBean {

  /**
   * Creates a new instance of UtilityManagedBean
   */
  public UtilityManagedBean() {
  }

  public String formatDate(Date date) {
    return date.toGMTString().substring(0, 11).trim().replace(" ", "-");
  }

  public int getCommentCount(List<Comment> comments) {
    int count = comments.size();

    for (Comment c : comments) {
      count += c.getChildren().size();
    }

    return count;
  }

  public String getTimeDifference(Date date) {
    Date currDate = new Date();

    long timeDiff = currDate.getTime() - date.getTime();

    long sec = TimeUnit.MILLISECONDS.toSeconds(timeDiff);
    if (sec < 60) {
      return sec + " seconds";
    }

    long mins = TimeUnit.MILLISECONDS.toMinutes(timeDiff);
    if (mins < 60) {
      return mins + " mins";
    }

    long h = TimeUnit.MILLISECONDS.toHours(timeDiff);
    if (h < 24) {
      return h + " hours";
    }

    return TimeUnit.MILLISECONDS.toDays(timeDiff) + " days";
  }

}
