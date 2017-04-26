package com.emarsys.google.storage.example

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Framing}
import akka.util.ByteString
import com.emarsys.google.storage.GoogleStorage

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._


object GoogleStorageReaderExample extends App {

  implicit val system       = ActorSystem("gc-example")
  implicit val materializer = ActorMaterializer()


  lazy val csvLines =
    Flow[ByteString]
      .via(Framing.delimiter(ByteString("\n"), maximumFrameLength = 25))
      .groupedWithin(1000, 1 seconds)
      .map(_.map(_.utf8String).mkString(","))


  system.log.info("Start streaming file...")

  GoogleStorage(system).storageSource("ids_only.csv").via(csvLines).runForeach(println)




}
