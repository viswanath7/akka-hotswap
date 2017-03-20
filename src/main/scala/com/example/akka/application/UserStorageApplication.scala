package com.example.akka.application

import akka.actor.{ActorRef, ActorSystem}
import com.example.akka.actor.UserRepository._
import com.example.akka.actor.{Switch, UserRepository}
import org.slf4j.LoggerFactory

object UserStorageApplication extends App {
  val logger = LoggerFactory getLogger UserStorageApplication.getClass

  logger info "Creating the actor system"
  private val actorSystem: ActorSystem = ActorSystem("actor-system-user-repository-stash")

  logger info "Creating a UserRepository actor"
  private val userRepository: ActorRef = actorSystem.actorOf(UserRepository.props)

  userRepository ! Operation(Create(User("john.doe")))
  userRepository ! EstablishConnection
  userRepository ! TerminateConnection

  Thread sleep 100
  logger info "Terminating actor system ..."
  actorSystem terminate
}
