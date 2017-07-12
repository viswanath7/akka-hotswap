package com.example.akka.actor

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestFSMRef, TestKit}
import com.example.akka.actor.UserRepositoryFSM._
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, MustMatchers}
import org.slf4j.LoggerFactory

/**
  * The TestKit contains an actor named 'testActor' which is the entry point for messages to be examined
  * with the various expectMsg() assertions.
  *
  * When mixing in the trait ImplicitSender this test actor is implicitly used as sender reference
  * when dispatching messages from the test procedure.
  */
class UserRepositoryFSMSpec extends TestKit(ActorSystem("test-actor-system"))
  with ImplicitSender with FlatSpecLike with BeforeAndAfterAll with MustMatchers {

  val logger = LoggerFactory getLogger this.getClass.getSimpleName

  override def afterAll {
    logger debug "Shutting down the actor system once all the tests have been completed " +
      "so that all actors—including the test actor—are stopped. ..."
    TestKit shutdownActorSystem system
  }

  "User repository FSM actor " must "start with disconnected state and empty data" in {
    val userRepositoryFSMTester: TestFSMRef[UserRepositoryFSM.FSMState, UserRepositoryFSM.FSMStateData, UserRepositoryFSM] = TestFSMRef(new UserRepositoryFSM)
    userRepositoryFSMTester.stateName mustBe Disconnected
    userRepositoryFSMTester.stateData mustBe EmptyData
  }

  "When user repository FSM actor receives a Connect event in Disconnected state, it" should "transition to Connected state" in {
    val userRepositoryFSMTester = TestFSMRef(new UserRepositoryFSM)
    userRepositoryFSMTester ! Connect
    userRepositoryFSMTester.stateName mustBe Connected
    userRepositoryFSMTester.stateData mustBe a [SystemStatus]
  }

  "When user repository FSM actor receives any other event in Disconnected state, it" must "stay in Disconnected state" in {
    val userRepositoryFSMTester = TestFSMRef(new UserRepositoryFSM)
    userRepositoryFSMTester ! Create
    userRepositoryFSMTester.stateName mustBe Disconnected
    userRepositoryFSMTester.stateData mustBe EmptyData
  }

  "When user repository FSM actor receives a Disconnect event in Connected state, it" should "transition to Disconnected state" in {
    val userRepositoryFSMTester = TestFSMRef(new UserRepositoryFSM)
    userRepositoryFSMTester ! Connect
    userRepositoryFSMTester.stateName mustBe Connected
    userRepositoryFSMTester.stateData mustBe a [SystemStatus]
    userRepositoryFSMTester ! Disconnect
    userRepositoryFSMTester.stateName mustBe Disconnected
    userRepositoryFSMTester.stateData mustBe EmptyData
  }

  "When user repository FSM actor receives a DatabaseOperation event in Connected state, it" should "stay in Connected state" in {
    val userRepositoryFSMTester = TestFSMRef(new UserRepositoryFSM)
    userRepositoryFSMTester ! Connect
    userRepositoryFSMTester ! Create
    userRepositoryFSMTester.stateName mustBe Connected
    userRepositoryFSMTester.stateData mustBe a [SystemStatus]
  }

}
