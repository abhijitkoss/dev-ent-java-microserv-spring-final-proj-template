package com.hinkmond.finalproj;
import org.springframework.http.HttpStatus;


public class HttpStatusPlusInfo {
    private HttpStatus httpStatus;
    private String info;

    public HttpStatusPlusInfo(HttpStatus httpStatus, String info) {
        this.httpStatus = httpStatus;
        this.info = info;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
