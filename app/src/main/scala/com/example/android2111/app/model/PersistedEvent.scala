package com.example.android2111.app.model

import com.google.gson.Gson
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable class PersistedEvent(_persistenceId: String, _seqNr: Long, _payload: Any) extends WithId {
  @DatabaseField val persistenceId = _persistenceId
  @DatabaseField val className = _payload.getClass.getName
  @DatabaseField val seqNr = _seqNr
  @DatabaseField val payload = PersistedEvent.gson.toJson(_payload)
  private def this() = this("", 0l, None) // Called by Ormlite. Will then set final fields
}

object PersistedEvent {
  val gson = new Gson()
}