package com.takirahal.srfgroup.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Component
public class RequestUtil {

    public static String getHeaderAttribute(String attributeName) {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        String attrValue = "";
        if (attributes != null) {
            HttpServletRequest request = ((ServletRequestAttributes) attributes).getRequest();
            attrValue = request.getHeader(attributeName);
        }
        return attrValue.isEmpty() ? "" : attrValue;
    }
}
