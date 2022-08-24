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

package org.acme.schooltimetabling.bootstrap;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.acme.schooltimetabling.domain.Lesson;
import org.acme.schooltimetabling.domain.Room;
import org.acme.schooltimetabling.domain.Timeslot;
import org.acme.schooltimetabling.persistence.LessonRepository;
import org.acme.schooltimetabling.persistence.RoomRepository;
import org.acme.schooltimetabling.persistence.TimeslotRepository;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class DemoDataGenerator {

    @Inject
    TimeslotRepository timeslotRepository;
    @Inject
    RoomRepository roomRepository;
    @Inject
    LessonRepository lessonRepository;

    @ConfigProperty(name = "demo-data.lesson-count", defaultValue = "4")
    int lessonCount;

    @Transactional
    public void generateDemoData(@Observes StartupEvent startupEvent) {
        if (lessonCount == 0) {
            return;
        }
        // Few lessons: 1 timeslot per 2 lessons - Many lessons: 40 timeslots
        int idealTimeslotCount = Math.min(DAY_OF_WEEK_LIST.size() * START_TIME_LIST.size(),
                Math.max(2, lessonCount / 2));
        int startTimeCount = Math.max(Math.min(5, idealTimeslotCount), idealTimeslotCount / DAY_OF_WEEK_LIST.size());
        int dayOfWeekCount = Math.max(1, (idealTimeslotCount + startTimeCount - 1) / startTimeCount);
        int timeslotCount = dayOfWeekCount * startTimeCount;
        // The minimum needed rooms + 10% (rounded down) + 1
        int roomCount = lessonCount == 4 ? 2 : (((lessonCount + timeslotCount - 1) / timeslotCount) * 11 / 10) + 1;

        List<Timeslot> timeslotList = new ArrayList<>(timeslotCount);
        for (DayOfWeek dayOfWeek : DAY_OF_WEEK_LIST.subList(0, dayOfWeekCount)) {
            for (LocalTime startTime : START_TIME_LIST.subList(0, startTimeCount)) {
                LocalTime endTime = startTime.plusHours(1);
                timeslotList.add(new Timeslot(dayOfWeek, startTime, endTime));
            }
        }
        timeslotRepository.persist(timeslotList);

        List<Room> roomList = new ArrayList<>(roomCount);
        for (int i = 0; i < roomCount; i++) {
            // Few rooms: A, B, C, ... - Many rooms: AA, AB, AC, ...
            String name = "Room " + Character.toString(roomCount <= 26 ? ('A' + i) : ('A' + (i / 26)) + ('A' + (i % 26)));
            roomList.add(new Room(name));
        }
        roomRepository.persist(roomList);

        List<Lesson> lessonList = LESSON_LIST.subList(0, lessonCount);
        lessonRepository.persist(lessonList);
    }

    private static final List<DayOfWeek> DAY_OF_WEEK_LIST = List.of(
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY);

    private static final List<LocalTime> START_TIME_LIST = List.of(
            LocalTime.of(8, 30),
            LocalTime.of(9, 30),
            LocalTime.of(10, 30),
            LocalTime.of(13, 30),
            LocalTime.of(14, 30),
            LocalTime.of(11, 30),
            LocalTime.of(15, 30),
            LocalTime.of(16, 30));

    private static List<Lesson> LESSON_LIST = List.of(
            new Lesson("Math", "A. Turing", "9th grade"),
            new Lesson("History", "I. Jones", "10th grade"),
            new Lesson("Chemistry", "M. Curie", "9th grade"),
            new Lesson("French", "M. Curie", "10th grade"),
            new Lesson("Math", "A. Turing", "9th grade"),
            new Lesson("Math", "A. Turing", "10th grade"),
            new Lesson("Physics", "M. Curie", "9th grade"),
            new Lesson("Math", "A. Turing", "10th grade"),
            new Lesson("Biology", "C. Darwin", "9th grade"),
            new Lesson("Math", "A. Turing", "10th grade"),
            new Lesson("History", "I. Jones", "9th grade"),
            new Lesson("Physics", "M. Curie", "10th grade"),
            new Lesson("English", "I. Jones", "9th grade"),
            new Lesson("Chemistry", "M. Curie", "10th grade"),
            new Lesson("English", "I. Jones", "9th grade"),
            new Lesson("Geography", "C. Darwin", "10th grade"),
            new Lesson("Spanish", "P. Cruz", "9th grade"),
            new Lesson("History", "I. Jones", "10th grade"),
            new Lesson("Spanish", "P. Cruz", "9th grade"),
            new Lesson("English", "P. Cruz", "10th grade"),
            new Lesson("Math", "A. Turing", "9th grade"),
            new Lesson("Spanish", "P. Cruz", "10th grade"),
            new Lesson("Math", "A. Turing", "9th grade"),
            new Lesson("Math", "A. Turing", "10th grade"),
            new Lesson("Math", "A. Turing", "9th grade"),
            new Lesson("Math", "A. Turing", "10th grade"),
            new Lesson("ICT", "A. Turing", "9th grade"),
            new Lesson("ICT", "A. Turing", "10th grade"),
            new Lesson("Physics", "M. Curie", "9th grade"),
            new Lesson("Physics", "M. Curie", "10th grade"),
            new Lesson("Geography", "C. Darwin", "9th grade"),
            new Lesson("Biology", "C. Darwin", "10th grade"),
            new Lesson("Geology", "C. Darwin", "9th grade"),
            new Lesson("Geology", "C. Darwin", "10th grade"),
            new Lesson("History", "I. Jones", "9th grade"),
            new Lesson("English", "P. Cruz", "10th grade"),
            new Lesson("English", "I. Jones", "9th grade"),
            new Lesson("English", "P. Cruz", "10th grade"),
            new Lesson("Drama", "I. Jones", "9th grade"),
            new Lesson("Drama", "I. Jones", "10th grade"),
            new Lesson("Art", "S. Dali", "9th grade"),
            new Lesson("Art", "S. Dali", "10th grade"),
            new Lesson("Art", "S. Dali", "9th grade"),
            new Lesson("Art", "S. Dali", "10th grade"),
            new Lesson("Physical education", "C. Lewis", "9th grade"),
            new Lesson("Physical education", "C. Lewis", "10th grade"),
            new Lesson("Physical education", "C. Lewis", "9th grade"),
            new Lesson("Physical education", "C. Lewis", "10th grade"),
            new Lesson("Physical education", "C. Lewis", "9th grade"),
            new Lesson("Physical education", "C. Lewis", "10th grade"),
            new Lesson("Math", "A. Turing", "11th grade"),
            new Lesson("Math", "A. Turing", "11th grade"),
            new Lesson("Math", "A. Turing", "11th grade"),
            new Lesson("Math", "A. Turing", "11th grade"),
            new Lesson("Math", "A. Turing", "11th grade"),
            new Lesson("ICT", "A. Turing", "11th grade"),
            new Lesson("Physics", "M. Curie", "11th grade"),
            new Lesson("Chemistry", "M. Curie", "11th grade"),
            new Lesson("French", "M. Curie", "11th grade"),
            new Lesson("Physics", "M. Curie", "11th grade"),
            new Lesson("Geography", "C. Darwin", "11th grade"),
            new Lesson("Biology", "C. Darwin", "11th grade"),
            new Lesson("Geology", "C. Darwin", "11th grade"),
            new Lesson("History", "I. Jones", "11th grade"),
            new Lesson("History", "I. Jones", "11th grade"),
            new Lesson("English", "P. Cruz", "11th grade"),
            new Lesson("English", "P. Cruz", "11th grade"),
            new Lesson("English", "P. Cruz", "11th grade"),
            new Lesson("Spanish", "P. Cruz", "11th grade"),
            new Lesson("Drama", "P. Cruz", "11th grade"),
            new Lesson("Art", "S. Dali", "11th grade"),
            new Lesson("Art", "S. Dali", "11th grade"),
            new Lesson("Physical education", "C. Lewis", "11th grade"),
            new Lesson("Physical education", "C. Lewis", "11th grade"),
            new Lesson("Physical education", "C. Lewis", "11th grade"),
            new Lesson("Math", "A. Turing", "12th grade"),
            new Lesson("Math", "A. Turing", "12th grade"),
            new Lesson("Math", "A. Turing", "12th grade"),
            new Lesson("Math", "A. Turing", "12th grade"),
            new Lesson("Math", "A. Turing", "12th grade"),
            new Lesson("ICT", "A. Turing", "12th grade"),
            new Lesson("Physics", "M. Curie", "12th grade"),
            new Lesson("Chemistry", "M. Curie", "12th grade"),
            new Lesson("French", "M. Curie", "12th grade"),
            new Lesson("Physics", "M. Curie", "12th grade"),
            new Lesson("Geography", "C. Darwin", "12th grade"),
            new Lesson("Biology", "C. Darwin", "12th grade"),
            new Lesson("Geology", "C. Darwin", "12th grade"),
            new Lesson("History", "I. Jones", "12th grade"),
            new Lesson("History", "I. Jones", "12th grade"),
            new Lesson("English", "P. Cruz", "12th grade"),
            new Lesson("English", "P. Cruz", "12th grade"),
            new Lesson("English", "P. Cruz", "12th grade"),
            new Lesson("Spanish", "P. Cruz", "12th grade"),
            new Lesson("Drama", "P. Cruz", "12th grade"),
            new Lesson("Art", "S. Dali", "12th grade"),
            new Lesson("Art", "S. Dali", "12th grade"),
            new Lesson("Physical education", "C. Lewis", "12th grade"),
            new Lesson("Physical education", "C. Lewis", "12th grade"),
            new Lesson("Physical education", "C. Lewis", "12th grade"));

}
