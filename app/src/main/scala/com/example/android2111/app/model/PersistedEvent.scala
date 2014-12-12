package com.example.android2111.app.model

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable class PersistedEvent(_persistenceId: String, _seqNr: Long, payload: Any) extends WithId {
  @DatabaseField val persistenceId = _persistenceId
  @DatabaseField val className = payload.getClass.getName
  @DatabaseField val seqNr = _seqNr
}