/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filter;

import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import managedBean.AuthenticationManagedBean;

/**
 *
 * @author p.tm
 */
public class AuthenticationFilter implements Filter {
  
  @Inject
  private AuthenticationManagedBean authenticationManagedBean;
  
  public AuthenticationFilter() {
  }
  
  @Override
  public void doFilter(ServletRequest request, ServletResponse response,
          FilterChain chain)
          throws IOException, ServletException {
    HttpServletRequest request1 = (HttpServletRequest) request;
    if ((authenticationManagedBean == null || authenticationManagedBean.getrId() == -1)
            && request1.getRequestURI().startsWith("/settings/")) {
      //redirect to login page if user is not logged in
      //and trying to access "secret/*" paths
      ((HttpServletResponse) response).sendRedirect(request1.getContextPath() + "/login.xhtml");
    } else {
      //authenticated - continue
      chain.doFilter(request1, response);
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
