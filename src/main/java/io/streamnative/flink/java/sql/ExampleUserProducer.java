package io.streamnative.flink.java.sql;

import io.streamnative.flink.java.config.ApplicationConfigs;
import io.streamnative.flink.java.sql.models.ExampleUser;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.impl.schema.JSONSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static io.streamnative.flink.java.config.ApplicationConfigs.loadConfig;
import static io.streamnative.flink.java.sql.models.ExampleUser.createRandomUserWithCreateTime;

/**
 * Producer application that writes data to a Pulsar topic.
 * SQL examples will then query the data from the same topic.
 */
public final class ExampleUserProducer {
    private static final Logger logger
        = LoggerFactory.getLogger(ExampleUserProducer.class);
    private static final String USER_COMPLETE_TOPIC_PATH = "sample/flink/user";
    public static void main(String[] args) throws PulsarClientException, InterruptedException {
        ApplicationConfigs configs = loadConfig();

        PulsarClient client = PulsarClient.builder()
                .serviceUrl(configs.serviceUrl())
                .build();

        // designate an AVRO schema
        Producer<ExampleUser> producer = client.newProducer(JSONSchema.of(ExampleUser.class))
                .topic(USER_COMPLETE_TOPIC_PATH)
                .create();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Closing Pulsar clients...");
            try {
                producer.close();
                client.close();
            } catch (PulsarClientException e) {
                e.printStackTrace();
            }
        }));

        while (true) {
            LocalDateTime currentDateTime = LocalDateTime.now();
            long epochMillis = currentDateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
            ExampleUser user = createRandomUserWithCreateTime(epochMillis);
            producer.newMessage()
                    .eventTime(epochMillis)
                    .value(user)
                    .send();

            Thread.sleep(100);
        }
    }
}
