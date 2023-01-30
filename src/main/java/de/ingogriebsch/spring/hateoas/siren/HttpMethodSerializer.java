package de.ingogriebsch.spring.hateoas.siren;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.http.HttpMethod;

public class HttpMethodSerializer extends JsonSerializer<HttpMethod> {

    @Override
    public void serialize(HttpMethod value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value.name());
    }

}
