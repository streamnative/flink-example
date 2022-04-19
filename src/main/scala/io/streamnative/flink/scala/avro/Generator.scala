package io.streamnative.flink.scala.avro

import io.streamnative.flink.java.common.InfiniteSourceFunction
import net.datafaker.Faker

import java.util.Random

/**
  * Generating avro messages.
  */
@SerialVersionUID(-1)
class Generator() extends InfiniteSourceFunction.InfiniteGenerator[Message] {

  @transient private lazy val faker = new Faker(new Random())

  /**
    * Generate a record.
    */
  override def generate(): Message =
    Message(
      faker.number().randomNumber(),
      faker.programmingLanguage().name(),
      faker.date().birthday().getTime
    )
}
