package com.ordemrealitas.ccg.gameevents

import com.ordemrealitas.ccg.gameevents.eventcauses.EventCause
import com.ordemrealitas.ccg.gameevents.eventcauses.EventCauseType
import com.ordemrealitas.ccg.match.Match


interface MatchEvent: EventCause {
    fun onExecute() {}
    val match: Match
    var cause: EventCause
    var isCancelled: Boolean

    override val eventCauseType: EventCauseType
        get() = EventCauseType.OTHER_EVENT
}