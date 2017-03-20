package com.example.akka.actor

import akka.actor.{Actor, Props}
import akka.actor.Actor.Receive
import com.example.akka.actor.Switch.Flip
import org.slf4j.LoggerFactory

object Switch {
  val props = Props[Switch]
  sealed trait RequestMesage
  case object Flip extends RequestMesage
}

class Switch extends Actor {

  val logger = LoggerFactory getLogger Switch.getClass

  /**
    * Defines initial actor behavior
    * @return An Actor.Receive, which is a type alias for PartialFunction[Any,Unit]
    */
  override def receive: Receive = off

  /**
    *
    * @return
    */
  def off: Receive = {
    case Flip => logger info "Flipping the switch to ON state"
      logger debug "Hot-swapping new message handling code at runtime, by replacing current behaviour on the top of the behaviour stack."
      context become on
    /**
      * Care must be taken to ensure that the number of “pop” operations (i.e. unbecome)
      * matches the number of “push” ones in the long run, otherwise this amounts to a memory leak
      */
    case _ => logger warn "Unsupported message type"
  }

  /**
    *
    * @return
    */
  def on: Receive = {
    case Flip => logger info "Flipping the switch to OFF state"
      logger debug "Reset the latest behaviour set by last 'become' function on behaviour stack."
      context unbecome
    case _ => logger warn "Unsupported message type"
  }
}
