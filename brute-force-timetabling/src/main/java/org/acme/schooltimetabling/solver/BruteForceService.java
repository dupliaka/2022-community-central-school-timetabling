package org.acme.schooltimetabling.solver;

import java.util.List;
import java.util.Objects;
import javax.enterprise.context.ApplicationScoped;

import org.acme.schooltimetabling.domain.Lesson;
import org.acme.schooltimetabling.domain.Room;
import org.acme.schooltimetabling.domain.TimeTable;
import org.acme.schooltimetabling.domain.Timeslot;

@ApplicationScoped
public class BruteForceService {

    // TODO make record
    public static class Choice {
        public int timeslotIndex = 0;
        public int roomIndex = 0;

        public Choice() {
        }

        public Choice(Choice other) {
            timeslotIndex = other.timeslotIndex;
            roomIndex = other.roomIndex;
        }

        @Override
        public String toString() {
            return timeslotIndex + "," + roomIndex;
        }
    }

    public void solve(TimeTable timeTable) {
        List<Lesson> lessonList = timeTable.getLessonList();
        List<Timeslot> timeslotList = timeTable.getTimeslotList();
        List<Room> roomList = timeTable.getRoomList();

        Choice[] choices = new Choice[lessonList.size()];
        for (int i = 0; i < choices.length; i++) {
            choices[i] = new Choice();
        }

        Choice[] bestChoices = null;
        long bestScore = Long.MIN_VALUE;
        boolean finished = true;
        while (finished) {
            setupChoices(lessonList, timeslotList, roomList, choices);
            long score = calculateScore(timeTable);
            if (score > bestScore) {
                bestScore = score;
                bestChoices = new Choice[choices.length];
                for (int i = 0; i < choices.length; i++) {
                    bestChoices[i] = new Choice(choices[i]);
                }
            }
            finished = incrementChoices(timeslotList, roomList, choices, finished);
        }
        setupChoices(lessonList, timeslotList, roomList, bestChoices);
        timeTable.setScore(bestScore);
    }

    private void setupChoices(List<Lesson> lessonList, List<Timeslot> timeslotList, List<Room> roomList, Choice[] choices) {
        for (int i = 0; i < lessonList.size(); i++) {
            Lesson lesson = lessonList.get(i);
            Choice choice = choices[i];
            lesson.setTimeslot(timeslotList.get(choice.timeslotIndex));
            lesson.setRoom(roomList.get(choice.roomIndex));
        }
    }

    private boolean incrementChoices(List<Timeslot> timeslotList, List<Room> roomList, Choice[] choices, boolean finished) {
        for (int i = 0;;) {
            choices[i].timeslotIndex++;
            if (choices[i].timeslotIndex < timeslotList.size()) {
                return true;
            }
            choices[i].timeslotIndex = 0;
            choices[i].roomIndex++;
            if (choices[i].roomIndex < roomList.size()) {
                return true;
            }
            choices[i].roomIndex = 0;
            i++;
            if (i >= choices.length) {
                return false;
            }
        }
    }

    private long calculateScore(TimeTable timeTable) {
        long hardScore = 0;
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
        return hardScore;
    }

}
