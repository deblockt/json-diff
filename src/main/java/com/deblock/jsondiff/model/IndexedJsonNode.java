package com.deblock.jsondiff.model;

import com.fasterxml.jackson.databind.JsonNode;

public class IndexedJsonNode {
    int index;
    JsonNode jsonNode;

    public IndexedJsonNode(int index, JsonNode jsonNode) {
        this.index = index;
        this.jsonNode = jsonNode;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public JsonNode getJsonNode() {
        return jsonNode;
    }

    public void setJsonNode(JsonNode jsonNode) {
        this.jsonNode = jsonNode;
    }
}
