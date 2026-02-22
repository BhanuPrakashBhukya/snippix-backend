package com.meloffy.chat.dto;

import com.fasterxml.jackson.databind.JsonNode;

public class WsEnvelope {

    private String type;
    private JsonNode payload;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public JsonNode getPayload() {
        return payload;
    }

    public void setPayload(JsonNode payload) {
        this.payload = payload;
    }
}
