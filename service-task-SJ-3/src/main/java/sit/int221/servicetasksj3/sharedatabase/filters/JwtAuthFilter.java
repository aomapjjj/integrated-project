package sit.int221.servicetasksj3.sharedatabase.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import sit.int221.servicetasksj3.exceptions.ErrorResponse;
import sit.int221.servicetasksj3.exceptions.ForbiddenException;
import sit.int221.servicetasksj3.exceptions.ItemNotFoundException;
import sit.int221.servicetasksj3.exceptions.UnauthorizedException;
import sit.int221.servicetasksj3.services.BoardService;
import sit.int221.servicetasksj3.services.CollaboratorService;
import sit.int221.servicetasksj3.sharedatabase.entities.AuthUser;
import sit.int221.servicetasksj3.sharedatabase.services.JwtTokenUtil;
import sit.int221.servicetasksj3.sharedatabase.services.JwtUserDetailsService;

import javax.security.sasl.AuthenticationException;
import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private BoardService boardService;
    @Autowired
    private CollaboratorService collaboratorService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        try {
            final String requestTokenHeader = request.getHeader("Authorization");
            String username = null;
            String jwtToken = null;

            boolean isTokenValid = false;
            String tokenError = null;

            if (request.getRequestURI().equals("/token") || request.getRequestURI().equals("/login")) {
                chain.doFilter(request, response);
                return;
            }

            if (requestTokenHeader != null) {
                if (requestTokenHeader.startsWith("Bearer ")) {
                    jwtToken = requestTokenHeader.substring(7);
                    try {
                        username = jwtTokenUtil.getUsernameFromToken(jwtToken);
                        isTokenValid = true;
                    } catch (IllegalArgumentException e) {
                        tokenError = e.getMessage();
                    } catch (ExpiredJwtException e) {
                        tokenError = e.getMessage();
                    }
                } else {
                    tokenError = "JWT Token does not begin with Bearer String";
                }
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);
                if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }

            if (request.getRequestURI().startsWith("/v3/boards/")) {
                handleRequest(request, isTokenValid, tokenError);
            } else if (!isTokenValid && !request.getRequestURI().equals("/v3/boards")) {
                System.out.println("JWT Token: " + jwtToken);
                System.out.println("Username from Token: " + username);
                throw new AuthenticationException("Invalid token");
            }
            chain.doFilter(request, response);
        } catch (Exception e) {
            handleException(response, e, request.getRequestURI());
        }
    }

    private void handleRequest(HttpServletRequest request, boolean isTokenValid, String tokenError) {
        String boardId = request.getRequestURI().split("/")[3];
        String requestMethod = request.getMethod();
        AuthUser currentUser = getCurrentUserDetails();
        boolean isBoardExist = boardService.boardExists(boardId);

        if (!isBoardExist) {
            if (!isTokenValid && !requestMethod.equals("GET")) {
                throw new UnauthorizedException("Unauthorized attempt to modify a non-existent board with ID: " + boardId);
            } else {
                throw new ItemNotFoundException("Board with ID: " + boardId + " not found.");
            }
        }

        boolean isPublic = boardService.isBoardPublic(boardId);

        if (currentUser != null) {
            boolean isCollaborator = collaboratorService.isCollaborator(boardId, currentUser.getOid());
            boolean isOwner = boardService.isBoardOwner(boardId);
            boolean isWriteAccess = collaboratorService.hasWriteAccess(boardId, currentUser.getOid());
            boolean isUserPending = collaboratorService.isPending(boardId, currentUser.getOid());

            if (isUserPending) {
                if (requestMethod.equals("DELETE") && request.getRequestURI().contains("/collabs/")) {
                    String[] uriParts = request.getRequestURI().split("/");
                    String collaboratorId = uriParts[uriParts.length - 1];
                    if (!collaboratorId.equals(currentUser.getOid())) {
                        throw new ForbiddenException(
                                "Pending collaborators can only remove themselves from board with ID: " + boardId
                        );
                    }
                    return;
                } else if (requestMethod.equals("GET") || requestMethod.equals("PATCH") && request.getRequestURI().contains("/collabs/") && request.getRequestURI().endsWith("/status")) {
                    return;
                } else {
                    throw new ForbiddenException(
                            "Pending collaborators are only allowed to access GET methods on board with ID: " + boardId
                    );
                }

            }

            if (isCollaborator) {
                if (isWriteAccess) {
                    if ((requestMethod.equals("PATCH") || requestMethod.equals("POST")) &&
                            !(request.getRequestURI().endsWith("/tasks") || request.getRequestURI().endsWith("/statuses"))) {
                        throw new ForbiddenException(
                                "Collaborators with WRITE access are not allowed to access PATCH methods on board with ID: " + boardId
                        );
                    }
                    if (requestMethod.equals("DELETE") && request.getRequestURI().contains("/collabs/")) {
                        String[] uriParts = request.getRequestURI().split("/");
                        String collaboratorId = uriParts[uriParts.length - 1];
                        if (!collaboratorId.equals(currentUser.getOid())) {
                            throw new ForbiddenException(
                                    "Collaborators with WRITE access can only remove themselves from board with ID: " + boardId
                            );
                        }
                    }
                } else {
                    if (requestMethod.equals("DELETE") && request.getRequestURI().contains("/collabs/")) {
                        String[] uriParts = request.getRequestURI().split("/");
                        String collaboratorId = uriParts[uriParts.length - 1];
                        if (!collaboratorId.equals(currentUser.getOid())) {
                            throw new ForbiddenException(
                                    "Collaborators without WRITE access can only remove themselves from board with ID: " + boardId
                            );
                        }
                    } else if (!requestMethod.equals("GET")) {
                        throw new ForbiddenException(
                                "Collaborators without WRITE access are only allowed to access GET methods on board with ID: " + boardId
                        );
                    }

                }
            } else if (!isOwner && (!isPublic || !requestMethod.equals("GET"))) {
                throw new ForbiddenException(
                        "Access forbidden: User with ID: " + currentUser.getOid() + " is not authorized to access board with ID: " + boardId
                );
            }
        } else {
            if (!isPublic) {
                if (requestMethod.equals("GET")) {
                    throw new ForbiddenException("Anonymous users are not allowed to view private board with ID: " + boardId);
                } else {
                    throw new UnauthorizedException("Authentication required for this action. Token error: " + tokenError);
                }
            } else {
                if (!requestMethod.equals("GET")) {
                    throw new UnauthorizedException("Token required for modifying public board with ID: " + boardId + ". Token error: " + tokenError);
                }
            }
        }
    }

    public AuthUser getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AuthUser) {
            return (AuthUser) authentication.getPrincipal();
        }
        return null;
    }

    public void handleException(HttpServletResponse response, Exception e, String uri) throws IOException {
        HttpStatus status;
        String message;

        if (e instanceof UnauthorizedException) {
            status = HttpStatus.UNAUTHORIZED;
            message = e.getMessage();
        } else if (e instanceof ItemNotFoundException) {
            status = HttpStatus.NOT_FOUND;
            message = e.getMessage();
        } else if (e instanceof ForbiddenException) {
            status = HttpStatus.FORBIDDEN;
            message = e.getMessage();
        } else {
            status = HttpStatus.UNAUTHORIZED;
            message = e.getMessage();
        }

        response.setStatus(status.value());
        response.setContentType("application/json");
        ErrorResponse errorResponse = new ErrorResponse(status.value(), message, uri);
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    }
}

