package com.emarsys.google.storage

import java.io.ByteArrayInputStream

import akka.actor.ActorSystem
import akka.event.Logging
import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.google.api.services.storage.StorageScopes
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.storage.{Bucket, Storage, StorageOptions}

import scala.concurrent.{ExecutionContext, Future}

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


  private lazy val connection : (String, String) => Bucket = (project, bucket) => {
    system.log.debug("Connect to {} bucket.", bucket)
    storage(project).get(bucket)
  }

  def storageSource(fileName: String)(implicit ec: ExecutionContext) : Source[ByteString, _] =  {
    Source.fromFuture(
      Future {
        readFiles(config.configOfProject("name"), fileName, config.configOfProject("bucket"))
      })
  }

  private def readFiles(project: String, fileName: String, bucketName: String)(implicit ec: ExecutionContext): ByteString =
    try {
      system.log.debug("Read {} file content.", fileName)
      ByteString.apply(connection(project, bucketName).get(fileName).getContent())
    } catch {
      case e: Throwable => {
        system.log.error(e, "An exception occurred, message: {} ", e.getMessage)
        ByteString.empty
      }
    }


}
