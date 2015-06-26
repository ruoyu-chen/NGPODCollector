package cn.edu.bistu.search.servlets;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

/**
 * Servlet Filter implementation class CharSetFilter
 */
@WebFilter("/CharSetFilter")
public class CharSetFilter implements Filter {

    /**
     * Default constructor. 
     */
    public CharSetFilter() {
        // TODO Auto-generated constructor stub
    }
    public void destroy() {
        //销毁过滤器
    }
 
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        // TODO Auto-generated method stub
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        chain.doFilter(request, response); 
    }
	@Override
	public void init(FilterConfig arg0) throws ServletException {
		
	}
 
}
