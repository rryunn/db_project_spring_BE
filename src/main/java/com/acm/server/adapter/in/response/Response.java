package com.acm.server.adapter.in.response;

public class Response {
    int status;
    String message;
    Object data;

    public Response() {}

    public Response(int status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public Response(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
