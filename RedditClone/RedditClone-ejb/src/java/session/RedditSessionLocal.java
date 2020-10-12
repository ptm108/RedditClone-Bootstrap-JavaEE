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
import java.util.List;
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

  public Community createCommunity(Community c);

  public Community updateCommunity(Community c) throws NotFoundException;

  public Community getCommunity(String cName) throws NotFoundException;

  public Community getCommunity(Long cId) throws NotFoundException;

  public Post createPost(Post p);

  public Post getPost(Long pId) throws NotFoundException;

  public List<Post> getAllPosts(String searchTerm);

  public List<Post> getUserPosts(Long rId, String searchTerm);

  public List<Post> getCommunityPosts(Long cId, String searchTerm);

  public Post updatePost(Post p) throws NotFoundException;

  public Post deletePost(Long pId) throws NotFoundException;

  public Post upvotePost(Long rId, Long pId) throws NotFoundException;

  public Post downVotePost(Long rId, Long pId) throws NotFoundException;

  public Post removeVote(Long rId, Long pId) throws NotFoundException;

  public Comment createComment(Comment c) throws NotFoundException;

  public Comment updateComment(Comment c) throws NotFoundException;

  public Comment deleteComment(Long cId) throws NotFoundException;

  public Comment getComment(Long cId) throws NotFoundException;

}
