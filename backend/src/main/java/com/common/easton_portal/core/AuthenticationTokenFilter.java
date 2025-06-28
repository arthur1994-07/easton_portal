package com.common.easton_portal.core;

import com.common.core.base.helper.ExceptionHelper;
import com.common.core.base.helper.StringHelper;
import com.common.core.base.log.Log;
import com.common.core.web.security.jwt.HttpJwtHelper;
import com.common.core.web.security.jwt.JwtParser;
import com.common.easton_portal.model.UserModel;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

public class AuthenticationTokenFilter extends OncePerRequestFilter {
    private final SignerProvider mSigner;

    public AuthenticationTokenFilter(SignerProvider signer) { mSigner = signer; }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            checkAuthentication(request);
        } catch (Exception ex) {
            Log.e("Cannot get user authentication: {0}", ExceptionHelper.asString(ex));
        }
        filterChain.doFilter(request, response);
    }

    private void checkAuthentication(HttpServletRequest request) {
        var token = HttpJwtHelper.getToken(new HttpJwtHelper.RequestService() {
            @Override
            public String header(String key) {
                return request.getHeader(key);
            }
            @Override
            public String cookie(String key) {
                var cookies = request.getCookies();
                return Arrays.stream(cookies == null ? new Cookie[0] : cookies)
                        .filter(s -> key.equals(s.getName())).findFirst().map(Cookie::getValue).orElse(null);
            }
        });
        if (StringHelper.isNullOrEmpty(token)) return;
        var uncheckedData = JwtParser.deserialize(token);

        if (HttpJwtHelper.K_AUDIENCE.equals(uncheckedData.audience)) {
            var data = HttpJwtHelper.valid(token, mSigner.getReadSigners());
            if (data == null) return;

            var authUser = new UserModel(Long.parseLong(data.subject), data.claimsLoader);

            var auth = new UsernamePasswordAuthenticationToken(authUser, null, authUser.getAuthorities());
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
    }
}
