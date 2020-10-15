/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author p.tm
 */
@Entity
public class Post implements Serializable {

  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String title;

  @Lob
  @Column(length = 8096)
  private String body;

  @Temporal(TemporalType.TIMESTAMP)
  private Date timeCreated;
  @Temporal(TemporalType.TIMESTAMP)
  private Date timeEdited;

  @ManyToOne
  private Community community;

  @ManyToOne
  private Redditor author;

  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
  private List<Comment> comments;

  @ManyToMany(mappedBy = "upvotedPosts")
  private List<Redditor> upvoters;
  @ManyToMany(mappedBy = "downvotedPosts")
  private List<Redditor> downvoters;

  @Lob
  private byte[] image;

  // helper methods
  public void upvote(Redditor r) {
    this.downvoters.remove(r);
    this.upvoters.add(r);
  }

  public void downvote(Redditor r) {
    this.upvoters.remove(r);
    this.downvoters.add(r);
  }

  public void removeVote(Redditor r) {
    this.upvoters.remove(r);
    this.downvoters.remove(r);
  }

  public void addComment(Comment c) {
    this.comments.add(c);
  }

  public void removeComment(Comment c) {
    this.comments.remove(c);
  }

  // getters setters
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

  public List<Comment> getComments() {
    return comments;
  }

  public void setComments(List<Comment> comments) {
    this.comments = comments;
  }

  public Date getTimeCreated() {
    return timeCreated;
  }

  public void setTimeCreated(Date timeCreated) {
    this.timeCreated = timeCreated;
  }

  public Date getTimeEdited() {
    return timeEdited;
  }

  public void setTimeEdited(Date timeEdited) {
    this.timeEdited = timeEdited;
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

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public byte[] getImage() {
    return image;
  }

  public void setImage(byte[] image) {
    this.image = image;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (id != null ? id.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof Post)) {
      return false;
    }
    Post other = (Post) object;
    if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "entity.Post[ id=" + id + " ]";
  }

}
