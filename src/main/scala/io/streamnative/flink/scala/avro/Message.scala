package io.streamnative.flink.scala.avro

/**
  * Scala case class as the avro message.
  */
case class Message(var id: Long, var payload: String, var eventTime: Long) {
  def this() = this(0, "", 0)
}
