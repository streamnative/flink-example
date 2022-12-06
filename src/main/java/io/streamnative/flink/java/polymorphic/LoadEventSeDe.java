package io.streamnative.flink.java.polymorphic;

import io.streamnative.flink.java.generator.RandomLoadEventGenerator;
import io.streamnative.flink.java.models.LoadEvent;
import org.apache.pulsar.common.util.ObjectMapperFactory;
import org.apache.pulsar.shade.com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.pulsar.shade.com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This is an example on how to serialize/deserializer the load event interface with jackson.
 */
public class LoadEventSeDe {

    private static final ObjectMapper mapper = ObjectMapperFactory.create();

    public static void main(String[] args) throws JsonProcessingException {
        RandomLoadEventGenerator generator = new RandomLoadEventGenerator();
        generator.open(null);

        while (true) {
            LoadEvent event = generator.generate();
            System.out.println("The event type is: " + event.getClass().getName());

            // Serialize it into JSON.
            String json = mapper.writeValueAsString(event);
            System.out.println("Serialize into JSON: "+ json);

            // Deserializer the json into interface.
            LoadEvent value = mapper.readValue(json, LoadEvent.class);
            System.out.println("Deserailize the json, class type: " + value.getClass().getName() + ", content: " + value);
        }
    }
}
