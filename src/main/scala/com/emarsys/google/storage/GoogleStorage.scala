package com.emarsys.google.storage

import java.net.URL

import akka.actor.ActorSystem
import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.google.cloud.ReadChannel
import com.google.cloud.storage.Blob

import scala.concurrent.duration.FiniteDuration
import scala.util.{Failure, Success, Try}

trait GoogleStorage {

  def storageSource(
      fileName: String,
      chunkSize: Int = 0
  )(implicit actorSystem: ActorSystem): Source[ByteString, _] = {
    val project = Config.default.google.storage.project
    storageSource(fileName, project.name, project.bucket, chunkSize)
  }

  def storageSource(fileName: String, project: String, bucket: String, chunkSize: Int)(
      implicit actorSystem: ActorSystem
  ): Source[ByteString, _] = {
    val chunkKbSize = getValidChunkSize(chunkSize)
    createChannel(project, bucket, fileName) match {
      case Success(channel) => Source.fromGraph(GoogleStorageGraphStage(channel, chunkKbSize))
      case Failure(error) =>
        actorSystem.log.error("An exception occured during creating channel, message {} ", error.getStackTrace)
        Source.empty
    }
  }

  def getValidChunkSize(chunkSize: Int): Int = {
    Seq(chunkSize, Config.default.google.storage.chunkSize).find(_ > 0).getOrElse(64)
  }

  def checkFile(fileName: String): Boolean = {
    getBlob(fileName, Config.default) != null
  }

  def signedUrlFor(fileName: String, duration: FiniteDuration): Option[URL] = {
    Option(getBlob(fileName, Config.default)).map(_.signUrl(duration.length, duration.unit))
  }

  private def getBlob(fileName: String, config: Config): Blob = {
    val project = config.google.storage.project
    GoogleStorageService(project.name, config)
      .get(project.bucket)
      .get(fileName)
  }

  private def createChannel(project: String, bucket: String, fileName: String): Try[ReadChannel] =
    Try(GoogleStorageService(project, Config.default).reader(bucket, fileName))

}

object GoogleStorage extends GoogleStorage
