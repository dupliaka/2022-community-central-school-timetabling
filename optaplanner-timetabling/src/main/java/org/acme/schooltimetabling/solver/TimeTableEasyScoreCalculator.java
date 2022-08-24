/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.acme.schooltimetabling.solver;

import org.acme.schooltimetabling.domain.Lesson;
import org.acme.schooltimetabling.domain.TimeTable;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;

public class TimeTableEasyScoreCalculator implements EasyScoreCalculator<TimeTable, HardSoftScore> {

    @Override
    public HardSoftScore calculateScore(TimeTable timeTable) {
        int hardScore = 0;
        for (Lesson lesson1 : timeTable.getLessonList()) {
            for (Lesson lesson2 : timeTable.getLessonList()) {
                if (lesson1 == lesson2) {
                    break;
                }
                if (lesson1.getRoom() == lesson2.getRoom()
                        && lesson1.getTimeslot() == lesson2.getTimeslot()) {
                    hardScore--;
                }
                if (lesson1.getTeacher() == lesson2.getTeacher()
                        && lesson1.getTimeslot() == lesson2.getTimeslot()) {
                    hardScore--;
                }
                if (lesson1.getStudentGroup() == lesson2.getStudentGroup()
                        && lesson1.getTimeslot() == lesson2.getTimeslot()) {
                    hardScore--;
                }
            }
        }
        return HardSoftScore.ofHard(hardScore);
    }

}
