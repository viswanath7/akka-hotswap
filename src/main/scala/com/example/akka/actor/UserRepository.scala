package com.example.akka.actor

import akka.actor.{Actor, Props, Stash}
import com.example.akka.actor.UserRepository.{EstablishConnection, Operation, TerminateConnection, User}
import org.slf4j.{Logger, LoggerFactory}

object UserRepository {
  val props = Props[UserRepository]

  case class User(userName:String)

  sealed trait RequestMessage
  case object EstablishConnection extends RequestMessage
  case object TerminateConnection extends RequestMessage
  case class Operation[T <:DatabaseOperation](databaseOperation: T) extends RequestMessage

  sealed trait DatabaseOperation
  case class Create(user: User) extends DatabaseOperation
  case class Read(userName: String) extends DatabaseOperation
  case class Update(user: User) extends DatabaseOperation
  case class Delete(user: User) extends DatabaseOperation
}


class UserRepository extends Actor with Stash {

  val logger = LoggerFactory getLogger UserRepository.getClass

  /**
    * Defines initial actor behavior
    * @return An Actor.Receive, which is a type alias for PartialFunction[Any,Unit]
    */
  override def receive: Receive = disconnected

  def disconnected: Receive = {
    case EstablishConnection => logger info "Establishing connection with database ..."
      unstashAll
      context become connected
    case TerminateConnection => logger info "Already disconnected. Discarding message 'TerminateConnection'."
    case Operation(dbOperation) =>
      logger info s"Stashing operation $dbOperation as the database is in disconnected state."
      stash
    case _ => logger warn "Unsupported message type!"
  }

  def connected: Receive = {
    case EstablishConnection => logger info "Already connected with database! Discarding message 'EstablishConnection'."
    case TerminateConnection => logger info "Terminating connection with database ..."
      context unbecome
    case Operation(dbOperation) => logger info s"Performing $dbOperation operation"
    case _ => logger warn "Unsupported message type!"
  }

}
