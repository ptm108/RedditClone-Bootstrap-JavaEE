/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author p.tm
 */
public class CommunityRedirectFilter implements Filter {

  public CommunityRedirectFilter() {
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response,
          FilterChain chain)
          throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) request;
    String[] uri = req.getRequestURI().split("/");

    String c = uri[uri.length - 1];
    req.setAttribute("cName", c);

    if (!req.getRequestURI().equals("/RedditClone-war/r/CommunityPage.xhtml")) {
      req.getRequestDispatcher("CommunityPage.xhtml").forward(req, response);
    } else {
      chain.doFilter(req, response);
    }

  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    //do nothing
  }

  @Override
  public void destroy() {
    //do nothing
  }

}
