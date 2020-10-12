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
import java.util.Date;
import java.util.List;
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
  public List<Community> searchCommunity(String searchTerm) {
    Query q;
    q = em.createQuery("SELECT c FROM Community c WHERE LOWER(c.title) LIKE :search OR LOWER(c.name) LIKE :search "
            + "OR LOWER(c.description) LIKE :search");
    q.setParameter("search", "%" + searchTerm.toLowerCase() + "%");
    return q.getResultList();
  }

  @Override
  public Post createPost(Post p) {
    em.persist(p);
    return p;
  }

  @Override
  public Post getPost(Long pId) throws NotFoundException {
    Post p = em.find(Post.class, pId);

    if (p != null) {
      return p;
    } else {
      throw new NotFoundException("Post not found");
    }
  }

  @Override
  public List<Post> getAllPosts(String searchTerm) {
    Query q;
    q = em.createQuery("SELECT p FROM Post p WHERE LOWER(p.title) LIKE :search OR LOWER(p.body) LIKE :search"
            + " ORDER BY p.timeCreated DESC");
    q.setParameter("search", "%" + searchTerm.toLowerCase() + "%");
    return q.getResultList();
  }

  @Override
  public List<Post> getUserPosts(Long rId, String searchTerm) {
    Query q;
    q = em.createQuery("SELECT p FROM Post p WHERE (p.author.id = :id) "
            + "AND (LOWER(p.title) LIKE :search OR LOWER(p.body) LIKE :search)");
    q.setParameter("id", rId);
    q.setParameter("search", "%" + searchTerm.toLowerCase() + "%");
    return q.getResultList();
  }

  @Override
  public List<Post> getCommunityPosts(Long cId, String searchTerm) {
    Query q;
    q = em.createQuery("SELECT p FROM Post p WHERE (p.community.id = :id) "
            + "AND (LOWER(p.title) LIKE :search OR LOWER(p.body) LIKE :search)");
    q.setParameter("id", cId);
    q.setParameter("search", "%" + searchTerm.toLowerCase() + "%");
    return q.getResultList();
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
    Post p = em.find(Post.class, pId);
    Community c = em.find(Community.class, p.getCommunity().getId());

    if (p == null) {
      throw new NotFoundException("Post not found");
    }

    em.remove(p);
    c.removePost(p);
    return p;
  }

  @Override
  public Post upvotePost(Long rId, Long pId) throws NotFoundException {
    Post currPost = em.find(Post.class, pId);
    Redditor currRedditor = em.find(Redditor.class, rId);

    if (currPost != null && currRedditor != null) {
      currPost.upvote(currRedditor);
      currRedditor.upvote(currPost);
    } else {
      throw new NotFoundException("Post or redditor not found");
    }

    return currPost;
  }

  @Override
  public Post downVotePost(Long rId, Long pId) throws NotFoundException {
    Post currPost = em.find(Post.class, pId);
    Redditor currRedditor = em.find(Redditor.class, rId);

    if (currPost != null && currRedditor != null) {
      currPost.downvote(currRedditor);
      currRedditor.downvote(currPost);
    } else {
      throw new NotFoundException("Post or redditor not found");
    }

    return currPost;
  }

  @Override
  public Post removeVote(Long rId, Long pId) throws NotFoundException {
    Post currPost = em.find(Post.class, pId);
    Redditor currRedditor = em.find(Redditor.class, rId);

    if (currPost != null && currRedditor != null) {
      currPost.removeVote(currRedditor);
      currRedditor.removeVote(currPost);

      return currPost;
    } else {
      throw new NotFoundException("Post or redditor not found");
    }
  }

  @Override
  public Comment createComment(Comment c) throws NotFoundException {
    em.persist(c);

    if (c.getPost() != null) {
      Long pId = c.getPost().getId();

      Post p = em.find(Post.class, pId);
      p.addComment(c);
    } // comment is a comment to post
    else {
      Comment parent = em.find(Comment.class, c.getParent().getId());
      parent.addReply(c);
    } // comment is a reply

    return c;

  }

  @Override
  public Comment updateComment(Comment c) throws NotFoundException {
    Comment currComment = em.find(Comment.class, c.getId());

    if (currComment != null) {
      currComment.setBody(c.getBody());
      currComment.setTimeEdited(new Date());
      em.flush();
      return currComment;
    } else {
      throw new NotFoundException("Comment not found");
    }

  }

  @Override
  public Comment deleteComment(Long cId) throws NotFoundException {
    Comment c = em.find(Comment.class, cId);

    if (c == null) {
      throw new NotFoundException("Comment not found");
    }

    if (c.getPost() != null) {
      Long pId = c.getPost().getId();
      Post p = em.find(Post.class, pId);
      p.removeComment(c);
    } // comment is a comment to post
    else {
      Comment parent = em.find(Comment.class, c.getParent().getId());
      parent.removeReply(c);
    } // comment is a reply

    em.remove(c);

    return c;
  }

  @Override
  public Comment getComment(Long cId) throws NotFoundException {
    Comment c = em.find(Comment.class, cId);

    if (c == null) {
      throw new NotFoundException("Comment not found");
    }
    return c;
  }

}
