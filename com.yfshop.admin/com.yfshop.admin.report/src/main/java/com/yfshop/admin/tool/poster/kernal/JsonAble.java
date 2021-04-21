package com.yfshop.admin.tool.poster.kernal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yfshop.admin.tool.poster.contracts.Jsonable;

import java.io.IOException;

public abstract class JsonAble implements Jsonable {

    @Override
    public String toString() {
        String str = this.toJson();
        return str != null ? str : super.toString();
    }

    @Override
    public String toJson() {
        try {
            return Jsonable.encode(this);
        } catch (JsonProcessingException e) {
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
