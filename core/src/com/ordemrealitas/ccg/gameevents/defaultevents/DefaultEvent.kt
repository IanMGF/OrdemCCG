package com.ordemrealitas.ccg.gameevents.defaultevents

import com.ordemrealitas.ccg.gameevents.MatchEvent
import com.ordemrealitas.ccg.gameevents.eventcauses.EventCause
import com.ordemrealitas.ccg.match.Match

abstract class DefaultEvent(override val match: Match, override var cause: EventCause = match) : MatchEvent {
    override var isCancelled: Boolean = false
}