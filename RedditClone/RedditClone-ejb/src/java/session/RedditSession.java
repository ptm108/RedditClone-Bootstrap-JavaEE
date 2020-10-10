/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.Comment;
import entity.Community;
import entity.Post;
import entity.Redditor;
import exception.NotFoundException;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author p.tm
 */
@Stateless
public class RedditSession implements RedditSessionLocal {

  @PersistenceContext
  private EntityManager em;

  @Override
  public void createRedditor(Redditor r) {
    em.persist(r);
  } //end createRedditor

  @Override
  public void updateRedditor(Redditor r) throws NotFoundException {
    Redditor currRedditor = em.find(Redditor.class, r.getId());

    if (currRedditor != null) {
      currRedditor.setPassword(r.getPassword());
      currRedditor.setUsername(r.getUsername());
      currRedditor.setDisplayName(r.getDisplayName());
      currRedditor.setAbout(r.getAbout());
      currRedditor.setCommunities(r.getCommunities());
      currRedditor.setPosts(r.getPosts());
      em.flush();
    } else {
      throw new NotFoundException("Not found");
    }
  } //end updateRedditor

  @Override
  public Redditor getRedditor(String username) throws NotFoundException {
    Query q;
    if (username != null) {
      q = em.createQuery("SELECT r FROM Redditor r WHERE LOWER(r.username) = :username");
      q.setParameter("username", username.toLowerCase());
      return (Redditor) q.getSingleResult();
    } else {
      throw new NotFoundException("Redditor not found");
    }
  }

  @Override
  public Redditor getRedditor(Long rId) throws NotFoundException {
    Redditor r = em.find(Redditor.class, rId);

    if (r != null) {
      return r;
    } else {
      throw new NotFoundException("Not found");
    }
  } //end getRedditor

  @Override
  public Community createCommunity(Community c) {
    em.persist(c);
    return c;
  }

  @Override
  public Community updateCommunity(Community c) throws NotFoundException {
    Community currCommunity = em.find(Community.class, c.getId());

    if (currCommunity != null) {
      currCommunity.setMembers(c.getMembers());
      currCommunity.setName(c.getName());
      currCommunity.setDescription(c.getDescription());
      currCommunity.setTitle(c.getTitle());
      currCommunity.setPosts(c.getPosts());
      return currCommunity;
    } else {
      throw new NotFoundException("Not found");
    }
  }

  @Override
  public Community getCommunity(String cName) throws NotFoundException {

    System.out.println("********************************" + cName);
    Query q;
    if (cName != null) {
      q = em.createQuery("SELECT c FROM Community c WHERE LOWER(c.name) = :name");
      q.setParameter("name", cName.toLowerCase());
      return (Community) q.getSingleResult();
    } else {
      throw new NotFoundException("Redditor not found");
    }
  }

  @Override
  public Community getCommunity(Long cId) throws NotFoundException {
    Community c = em.find(Community.class, cId);

    if (c != null) {
      return c;
    } else {
      throw new NotFoundException("Not found");
    }
  }

  @Override
  public Post createPost(Post p) {
    em.persist(p);
    return p;
  }

  @Override
  public Post updatePost(Post p) throws NotFoundException {
    Post currPost = em.find(Post.class, p.getId());

    if (currPost != null) {
      currPost.setTitle(p.getTitle());
      currPost.setBody(p.getBody());
      currPost.setTimeEdited(p.getTimeEdited());
      currPost.setComments(p.getComments());
      return currPost;
    } else {
      throw new NotFoundException("Not found");
    }
  }

  @Override
  public Post deletePost(Long pId) throws NotFoundException {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public Post upvotePost(Long rId, Long pId) throws NotFoundException {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public Post downVotePost(Long rId, Long pId) throws NotFoundException {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void createComment(Long rId, Long pId, Comment c) throws NotFoundException {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void replyComment(Long cId, Comment c) throws NotFoundException {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void updateComment(Long cId, Comment c) throws NotFoundException {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public Comment deleteComment(Long cId) throws NotFoundException {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

}
