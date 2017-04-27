package com.emarsys.google.storage

import java.io.ByteArrayInputStream

import akka.actor.ActorSystem
import akka.event.Logging
import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.google.api.services.storage.StorageScopes
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.ReadChannel
import com.google.cloud.storage.{Bucket, Storage, StorageOptions}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

object GoogleStorage {

  def apply(s: ActorSystem) = {
    new GoogleStorage(s)
  }

}

class GoogleStorage(system: ActorSystem) {

  private val config = DefaultConfig(system)

  private lazy val  storage : String => Storage = project => StorageOptions.newBuilder()
    .setCredentials(GoogleCredentials.fromStream(new ByteArrayInputStream(config.configAsJson("secret").getBytes)).createScoped(StorageScopes.all())).setProjectId(project)
    .build()
    .getService();


  def storageSource(fileName: String, chunkSize: Int = 64)(implicit ec: ExecutionContext) : Source[ByteString, _] =  {
    createChannel(config.configOfProject("name"), config.configOfProject("bucket"), fileName) match {
      case Success(channel) => Source.fromGraph(GoogleStorageGraphStage(channel, chunkSize))
      case Failure(error) => system.log.error("An exception occured during creating channel, message {} ", error.getStackTrace)
                         Source.empty
    }

  }

  private def createChannel(project: String, bucket: String, fileName: String) : Try[ReadChannel] = Try(storage(project).reader(bucket, fileName))


}
