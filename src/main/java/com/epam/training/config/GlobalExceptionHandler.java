package com.epam.training.config;

import com.epam.training.exception.BadRequest;
import com.epam.training.exception.FileDuplicateException;
import com.epam.training.exception.UserNotFoundException;
import org.apache.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private final static Logger logger = Logger.getLogger(GlobalExceptionHandler.class);

    private Map<Class<?>, HttpStatus> exceptionStatus;

    public GlobalExceptionHandler() {
        exceptionStatus = new HashMap<Class<?>, HttpStatus>();
        exceptionStatus.put(FileNotFoundException.class, HttpStatus.NOT_FOUND);
        exceptionStatus.put(BadRequest.class, HttpStatus.BAD_REQUEST);
        exceptionStatus.put(UserNotFoundException.class, HttpStatus.NOT_FOUND);
        exceptionStatus.put(FileDuplicateException.class, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = Exception.class)
    protected ResponseEntity handleCustomException(Exception ex, HttpServletRequest request) {
        logger.error("Unhandled Exception servicing " + request.getRequestURL(), ex);
        HttpStatus status = exceptionStatus.get(ex.getClass());
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        ExceptionResponse response = createResponse(ex.getMessage(),
                status,
                request.getMethod(),
                request.getRequestURI());
        return handleExceptionInternal(ex, response, new HttpHeaders(),
                status, new ServletWebRequest(request));
    }

    private ExceptionResponse createResponse(String message, HttpStatus httpStatus,
                                             String httpMethod, String callingUrl) {
        ExceptionResponse response = new ExceptionResponse();
        response.setDeveloperMessage(message);
        response.setHttpStatusCode(String.valueOf(httpStatus.value()));
        response.setUrl(callingUrl);
        response.setMethod(httpMethod);
        return response;
    }

    public static class ExceptionResponse {
        private String httpStatusCode;
        private String developerMessage;
        private String url;
        private String method;
        private String moreInfo;

        public String getHttpStatusCode() {
            return httpStatusCode;
        }
        public void setHttpStatusCode(String httpStatusCode) {
            this.httpStatusCode = httpStatusCode;
        }

        public String getDeveloperMessage() {
            return developerMessage;
        }
        public void setDeveloperMessage(String developerMessage) {
            this.developerMessage = developerMessage;
        }

        public String getUrl() {
            return url;
        }
        public void setUrl(String url) {
            this.url = url;
        }

        public String getMethod() {
            return method;
        }
        public void setMethod(String method) {
            this.method = method;
        }

        public String getMoreInfo() {
            return moreInfo;
        }
        public void setMoreInfo(String moreInfo) {
            this.moreInfo = moreInfo;
        }
    }
}
