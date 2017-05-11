package com.emarsys.google.storage

import akka.actor.ActorSystem
import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.google.cloud.ReadChannel

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}
import scala.concurrent.JavaConversions._



trait GoogleStorage {

  def storageSource(fileName: String, chunkSize: Int = 0)(implicit ec: ExecutionContext, actorSystem: ActorSystem) : Source[ByteString, _] =  {
    val chunkKbSize = getValidChunkSize(chunkSize)
    createChannel(getConfigOfProject("name"), getConfigOfProject("bucket"), fileName) match {
      case Success(channel) => Source.fromGraph(GoogleStorageGraphStage(channel, chunkKbSize))
      case Failure(error) => actorSystem.log.error("An exception occured during creating channel, message {} ", error.getStackTrace)
                         Source.empty
    }
  }

  def getValidChunkSize(chunkSize: Int)(implicit actorSystem: ActorSystem) = {
    Seq(chunkSize, getConfigValue("chunk-size", "0").toInt).find(_ > 0).getOrElse(64)
  }

  def checkFile(fileName: String)(implicit ec: ExecutionContext, actorSystem: ActorSystem): Boolean = {
    GoogleStorageService(getConfigOfProject("name")).get(getConfigOfProject("bucket")).get(fileName) != null
  }

  private def getConfigValue(key: String, default: String)(implicit actorSystem: ActorSystem) = {
    DefaultConfig(actorSystem).configValue(key, default)
  }

  private def getConfigOfProject(key: String)(implicit actorSystem: ActorSystem) = {
    DefaultConfig(actorSystem).configOfProject(key)
  }

  private def createChannel(project: String, bucket: String, fileName: String)(implicit actorSystem: ActorSystem) : Try[ReadChannel] = Try( GoogleStorageService(project).reader(bucket, fileName))

}

object GoogleStorage extends GoogleStorage