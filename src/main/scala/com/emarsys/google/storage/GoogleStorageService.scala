package com.emarsys.google.storage

import akka.actor.ActorSystem
import com.google.cloud.http.HttpTransportOptions
import com.google.cloud.storage.StorageOptions


object GoogleStorageService {

  def apply(project: String)(implicit system: ActorSystem) = {
    StorageOptions.newBuilder()
      .setTransportOptions(createTransportOptions())
      .setCredentials(DefaultConfig(system).credentials)
      .setProjectId(project)
      .build()
      .getService
  }

  private def createTransportOptions()(implicit system: ActorSystem): HttpTransportOptions = {
    val config = DefaultConfig(system)
    HttpTransportOptions.newBuilder().setHttpTransportFactory(config.httpTransportFactory).build()
  }

}
