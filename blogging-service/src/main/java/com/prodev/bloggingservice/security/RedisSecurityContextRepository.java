package com.prodev.bloggingservice.security;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SaveContextOnUpdateOrErrorResponseWrapper;
import org.springframework.security.web.context.SecurityContextRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

public class RedisSecurityContextRepository implements SecurityContextRepository {

    public static final String SPRING_SECURITY_CONTEXT_KEY = "SPRING_SECURITY_CONTEXT";
    public static final String SECURITY_CONTEXT_ID = "securityContextId";

    protected final Log logger = LogFactory.getLog(this.getClass());

    private SecurityContextDao securityContextDao;

    private Object contextObject = SecurityContextHolder.createEmptyContext();
    private boolean allowSessionCreation = true;
    private boolean disableUrlRewriting = false;

    private AuthenticationTrustResolver authenticationTrustResolver = new AuthenticationTrustResolverImpl();

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        HttpServletRequest request = requestResponseHolder.getRequest();
        HttpServletResponse response = requestResponseHolder.getResponse();
        HttpSession httpSession = request.getSession(false);

        String securityContextId = readSecurityContextId(request);
        SecurityContext context = readSecurityContextFromRedis(securityContextId);

        if (context == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("No Security Context was available. A new one will be created.");
            }
            context = SecurityContextHolder.createEmptyContext();
        }

        requestResponseHolder.setResponse(new SaveToSessionResponseWrapper(response, request,
                httpSession != null, context.hashCode()));

        return context;
    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
        return readSecurityContextFromRedis(request) != null;
    }

    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request,
                            HttpServletResponse response) {
        SaveToSessionResponseWrapper responseWrapper = (SaveToSessionResponseWrapper) response;
        if (!responseWrapper.isContextSaved()) {
            responseWrapper.saveContext(context);
        }
    }

    private SecurityContext readSecurityContextFromRedis(HttpServletRequest request) {
        String securityContextId = readSecurityContextId(request);
        return readSecurityContextFromRedis(securityContextId);
    }

    private SecurityContext readSecurityContextFromRedis(String securityContextId) {
        if (StringUtils.isNotEmpty(securityContextId)) {
            return securityContextDao.getSecurityContext(securityContextId);
        }
        return null;
    }

    private String readSecurityContextId(HttpServletRequest request) {
        String securityContextId = request.getParameter(SECURITY_CONTEXT_ID);

        if (StringUtils.isNotEmpty(securityContextId)) {
            HttpSession httpSession = request.getSession(false);

            if (httpSession != null) {
                securityContextId = (String) httpSession.getAttribute(SECURITY_CONTEXT_ID);
            }
        }

        return securityContextId;
    }

    public void setSecurityContextDao(SecurityContextDao securityContextDao) {
        this.securityContextDao = securityContextDao;
    }

    public void setAllowSessionCreation(boolean allowSessionCreation) {
        this.allowSessionCreation = allowSessionCreation;
    }

    public void setDisableUrlRewriting(boolean disableUrlRewriting) {
        this.disableUrlRewriting = disableUrlRewriting;
    }

    private void removeSecurityContext(HttpSession httpSession, String securityContextId) {
        if (httpSession != null) {
            httpSession.removeAttribute(SECURITY_CONTEXT_ID);
        }
        if (securityContextId != null) {
            securityContextDao.deleteSecurityContext(securityContextId);
        }
    }

    void removeSecurityContext(HttpServletRequest request) {
        String securityContextId = readSecurityContextId(request);
        removeSecurityContext(request.getSession(false), securityContextId);
    }

    final class SaveToSessionResponseWrapper extends SaveContextOnUpdateOrErrorResponseWrapper {

        private HttpServletRequest request;

        private boolean httpSessionExistedAtStartOfRequest;
        private int contextHashBeforeChainExecution;

        SaveToSessionResponseWrapper(HttpServletResponse response, HttpServletRequest request,
                                     boolean httpSessionExistedAtStartOfRequest,
                                     int contextHashBeforeChainExecution) {
            super(response, disableUrlRewriting);
            this.request = request;
            this.httpSessionExistedAtStartOfRequest = httpSessionExistedAtStartOfRequest;
            this.contextHashBeforeChainExecution = contextHashBeforeChainExecution;
        }

        @Override
        protected void saveContext(SecurityContext context) {
            final Authentication authentication = context.getAuthentication();
            HttpSession httpSession = request.getSession(false);
            String securityContextId = readSecurityContextId(request);

            if (authentication == null || authenticationTrustResolver.isAnonymous(authentication)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("SecurityContext is empty or anonymous - context will not be stored. ");
                }
                removeSecurityContext(httpSession, securityContextId);
                return;
            }

            if (httpSession == null) {
                httpSession = createNewSessionIfAllowed(context);
            }

            if (httpSession != null) {
                if ((context.hashCode() != contextHashBeforeChainExecution ||
                        httpSession.getAttribute(SECURITY_CONTEXT_ID) == null)) {
                    if (StringUtils.isNotEmpty(securityContextId)) {
                        securityContextId = UUID.randomUUID().toString();
                    }
                    httpSession.setAttribute(SECURITY_CONTEXT_ID, securityContextId);
                    securityContextDao.saveSecurityContext(securityContextId, context);
                    if (logger.isDebugEnabled()) {
                        logger.debug("SecurityContext stored: '" + context + "'");
                    }
                }
                long expireTime = httpSession.getLastAccessedTime() + httpSession.getMaxInactiveInterval() * 1000;
                securityContextDao.setExpireTime(securityContextId, expireTime);
            }
        }

        private HttpSession createNewSessionIfAllowed(SecurityContext context) {
            if (httpSessionExistedAtStartOfRequest) {
                if (logger.isDebugEnabled()) {
                    logger.debug("HttpSession is now null, but was not null at start of request; "
                            + "session was invalidated, so do not create a new session");
                }
                return null;
            }

            if (!allowSessionCreation) {
                if (logger.isDebugEnabled()) {
                    logger.debug("The HttpSession is currently null, and the "
                            + HttpSessionSecurityContextRepository.class.getSimpleName()
                            + " is prohibited from creating an HttpSession "
                            + "(because the allowSessionCreation property is false) - SecurityContext thus not "
                            + "stored for next request");
                }

                return null;
            }
            if (contextObject.equals(context)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("HttpSession is null, but SecurityContext has not changed from default empty context: ' "
                            + context
                            + "'; not creating HttpSession or storing SecurityContext");
                }
                return null;
            }
            if (logger.isDebugEnabled()) {
                logger.debug("HttpSession being created as SecurityContext is non-default");
            }
            try {
                return request.getSession(true);
            } catch (IllegalStateException e) {
                logger.warn("Failed to create a session, as response has been committed. Unable to store" +
                        " SecurityContext.");
            }
            return null;
        }

    }


}
