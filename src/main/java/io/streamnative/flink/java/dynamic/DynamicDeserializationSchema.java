package io.streamnative.flink.java.dynamic;

import io.streamnative.flink.java.models.LoadCreatedEvent;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.connector.pulsar.source.reader.deserializer.PulsarDeserializationSchema;
import org.apache.flink.util.Collector;
import org.apache.pulsar.client.api.Message;

/**
 * Query the schema by schema version.
 */
public class DynamicDeserializationSchema implements PulsarDeserializationSchema<LoadCreatedEvent> {
    private static final long serialVersionUID = 3320218454364912622L;

    @Override
    public void deserialize(Message<byte[]> message, Collector<LoadCreatedEvent> collector) throws Exception {

    }

    @Override
    public TypeInformation<LoadCreatedEvent> getProducedType() {
        return Types.POJO(LoadCreatedEvent.class);
    }
}
