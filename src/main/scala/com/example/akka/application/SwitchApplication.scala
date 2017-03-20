package com.example.akka.application

import akka.actor.{ActorRef, ActorSystem}
import com.example.akka.actor.Switch
import com.example.akka.actor.Switch.Flip
import org.slf4j.LoggerFactory


object SwitchApplication extends App {

  val logger = LoggerFactory getLogger SwitchApplication.getClass

  logger info "Creating the actor system"
  private val actorSystem: ActorSystem = ActorSystem("actor-system-hotswap-become")

  logger info "Creating a Switch actor"
  private val switch: ActorRef = actorSystem.actorOf(Switch.props)

  for(count <- 1 until 5) switch ! Flip

  Thread sleep 100
  logger debug "Terminating actor system ..."
  actorSystem terminate
}
