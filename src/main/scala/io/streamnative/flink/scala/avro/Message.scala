package io.streamnative.flink.scala.avro

/**
  * Scala case class as the avro message.
  */
case class Message(id: Long, payload: String, eventTime: Long)
