package example;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.pulsar.FlinkPulsarSink;
import org.apache.flink.streaming.connectors.pulsar.FlinkPulsarSource;
import org.apache.flink.streaming.connectors.pulsar.internal.PulsarDeserializationSchema;
import org.apache.flink.streaming.connectors.pulsar.internal.PulsarDeserializationSchemaWrapper;
import org.apache.flink.streaming.connectors.pulsar.internal.PulsarOptions;
import org.apache.flink.streaming.connectors.pulsar.internal.PulsarSerializationSchema;
import org.apache.flink.streaming.connectors.pulsar.internal.PulsarSerializationSchemaWrapper;
import org.apache.flink.table.api.DataTypes;

import org.apache.pulsar.client.impl.auth.oauth2.AuthenticationOAuth2;

import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;

public class Stream {
    public static void main(String[] args) throws Exception{
        String serviceUrl = "your service url";
        String adminUrl = "your admin url";

        //auth by OAuth2, note that these parameters can be found in the StreamNative Cloud cluster sidebar -> Connect -> Clients.
        String issueUrl = "your issue url";
        String credentialsUrl = "your credentials url";
        String audience = "your audience";
        String url = "{\"type\":\"client_credentials\",\"privateKey\":\""+ credentialsUrl +"\",\"issuerUrl\":\"" + issueUrl + "\",\"audience\":\""+ audience +"\"}";
        String className = AuthenticationOAuth2.class.getName();

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        PulsarDeserializationSchema<String> sourceDeserializer = new PulsarDeserializationSchemaWrapper<>(new SimpleStringSchema());

        Properties sourceProperties = new Properties();
        sourceProperties.setProperty("topic", "public/default/source-topic");
        //set auth property
        sourceProperties.setProperty(PulsarOptions.AUTH_PARAMS_KEY, url);
        sourceProperties.setProperty(PulsarOptions.AUTH_PLUGIN_CLASSNAME_KEY, className);
        //create source
        FlinkPulsarSource<String> source = new FlinkPulsarSource(
                serviceUrl,
                adminUrl,
                sourceDeserializer,
                sourceProperties
        );

        source.setStartFromEarliest();

        DataStreamSource<String> dataStream = env.addSource(source);

        PulsarSerializationSchema<String> sinkSerializer = new PulsarSerializationSchemaWrapper.Builder(new SimpleStringSchema())
                .useAtomicMode(DataTypes.STRING())
                .build();

        Properties sinkProperties = new Properties();
        sinkProperties.setProperty(PulsarOptions.CLIENT_CACHE_SIZE_OPTION_KEY, "100");
        //set auth property
        sinkProperties.setProperty(PulsarOptions.AUTH_PARAMS_KEY, url);
        sinkProperties.setProperty(PulsarOptions.AUTH_PLUGIN_CLASSNAME_KEY, className);
        //create sink
        FlinkPulsarSink<String> sink = new FlinkPulsarSink<String>(
                serviceUrl,
                adminUrl,
                Optional.of("public/default/sink-topic"),
                sinkProperties,
                sinkSerializer
        );

        dataStream.addSink(sink);

        env.execute("aws example");
    }
}
