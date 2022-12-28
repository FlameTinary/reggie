package top.sheldon.reggie.filter;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import top.sheldon.reggie.common.BaseContext;
import top.sheldon.reggie.common.R;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebFilter(filterName = "LoginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    // 路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        log.info("拦截到请求：{}", request.getRequestURI());

        // 获取本次请求的uri
        String requestURI = request.getRequestURI();
        // 判断本次请求是否需要处理
        String[] uris = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };
        boolean check = check(uris, requestURI);
        // 如果不需要处理，直接放行
        if (check) {
            filterChain.doFilter(request, response);
            return;
        }
        // 判断后台用户登录状态，如果已经登录，直接放行
        if (request.getSession().getAttribute("employee") != null) {
            Long empId = (Long) request.getSession().getAttribute("employee");
            long id = Thread.currentThread().getId();
            BaseContext.setCurrentId(empId);
            filterChain.doFilter(request, response);
            return;
        }

        // 判断前端用户登录状态，如果已经登录，直接放行
        if (request.getSession().getAttribute("user") != null) {
            Long userId = (Long) request.getSession().getAttribute("user");
            long id = Thread.currentThread().getId();
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request, response);
            return;
        }

        // 如果未登录则返回未登录结果，采用输出流方式向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    private boolean check(String[] uris, String requestURI){
        for (String uri : uris) {
            boolean match = PATH_MATCHER.match(uri, requestURI);
            if (match){
                return true;
            }
        }

        return false;
    }
}
