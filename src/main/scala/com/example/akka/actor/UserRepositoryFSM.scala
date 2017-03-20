package com.example.akka.actor

import java.time.LocalDateTime

import akka.actor.{FSM, Props, Stash}
import com.example.akka.actor.UserRepositoryFSM.{SystemStatus, _}

import scala.concurrent.duration._
import org.slf4j.LoggerFactory

object UserRepositoryFSM {

  val props = Props[UserRepositoryFSM]

  sealed trait FSMState // State of the finite state machine
  case object Connected extends FSMState
  case object Disconnected extends FSMState

  sealed trait FSMStateData // State data tracked by the FSM module
  case object EmptyData extends FSMStateData
  case class SystemStatus(connectedAt: LocalDateTime) extends FSMStateData

  sealed trait Event
  case object Connect extends Event
  case object Disconnect extends Event
  case class Operation[T <:DatabaseOperations](databaseOperation: T, user:User) extends Event


  case class User(userName:String, email:String)

  sealed trait DatabaseOperations
  case object Create extends DatabaseOperations
  case object Read extends DatabaseOperations
  case object Update extends DatabaseOperations
  case object Delete extends DatabaseOperations


}
class UserRepositoryFSM extends FSM[FSMState, FSMStateData] with Stash {

  val logger = LoggerFactory getLogger UserRepositoryFSM.getClass


  // Defines the initial state and initial data
  startWith(Disconnected, EmptyData)

  // Declaration per state to be handled
  when(Disconnected) {
    case Event(Connect,_) =>
      logger debug "Received 'Connect' event while in 'Disconnected' state."
      unstashAll()
      val state = SystemStatus(LocalDateTime now)
      logger debug s"Making a transition to 'Connected' state. \t $state"
      goto(Connected) using state
    case Event(receivedEvent, stateData)=>
      logger debug s"Received event $receivedEvent while in 'Disconnected' state."
      logger debug "Stashing the event. No state transition will be triggered."
      stash()
      stay using EmptyData
  }

  /**
    * Connected state has declared timeout,
    * which means that if no message is received for 10 seconds,
    * a FSM.StateTimeout message will be generated.
    */
  when(Connected, stateTimeout = 10 second) {
    case Event(Disconnect, state) =>
      logger debug s"Received 'Disconnect' event while in 'Connected' state. \t $state"
      unstashAll()
      logger debug "Making a transition to 'Disconnected' state"
      goto(Disconnected) using EmptyData
    case Event(Operation(dbOperation, user), SystemStatus(attribute)) =>
      logger debug s"Performing database operation $dbOperation for $user..."
      //logger debug s"Connection with database was established at $attribute"
      stay using SystemStatus(attribute)
  }

  // Performs the transition into the initial state and sets up timers if required.
  initialize()
}
