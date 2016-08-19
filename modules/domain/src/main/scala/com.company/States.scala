package com.company

object States {
  sealed trait EntityState
  sealed trait ConferenceState extends EntityState
  sealed trait SessionState extends EntityState
  sealed trait SpeakerState extends EntityState
  sealed trait SponsorState extends EntityState
  sealed trait RoomState extends EntityState
  sealed trait TrackState extends EntityState
  sealed trait AppointmentState extends EntityState
  sealed trait ProductState extends EntityState
  sealed trait AttendeeState extends EntityState
  sealed trait SurveyState extends EntityState
  sealed trait SurveyEntryState extends EntityState

  case object Approved
    extends SessionState
      with SpeakerState

  case object Attended
    extends AttendeeState

  case object Cancelled
    extends AppointmentState
      with AttendeeState

  case object Closed
    extends ProductState
      with SurveyState

  case object Confirmed
    extends AttendeeState

  case object Declined
    extends AttendeeState

  case object Deleted
    extends ConferenceState
      with AppointmentState
      with AttendeeState
      with ProductState
      with RoomState
      with SessionState
      with SpeakerState
      with SponsorState
      with SurveyState
      with SurveyEntryState
      with TrackState

  case object Duplicate
    extends SurveyEntryState

  case object Exchanged
    extends AttendeeState

  case object Expired
    extends AttendeeState

  case object Imported
    extends AttendeeState

  case object Pending
    extends AttendeeState

  case object Published
    extends ConferenceState
      with AppointmentState
      with ProductState
      with RoomState
      with SessionState
      with SpeakerState
      with SponsorState
      with SurveyState
      with TrackState

  case object Proposed
    extends SessionState
      with SpeakerState

  case object Rejected
    extends SessionState
      with AttendeeState
      with SpeakerState
      with SurveyEntryState

  case object Submitted
    extends SurveyEntryState

  case object Unpublished
    extends ConferenceState
      with AppointmentState
      with ProductState
      with RoomState
      with SessionState
      with SpeakerState
      with SponsorState
      with SurveyState
      with TrackState

  case object WaitListed
    extends AttendeeState
}
