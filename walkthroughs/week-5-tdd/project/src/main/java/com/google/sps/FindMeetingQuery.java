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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class FindMeetingQuery {

  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    List<TimeRange> output = Arrays.asList(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, TimeRange.END_OF_DAY, true));

    // Update output time slots for all mandatory attendees
    for (String attendee : request.getAttendees()) {
      output = updateOutput(events, attendee, output);
    }

    List<TimeRange> mandatoryOutput = cleanUpOutput(request, output);
    
    // Update output time slots for all optional attendees
    for (String attendee : request.getOptionalAttendees()) {
      output = updateOutput(events, attendee, output);
    }

    List<TimeRange> optionalOutput = cleanUpOutput(request, output);

    if (optionalOutput.size() == 0 && request.getAttendees().size() != 0) {
      return mandatoryOutput;
    }
    return optionalOutput;
  }

  List<TimeRange> updateOutput(Collection<Event> events, String attendee, List<TimeRange> output) {
    // For each time slot of that attendee
    for (Event event : events) {
      if (event.getAttendees().contains(attendee)) {
        TimeRange existingTR = event.getWhen();
        List<TimeRange> tempRanges = new ArrayList<TimeRange>();

        // For each currently "open" time slot
        for (TimeRange possibleTR : output) {
          if (possibleTR.overlaps(existingTR)) {
            // Case 1: E |------|
            //         P  |---|
            //
            // Case 2: E     |---|
            //         P |----------|
            //
            // Case 3: E     |-------|
            //         P  |------|
            //
            // Case 4: E |-----|
            //         P   |-------|
            int possibleStart = possibleTR.start();
            int possibleEnd = possibleTR.end();
            int existingStart = existingTR.start();
            int existingEnd = existingTR.end();

            if (existingTR.contains(possibleTR)) {
              // Disregard possibleTR
              continue;
            } else if (possibleTR.contains(existingTR)) {
              // Split into two on either side
              tempRanges.add(TimeRange.fromStartEnd(possibleStart, existingStart, false));
              tempRanges.add(TimeRange.fromStartEnd(existingEnd, possibleEnd, false));
            } else if (possibleStart < existingStart) {
              // Split into one on left
              tempRanges.add(TimeRange.fromStartEnd(possibleStart, existingStart, false));
            } else if (possibleEnd > existingEnd) {
              // Split into one on right
              tempRanges.add(TimeRange.fromStartEnd(existingEnd, possibleEnd, false));
            }
          } else {
            tempRanges.add(possibleTR);
          }
        }
        output = tempRanges;
      }
    }
    return output;
  }

  // Remove any leftover time slots that have durations outside of [requestedDuration, infinity)
  List<TimeRange> cleanUpOutput(MeetingRequest request, List<TimeRange> output) {
    List<TimeRange> tempOutput = new ArrayList<TimeRange>();
    for (TimeRange possibleTR : output) {
      int duration = possibleTR.duration();
      if (duration < request.getDuration()) {
        continue;
      }
      tempOutput.add(possibleTR);
    }
    return tempOutput;
  }
}
