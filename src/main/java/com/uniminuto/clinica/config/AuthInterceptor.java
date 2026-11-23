package com.uniminuto.clinica.config;

import com.uniminuto.clinica.apicontroller.AuthApiController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;
import java.util.Map;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    private final AuthApiController auth;
    public AuthInterceptor(AuthApiController auth){this.auth=auth;}

    @Override
    public boolean preHandle(HttpServletRequest request,HttpServletResponse response,Object handler) throws Exception {
        if (HttpMethod.OPTIONS.matches(request.getMethod())) return true;
        String path=request.getRequestURI().substring(request.getContextPath().length());
        if (path.equals("/auth/login") || path.equals("/auth/bootstrap")) return true;
        String header=request.getHeader("Authorization");
        if(header==null || !header.startsWith("Bearer ")) { response.sendError(401,"Autenticación requerida."); return false; }
        try {
            Map<String,Object> user=auth.currentUser(header.substring(7).trim());
            String role=String.valueOf(user.get("rol"));
            request.setAttribute("clinica.user",user);
            if(!allowed(path,request.getMethod(),role)){response.sendError(403,"No tienes permiso para esta operación.");return false;}
            return true;
        } catch(org.springframework.web.server.ResponseStatusException ex) {
            response.sendError(ex.getStatusCode().value(),ex.getReason()); return false;
        }
    }

    private boolean allowed(String path,String method,String role) {
        if(path.startsWith("/auth/usuarios")) return role.equals("ADMINISTRADOR");
        if(path.startsWith("/inventario") || path.startsWith("/facturacion") || path.startsWith("/reportes"))
            return role.equals("ADMINISTRADOR") || role.equals("RECEPCION");
        if(path.startsWith("/citas"))
            return role.equals("ADMINISTRADOR") || role.equals("RECEPCION") || role.equals("MEDICO");
        if(path.startsWith("/historias") || path.startsWith("/formulas"))
            return role.equals("ADMINISTRADOR") || role.equals("MEDICO");
        return role.equals("ADMINISTRADOR");
    }
}
