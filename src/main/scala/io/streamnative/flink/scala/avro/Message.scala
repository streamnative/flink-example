package io.streamnative.flink.scala.avro

/**
  * Scala case class as the avro message.
  */
case class Message(var id: Long, var payload: String, var eventTime: Long) {

  // Consuming from pulsar would require a no-args constructor.
  def this() = this(0, "", 0)
}
