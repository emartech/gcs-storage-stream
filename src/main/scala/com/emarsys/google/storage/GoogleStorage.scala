package com.emarsys.google.storage

import akka.actor.ActorSystem
import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.google.cloud.ReadChannel
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success, Try}


trait GoogleStorage {

  def storageSource(fileName: String, chunkSize: Int = 64)(implicit ec: ExecutionContext, actorSystem: ActorSystem) : Source[ByteString, _] =  {
    val config = DefaultConfig(actorSystem)
    createChannel(config.configOfProject("name"), config.configOfProject("bucket"), fileName) match {
      case Success(channel) => Source.fromGraph(GoogleStorageGraphStage(channel, chunkSize))
      case Failure(error) => actorSystem.log.error("An exception occured during creating channel, message {} ", error.getStackTrace)
                         Source.empty
    }

  }

  private def createChannel(project: String, bucket: String, fileName: String)(implicit actorSystem: ActorSystem) : Try[ReadChannel] = Try( GoogleStorageService(project).reader(bucket, fileName))

}

object GoogleStorage extends GoogleStorage