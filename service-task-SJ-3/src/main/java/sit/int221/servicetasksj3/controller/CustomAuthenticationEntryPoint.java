//package sit.int221.servicetasksj3.controller;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.AuthenticationEntryPoint;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//
//@Component
//public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
//    @Override
//
//    public void commence(HttpServletRequest request, HttpServletResponse response,
//                         AuthenticationException authException) throws IOException {
//        String responseBody = String.format("""
//                {
//                    "status": 401,
//                    "error" : "%s",
//                    "message" : "No JWT Token provided or Token expired or Invalid Token.",
//                    "instance" : "uri=%s"
//                }
//                """, authException.getLocalizedMessage(), request.getRequestURI());
//
//        response.setContentType("application/json");
//        response.setStatus(401);
//        response.getWriter().println(responseBody);
//        response.getWriter().flush();
//        return;
//    }
//}
