/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.StringWriter;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;

/**
 *
 * @author michal
 */
public class JsonUtils {

    public static String asJsonString(JsonArrayBuilder jsonBuilder) {
        StringWriter stWriter = new StringWriter();
        try (JsonWriter jsonWriter = Json.createWriter(stWriter)) {
            jsonWriter.writeArray(jsonBuilder.build());
        }
        return stWriter.toString();
    }

    public static String asJsonString(JsonObjectBuilder jsonBuilder) {
        StringWriter stWriter = new StringWriter();
        try (JsonWriter jsonWriter = Json.createWriter(stWriter)) {
            jsonWriter.writeObject(jsonBuilder.build());
        }
        return stWriter.toString();
    }
}
