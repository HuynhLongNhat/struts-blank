package utils;

import javax.servlet.*;
import java.io.IOException;

/**
 * A servlet filter that ensures request and response
 * use a specific character encoding (default UTF-8).
 */
public class CharacterEncodingFilter implements Filter {
    /** Default encoding (UTF-8) if not specified in web.xml */
    private String encoding = "UTF-8";

    /**
     * Initializes the filter and reads optional "encoding" parameter from web.xml.
     *
     * @param filterConfig Filter configuration from deployment descriptor
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String param = filterConfig.getInitParameter("encoding");
        if (param != null) {
            encoding = param;
        }
    }

    /**
     * Sets the character encoding for both request and response
     * before passing control to the next filter/servlet in the chain.
     *
     * @param request  the incoming servlet request
     * @param response the outgoing servlet response
     * @param chain    the filter chain to continue processing
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        request.setCharacterEncoding(encoding);
        response.setCharacterEncoding(encoding);
        chain.doFilter(request, response);
    }

    /**
     * Cleans up resources when filter is destroyed.
     */
    @Override
    public void destroy() {
        // No resources to release
    }
}
