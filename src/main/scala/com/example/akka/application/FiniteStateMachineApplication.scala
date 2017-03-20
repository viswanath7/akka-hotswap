package com.example.akka.application

import akka.actor.ActorSystem
import com.example.akka.actor.UserRepositoryFSM
import com.example.akka.actor.UserRepositoryFSM._
import org.slf4j.LoggerFactory


object FiniteStateMachineApplication extends App {

  val logger = LoggerFactory getLogger FiniteStateMachineApplication.getClass

  logger info "Creating the actor system"
  val actorSystem = ActorSystem("actor-system-user-repository-finite-state-machine")

  logger info "Create finite state machine actor for user repository"
  val userRepositoryFSM = actorSystem.actorOf(UserRepositoryFSM.props, "user-repository-fsm")

  private var user = User("JohnDoe", "John.Doe@example.com")

  userRepositoryFSM ! Operation(Create, user)
  userRepositoryFSM ! Connect
  user=User("JohnDoe","John.Doe@test.it")
  userRepositoryFSM ! Operation(Update, user)
  userRepositoryFSM ! Operation(Delete,user)
  userRepositoryFSM ! Disconnect
  Thread sleep 100
  userRepositoryFSM ! Connect
  userRepositoryFSM ! Disconnect

  Thread sleep 100

  logger info "Terminating actor system ..."
  actorSystem terminate
}
