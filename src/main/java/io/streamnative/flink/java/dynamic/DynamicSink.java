package io.streamnative.flink.java.dynamic;

import io.streamnative.flink.java.config.ApplicationConfigs;
import io.streamnative.flink.java.generator.RandomLoadEventGenerator;
import io.streamnative.flink.java.models.LoadEvent;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

import java.util.HashMap;
import java.util.Map;

import static io.streamnative.flink.java.config.ApplicationConfigs.loadConfig;
import static org.apache.pulsar.client.api.Schema.AVRO;

/**
 * This sink will use Pulsar's {@code Schema.AVRO} to serialize the messages into a topic.
 * The read serializer is {@code Schema.AUTO}, Pulsar only supports avro and json in such way.
 */
public class DynamicSink {

    private static final Map<Class<?>, Producer<?>> producers = new HashMap<>();

    public static void main(String[] args) throws Exception {
        // Load application configs.
        ApplicationConfigs configs = loadConfig(args);

        // Create a fake source.
        RandomLoadEventGenerator generator = new RandomLoadEventGenerator();
        generator.open(null);

        // Create Pulsar Producer.
        PulsarClient client = PulsarClient.builder().serviceUrl(configs.serviceUrl()).build();

        while (true) {
            LoadEvent event = generator.generate();
            Producer producer = getProducer(client, event);
            producer.newMessage().value(event).send();
        }
    }

    private static Producer<?> getProducer(PulsarClient client, LoadEvent event) throws PulsarClientException {
        Class<?> clazz = event.getClass();
        Producer<?> producer = producers.get(clazz);

        if (producer == null) {
            producer = client.newProducer(AVRO(clazz))
                    .topic("persistent://sample/flink/dynamic-load-event")
                    .create();
            producers.put(clazz, producer);
        }

        return producer;
    }
}
