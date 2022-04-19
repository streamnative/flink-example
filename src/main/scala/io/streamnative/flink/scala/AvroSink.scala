package io.streamnative.flink.scala

import io.streamnative.flink.java.common.EnvironmentUtils.createEnvironment
import io.streamnative.flink.java.common.InfiniteSourceFunction
import io.streamnative.flink.java.config.ApplicationConfigs.loadConfig
import io.streamnative.flink.scala.avro.{Generator, Message}
import org.apache.flink.configuration.Configuration.fromMap
import org.apache.flink.connector.base.DeliveryGuarantee.EXACTLY_ONCE
import org.apache.flink.connector.pulsar.sink.PulsarSink
import org.apache.flink.connector.pulsar.sink.writer.serializer.PulsarSerializationSchema.pulsarSchema
import org.apache.flink.streaming.api.scala._
import org.apache.pulsar.client.api.Schema

import java.time.Duration.ofSeconds

/**
  * The application for scala based flink project. Writing avro messages into Pulsar.
  */
object AvroSink extends App {
  // Load application configs.
  val configs = loadConfig

  // Create scala based execution environment.
  val env = new StreamExecutionEnvironment(createEnvironment(configs))

  // Create avro source.
  val sourceFunction =
    new InfiniteSourceFunction[Message](new Generator(), ofSeconds(1))
  val source = env.addSource(sourceFunction)

  // Create sink schema.
  val avroSchema = Schema.AVRO(classOf[Message])
  val sinkSchema = pulsarSchema(avroSchema, classOf[Message])

  // Create avro sink.
  val sink = PulsarSink
    .builder()
    .setServiceUrl(configs.serviceUrl())
    .setAdminUrl(configs.adminUrl())
    .setTopics("persistent://sample/flink/scala-avro")
    .setProducerName("flink-sink-%s")
    .setSerializationSchema(sinkSchema)
    .enableSchemaEvolution()
    .setDeliveryGuarantee(EXACTLY_ONCE)
    .setConfig(fromMap(configs.sinkConfigs))
    .build()

  // Execute
  source.sinkTo(sink)
  env.execute("Scala Avro Sink")
}
