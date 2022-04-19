package io.streamnative.flink.scala

import io.streamnative.flink.java.common.EnvironmentUtils.createEnvironment
import io.streamnative.flink.java.config.ApplicationConfigs.loadConfig
import io.streamnative.flink.scala.avro.Message
import org.apache.flink.api.common.eventtime.WatermarkStrategy.forBoundedOutOfOrderness
import org.apache.flink.configuration.Configuration.fromMap
import org.apache.flink.connector.pulsar.source.PulsarSource
import org.apache.flink.connector.pulsar.source.enumerator.cursor.{
  StartCursor,
  StopCursor
}
import org.apache.flink.connector.pulsar.source.reader.deserializer.PulsarDeserializationSchema.pulsarSchema
import org.apache.flink.streaming.api.scala._
import org.apache.pulsar.client.api.{Schema, SubscriptionType}

import java.time.Duration.ofMinutes

/**
  * The application for scala based flink project. Consuming avro messages from Pulsar.
  */
object AvroSource extends App {
  // Load application configs.
  val configs = loadConfig

  // Create execution environment
  val env = new StreamExecutionEnvironment(createEnvironment(configs))

  // Create sink schema.
  val avroSchema = Schema.AVRO(classOf[Message])
  val sourceSchema = pulsarSchema(avroSchema, classOf[Message])

  // Create a Pulsar source, it would consume messages from Pulsar on "persistent://sample/flink/scala-avro" topic.
  val pulsarSource = PulsarSource
    .builder[Message]
    .setServiceUrl(configs.serviceUrl)
    .setAdminUrl(configs.adminUrl)
    .setStartCursor(StartCursor.earliest)
    .setUnboundedStopCursor(StopCursor.never)
    .setTopics("persistent://sample/flink/scala-avro")
    .setDeserializationSchema(sourceSchema)
    .setSubscriptionName("flink-source")
    .setConsumerName("flink-source-%s")
    .setSubscriptionType(SubscriptionType.Shared)
    .setConfig(fromMap(configs.sourceConfigs))
    .build

  // Pulsar Source don't require extra TypeInformation be provided.
  env
    .fromSource(
      pulsarSource,
      forBoundedOutOfOrderness(ofMinutes(5)),
      "pulsar-source"
    )
    .print

  env.execute("Simple Pulsar Source")

}
