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
import javax.ejb.Local;

/**
 *
 * @author p.tm
 */
@Local
public interface RedditSessionLocal {

  public void createRedditor(Redditor r);

  public void updateRedditor(Redditor r) throws NotFoundException;

  public Redditor getRedditor(Long rId) throws NotFoundException;

  public Redditor getRedditor(String username) throws NotFoundException;

  public void createCommunity(Community c);

  public void updateCommunity(Community c) throws NotFoundException;

  public Community getCommunity(String cName) throws NotFoundException;

  public Community getCommunity(Long cId) throws NotFoundException;

  public void createPost(Post p);

  public void updatePost(Post p);

  public Post deletePost(Long pId);

  public void upvotePost(Long rId, Long pId) throws NotFoundException;

  public void downVotePost(Long rId, Long pId) throws NotFoundException;

  public void createComment(Long rId, Long pId, Comment c) throws NotFoundException;

  public void replyComment(Long cId, Comment c) throws NotFoundException;

  public void updateComment(Long cId, Comment c) throws NotFoundException;

  public Comment deleteComment(Long cId) throws NotFoundException;

}
