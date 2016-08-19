package com.company.service

import com.company.ConferenceId
import com.company.readmodel.Conference

import scala.concurrent.{ExecutionContext, Future}

trait ConferenceRepo {
  def read(id: ConferenceId)(implicit ec: ExecutionContext): Future[Option[Conference]]
  def save(model: Conference)(implicit ec: ExecutionContext): Future[Unit]
}
