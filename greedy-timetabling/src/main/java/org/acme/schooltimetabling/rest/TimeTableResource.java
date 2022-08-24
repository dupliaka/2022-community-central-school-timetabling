/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.acme.schooltimetabling.rest;

import io.quarkus.logging.Log;
import io.quarkus.panache.common.Sort;
import org.acme.schooltimetabling.domain.Lesson;
import org.acme.schooltimetabling.domain.Room;
import org.acme.schooltimetabling.domain.TimeTable;
import org.acme.schooltimetabling.domain.Timeslot;
import org.acme.schooltimetabling.persistence.LessonRepository;
import org.acme.schooltimetabling.persistence.RoomRepository;
import org.acme.schooltimetabling.persistence.TimeslotRepository;
import org.acme.schooltimetabling.solver.GreedyService;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.Objects;

@Path("timeTable")
public class TimeTableResource {
    @Inject
    TimeslotRepository timeslotRepository;
    @Inject
    RoomRepository roomRepository;
    @Inject
    LessonRepository lessonRepository;
    @Inject
    GreedyService greedyService;

    // To try, open http://localhost:8080/timeTable
    @GET
    public TimeTable getTimeTable() {
        return findById();
    }

    @GET
    @Path("solve")
    public TimeTable getTimeSolvedTable() {
        TimeTable timeTable = findById();
        long startMillis = System.currentTimeMillis();
        greedyService.solve(timeTable);
        long endMillis = System.currentTimeMillis();
        Log.info("Greedy algorithm took " + (endMillis - startMillis) + " milliseconds.");
        save(timeTable);
        return timeTable;
    }

    @POST
    @Path("stopSolving")
    public void stopSolving() {
        throw new UnsupportedOperationException("GreedyService does not support async termination.");
    }

    @Transactional
    protected TimeTable findById() {
        return new TimeTable(
                timeslotRepository.listAll(Sort.by("dayOfWeek").and("startTime").and("endTime").and("id")),
                roomRepository.listAll(Sort.by("name").and("id")),
                lessonRepository.listAll(Sort.by("id")));
    }

    @Transactional
    protected void save(TimeTable timeTable) {
        for (Lesson lesson : timeTable.getLessonList()) {
            Lesson attachedLesson = lessonRepository.findById(lesson.getId());
            attachedLesson.setTimeslot(lesson.getTimeslot());
            attachedLesson.setRoom(lesson.getRoom());
        }
    }

}
