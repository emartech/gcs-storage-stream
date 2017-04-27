package com.emarsys.google.storage

import java.nio.ByteBuffer

import akka.stream.{Attributes, Outlet, SourceShape}
import akka.stream.stage.{GraphStage, GraphStageLogic, OutHandler}
import akka.util.ByteString
import com.google.cloud.ReadChannel

object GoogleStorageGraphStage {

  def apply(storageChannel: ReadChannel, chunkKByteSize : Int = 64): GoogleStorageGraphStage = new GoogleStorageGraphStage(storageChannel, chunkKByteSize)

}

class GoogleStorageGraphStage(storageChannel: ReadChannel, chunkKByteSize : Int = 64) extends GraphStage[SourceShape[ByteString]] {

  val out: Outlet[ByteString] = Outlet("GoogleStorageSource")

  @scala.throws[Exception](classOf[Exception])
  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = new GraphStageLogic(shape) {
    
    val bytes = ByteBuffer.allocate(chunkKByteSize * 1024);
    
    setHandler(out, new OutHandler {

      override def onPull(): Unit = {
          if (storageChannel.read(bytes) > 0) {
            bytes.flip();
            push(out, ByteString(bytes))
          } else {
            completeStage()
          }
        bytes.clear();
      }
    })
  }

  override def shape: SourceShape[ByteString] = SourceShape(out)
}
