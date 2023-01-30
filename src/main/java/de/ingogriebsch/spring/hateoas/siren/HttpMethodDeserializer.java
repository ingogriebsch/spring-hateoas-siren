package de.ingogriebsch.spring.hateoas.siren;

import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.http.HttpMethod;

public class HttpMethodDeserializer extends JsonDeserializer<HttpMethod> {

    @Override
    public HttpMethod deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        return HttpMethod.valueOf(p.getValueAsString());
    }

}
