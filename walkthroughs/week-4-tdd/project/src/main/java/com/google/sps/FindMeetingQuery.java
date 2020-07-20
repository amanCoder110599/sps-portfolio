// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.Set;

public final class FindMeetingQuery {

  public static final Comparator<Event> orderByStartThenEnd = new Comparator<Event>() {
    @Override
    public int compare(Event a, Event b){
      if(a.getWhen().start() != b.getWhen().start())
        return Integer.compare(a.getWhen().start(), b.getWhen().start());
      else
        return Integer.compare(a.getWhen().end(), b.getWhen().end());
    }
  };

  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<String> peopleAttending = request.getAttendees();
    List<Event> eventWithPeopleAttending = new ArrayList<>();
    for(Event curr_event : events){
      boolean isNotAttending = true;
      for(String curr_attendee : curr_event.getAttendees()){
        if(peopleAttending.contains(curr_attendee))
        isNotAttending = false;
      }
      if(!isNotAttending)
        eventWithPeopleAttending.add(curr_event);
    }
    Collections.sort(eventWithPeopleAttending, orderByStartThenEnd);

    Collection<TimeRange> opt_ranges = new ArrayList<>();

    //Edge case
    //if the duration is greater than the duration of a day
    if(request.getDuration() > TimeRange.WHOLE_DAY.duration()){
      return opt_ranges;
    }
    
    int maxTimeTillNow = TimeRange.START_OF_DAY;
    for(Event e : eventWithPeopleAttending){
      if(e.getWhen().end() <= maxTimeTillNow)
        continue;
      TimeRange curr = TimeRange.fromStartDuration(maxTimeTillNow, (int)request.getDuration());
      if(!curr.overlaps(e.getWhen())) //if [maxTimeTillNow, maxTimeTillNow + request.getDuration() - 1] does not overlap with e
        opt_ranges.add(TimeRange.fromStartEnd(maxTimeTillNow, e.getWhen().start(), false));
      maxTimeTillNow = Math.max(maxTimeTillNow, e.getWhen().end());
    }

    if(maxTimeTillNow <= TimeRange.END_OF_DAY)
      opt_ranges.add(TimeRange.fromStartEnd(maxTimeTillNow, TimeRange.END_OF_DAY, true));

    return opt_ranges;
  }
}
