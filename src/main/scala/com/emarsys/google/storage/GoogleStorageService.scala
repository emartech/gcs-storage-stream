package com.emarsys.google.storage

import com.google.cloud.http.HttpTransportOptions
import com.google.cloud.storage.StorageOptions

object GoogleStorageService {

  def apply(project: String, config: Config) = {
    if (config.google.useWorkloadIdentityAuth) {
      StorageOptions
        .newBuilder()
        .setTransportOptions(createTransportOptions(config))
        .setProjectId(project)
        .build()
        .getService
    } else {
      StorageOptions
        .newBuilder()
        .setTransportOptions(createTransportOptions(config))
        .setCredentials(config.credentials)
        .setProjectId(project)
        .build()
        .getService
    }
  }

  private def createTransportOptions(config: Config): HttpTransportOptions = {
    HttpTransportOptions.newBuilder().setHttpTransportFactory(config.httpTransportFactory).build()
  }

}
