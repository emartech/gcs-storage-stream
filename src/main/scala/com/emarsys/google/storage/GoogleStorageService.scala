package com.emarsys.google.storage

import java.io.ByteArrayInputStream

import akka.actor.ActorSystem
import com.google.api.services.storage.StorageScopes
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.storage.StorageOptions


object GoogleStorageService {

  def apply(project: String)(implicit system: ActorSystem) = {
    StorageOptions.newBuilder()
      .setCredentials(DefaultConfig(system).credentials).setProjectId(project)
      .build()
      .getService
  }

}
