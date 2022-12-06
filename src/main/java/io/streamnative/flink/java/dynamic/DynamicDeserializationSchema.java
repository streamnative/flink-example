package io.streamnative.flink.java.dynamic;

import io.streamnative.flink.java.models.LoadCreatedEvent;
import org.apache.flink.api.common.serialization.DeserializationSchema.InitializationContext;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.connector.pulsar.source.config.SourceConfiguration;
import org.apache.flink.connector.pulsar.source.enumerator.topic.TopicNameUtils;
import org.apache.flink.connector.pulsar.source.reader.deserializer.PulsarDeserializationSchema;
import org.apache.flink.util.Collector;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.impl.PulsarClientImpl;
import org.apache.pulsar.client.impl.schema.AutoConsumeSchema;
import org.apache.pulsar.client.impl.schema.generic.MultiVersionSchemaInfoProvider;
import org.apache.pulsar.common.naming.TopicName;
import org.apache.pulsar.shade.org.apache.avro.generic.GenericRecord;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.apache.flink.connector.pulsar.common.config.PulsarClientFactory.createClient;

/**
 * Query the schema by schema version. This should only be applied to a topic with {@code Schema.AVRO} producer.
 */
public class DynamicDeserializationSchema implements PulsarDeserializationSchema<LoadCreatedEvent> {
    private static final long serialVersionUID = 3320218454364912622L;

    private transient PulsarClientImpl client;

    // No need to use the concurrent hash map, flink is thread safe here.
    private transient AutoConsumeSchema schema;

    @Override
    @SuppressWarnings("unchecked")
    public void deserialize(Message<byte[]> message, Collector<LoadCreatedEvent> collector) {
        GenericRecord record = decode(message);

        // Convert this GenericRecord to your class instance.
        // I only add a quite simple demo here which shouldn't be used in the production code.
        if (record.hasField("createdAction")) {
            String action = record.get("createdAction").toString();
            List<String> messages = ((List<?>) record.get("messages")).stream().map(Object::toString).collect(toList());
            collector.collect(new LoadCreatedEvent().setCreatedAction(action).setMessages(messages));
        }
    }

    private GenericRecord decode(Message<byte[]> message) {
        if (schema == null) {
            this.schema = new AutoConsumeSchema();

            // Set schema info provider.
            String topicName = TopicNameUtils.topicName(message.getTopicName());
            schema.setSchemaInfoProvider(new MultiVersionSchemaInfoProvider(TopicName.get(topicName), client));
        }

        return (GenericRecord) schema.decode(message.getData(), message.getSchemaVersion()).getNativeObject();
    }

    @Override
    public TypeInformation<LoadCreatedEvent> getProducedType() {
        return Types.POJO(LoadCreatedEvent.class);
    }

    @Override
    public void open(InitializationContext context, SourceConfiguration configuration) {
        this.client = (PulsarClientImpl) createClient(configuration);
    }
}
