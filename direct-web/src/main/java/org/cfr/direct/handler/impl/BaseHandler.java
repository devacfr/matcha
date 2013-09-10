package org.cfr.direct.handler.impl;

import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.cfr.direct.handler.IDirectHandler;
import org.cfr.direct.handler.context.IDirectHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.softwarementors.extjs.djn.router.RequestType;

public abstract class BaseHandler implements IDirectHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    /**
     * Must return a not null List of  {@link RequestType} accepted by the handler
     
     * @return
     */
    public abstract List<RequestType> getAcceptedRequestType();

    public boolean acceptRequest(RequestType type) {
        return getAcceptedRequestType().contains(type);
    }

    public final void process(IDirectHandlerContext handlerContext) {
        try {
            if (acceptRequest(handlerContext.getRequestType())) {
                doProcess(handlerContext);
            }
        } catch (Exception e) {
            LOGGER.error("Handler error during process", e);
            handleException(e, "Handler error during process");
        }
    }

    protected void handleException(Exception e, String message) {
        LOGGER.error(ExceptionUtils.getStackTrace(e));
        throw new DirectHandlerException(message, e);
    }

    protected abstract void doProcess(IDirectHandlerContext handlerContext) throws Exception;
}