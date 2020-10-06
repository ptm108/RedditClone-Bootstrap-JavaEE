/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author p.tm
 */
@Entity
public class Comment implements Serializable {

  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String body;

  @Temporal(TemporalType.TIMESTAMP)
  private Date timeCreated;
  @Temporal(TemporalType.TIMESTAMP)
  private Date timeEdited;

  @ManyToOne
  private Post post;

  @ManyToOne
  private Comment parent;
  @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER)
  private List<Comment> children;

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public Date getTimeCreated() {
    return timeCreated;
  }

  public void setTimeCreated(Timestamp timeCreated) {
    this.timeCreated = timeCreated;
  }

  public Date getTimeEdited() {
    return timeEdited;
  }

  public void setTimeEdited(Timestamp timeEdited) {
    this.timeEdited = timeEdited;
  }

  public Post getPost() {
    return post;
  }

  public void setPost(Post post) {
    this.post = post;
  }

  public List<Comment> getChildren() {
    return children;
  }

  public void setChildren(List<Comment> children) {
    this.children = children;
  }

  public Comment getParent() {
    return parent;
  }

  public void setParent(Comment parent) {
    this.parent = parent;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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
    if (!(object instanceof Comment)) {
      return false;
    }
    Comment other = (Comment) object;
    if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "entity.Comment[ id=" + id + " ]";
  }

}
