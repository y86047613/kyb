package com.zy.kyb.controller;

import com.google.common.collect.Maps;
import com.zy.kyb.excepiton.BasicException;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

@RequestMapping("/error")
public class CustomErrorController extends AbstractErrorController {

    private ErrorAttributes errorAttributes;

    private boolean includeStackTrace;

    public CustomErrorController(ErrorAttributes errorAttributes, boolean includeStackTrace) {
        super(errorAttributes);
        this.errorAttributes = errorAttributes;
        this.includeStackTrace = includeStackTrace;
    }

    @RequestMapping
    @ResponseBody
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request, HttpServletResponse response){
        WebRequest webRequest = new ServletWebRequest(request);
        Map<String, Object> resultMap = Maps.newHashMap();
        Throwable error = errorAttributes.getError(webRequest);
        if( error instanceof BasicException) {
            BasicException exception = (BasicException) error;
            resultMap.put("timestamp", new Date());
            resultMap.put("status", exception.getStatus());
            resultMap.put("error", exception.getType());
            resultMap.put("message", exception.getMessage());
            resultMap.put("path", getPath(webRequest));
        }else {
           resultMap = getErrorAttributes(request, includeStackTrace);
        }
        return new ResponseEntity(resultMap, getStatus(request));
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }

    private String getPath(RequestAttributes requestAttributes) {
        String path = getAttribute(requestAttributes, "javax.servlet.error.request_uri");
        if (path != null) {
            return path;
        }
        return "";
    }

    @SuppressWarnings("unchecked")
    private <T> T getAttribute(RequestAttributes requestAttributes, String name) {
        return (T) requestAttributes.getAttribute(name, RequestAttributes.SCOPE_REQUEST);
    }
}

