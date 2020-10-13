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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author p.tm
 */
@Entity
public class Redditor implements Serializable {

  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(unique = true, nullable = false)
  private String username;
  @Column(nullable = false)
  private String password;

  @Temporal(TemporalType.DATE)
  private Date dateJoined;
  @Column(unique = true, nullable = false)
  private String displayName;
  private String about;

  @ManyToMany(mappedBy = "members")
  private List<Community> communities;

  @ManyToMany(mappedBy = "moderators")
  private List<Community> moderatedCommunities;

  @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
  private List<Post> posts;

  @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
  private List<Comment> comments;

  @ManyToMany
  @JoinTable(name = "upvote_redditor", joinColumns = @JoinColumn(name = "redditor_id"),
          inverseJoinColumns = @JoinColumn(name = "post_id"))
  private List<Post> upvotedPosts;
  @ManyToMany

  @JoinTable(name = "downvote_redditor", joinColumns = @JoinColumn(name = "redditor_id"),
          inverseJoinColumns = @JoinColumn(name = "post_id"))
  private List<Post> downvotedPosts;

  // helper methods
  public void addCommunity(Community c) {
    this.communities.add(c);
  }

  public void removeCommunity(Community c) {
    this.communities.remove(c);
  }

  public void addPost(Post p) {
    this.posts.add(p);
  }

  public void deletePost(Post p) {
    this.posts.remove(p);
  }

  public void upvote(Post p) {
    this.downvotedPosts.remove(p);
    this.upvotedPosts.add(p);
  }

  public void downvote(Post p) {
    this.upvotedPosts.remove(p);
    this.downvotedPosts.add(p);
  }

  public void removeVote(Post p) {
    this.upvotedPosts.remove(p);
    this.downvotedPosts.remove(p);
  }

  // getters and setters
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Date getDateJoined() {
    return dateJoined;
  }

  public void setDateJoined(Date dateJoined) {
    this.dateJoined = dateJoined;
  }

  public List<Community> getCommunities() {
    return communities;
  }

  public void setCommunities(List<Community> communities) {
    this.communities = communities;
  }

  public List<Post> getPosts() {
    return posts;
  }

  public void setPosts(List<Post> posts) {
    this.posts = posts;
  }

  public List<Post> getUpvotedPosts() {
    return upvotedPosts;
  }

  public void setUpvotedPosts(List<Post> upvotedPosts) {
    this.upvotedPosts = upvotedPosts;
  }

  public List<Post> getDownvotedPosts() {
    return downvotedPosts;
  }

  public void setDownvotedPosts(List<Post> downvotedPosts) {
    this.downvotedPosts = downvotedPosts;
  }

  public String getAbout() {
    return about;
  }

  public void setAbout(String about) {
    this.about = about;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public List<Community> getModeratedCommunities() {
    return moderatedCommunities;
  }

  public void setModeratedCommunities(List<Community> moderatedCommunities) {
    this.moderatedCommunities = moderatedCommunities;
  }

  public List<Comment> getComments() {
    return comments;
  }

  public void setComments(List<Comment> comments) {
    this.comments = comments;
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
    if (!(object instanceof Redditor)) {
      return false;
    }
    Redditor other = (Redditor) object;
    if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "entity.Redditor[ id=" + id + " ]";
  }

}
