package org.bitcoins.spvnode.networking

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKit, TestProbe}
import org.bitcoins.core.crypto.DoubleSha256Digest
import org.bitcoins.core.protocol.blockchain.BlockHeader
import org.bitcoins.core.util.{BitcoinSLogger, BitcoinSUtil}
import org.bitcoins.spvnode.messages.BlockMessage
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FlatSpecLike, MustMatchers}

import scala.concurrent.duration.DurationInt

/**
  * Created by chris on 7/10/16.
  */
class BlockActorTest extends TestKit(ActorSystem("BlockActorTest")) with FlatSpecLike
  with MustMatchers with ImplicitSender
  with BeforeAndAfter with BeforeAndAfterAll with BitcoinSLogger  {

  def blockActor = TestActorRef(BlockActor.props,self)
  val blockHash = DoubleSha256Digest(BitcoinSUtil.flipEndianness("00000000b873e79784647a6c82962c70d228557d24a747ea4d1b8bbe878e1206"))

  "BlockActor" must "be able to send a GetBlocksMessage then receive that block back" in {
    blockActor ! blockHash
    val blockMsg = expectMsgType[BlockMessage](10.seconds)
    blockMsg.block.blockHeader.hash must be (blockHash)

  }


  it must "be able to request a block from it's block header" in {
    val blockHeader = BlockHeader("0100000043497fd7f826957108f4a30fd9cec3aeba79972084e90ead01ea330900000000bac8b0fa927c0ac8234287e33c5f74d38d354820e24756ad709d7038fc5f31f020e7494dffff001d03e4b672")
    blockActor ! blockHeader
    val blockMsg = expectMsgType[BlockMessage](10.seconds)
    blockMsg.block.blockHeader.hash must be (blockHash)
  }


  override def afterAll = {
    TestKit.shutdownActorSystem(system)
  }
}
