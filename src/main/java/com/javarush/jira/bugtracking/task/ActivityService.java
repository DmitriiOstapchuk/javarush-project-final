package com.javarush.jira.bugtracking.task;

import com.javarush.jira.bugtracking.Handlers;
import com.javarush.jira.bugtracking.task.to.ActivityTo;
import com.javarush.jira.common.error.DataConflictException;
import com.javarush.jira.login.AuthUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.javarush.jira.bugtracking.task.TaskUtil.getLatestValue;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityService {
    private final TaskRepository taskRepository;

    private final Handlers.ActivityHandler handler;
    private static final String IN_PROGRESS_STATUS_CODE = "in_progress";
    private static final String READY_FOR_REVIEW_STATUS_CODE = "ready_for_review";
    private static final String DONE_STATUS_CODE = "done";
    private static final String IN_PROGRESS_MESSAGE = "Task with task_id {} is still in progress";
    private static final String IN_TESTING_MESSAGE = "Task with task_id {} is still in testing";

    private static void checkBelong(HasAuthorId activity) {
        if (activity.getAuthorId() != AuthUser.authId()) {
            throw new DataConflictException("Activity " + activity.getId() + " doesn't belong to " + AuthUser.get());
        }
    }

    @Transactional
    public Activity create(ActivityTo activityTo) {
        checkBelong(activityTo);
        Task task = taskRepository.getExisted(activityTo.getTaskId());
        if (activityTo.getStatusCode() != null) {
            task.checkAndSetStatusCode(activityTo.getStatusCode());
        }
        if (activityTo.getTypeCode() != null) {
            task.setTypeCode(activityTo.getTypeCode());
        }
        return handler.createFromTo(activityTo);
    }

    @Transactional
    public void update(ActivityTo activityTo, long id) {
        checkBelong(handler.getRepository().getExisted(activityTo.getId()));
        handler.updateFromTo(activityTo, id);
        updateTaskIfRequired(activityTo.getTaskId(), activityTo.getStatusCode(), activityTo.getTypeCode());
    }

    @Transactional
    public void delete(long id) {
        Activity activity = handler.getRepository().getExisted(id);
        checkBelong(activity);
        handler.delete(activity.id());
        updateTaskIfRequired(activity.getTaskId(), activity.getStatusCode(), activity.getTypeCode());
    }

    private void updateTaskIfRequired(long taskId, String activityStatus, String activityType) {
        if (activityStatus != null || activityType != null) {
            Task task = taskRepository.getExisted(taskId);
            List<Activity> activities = handler.getRepository().findAllByTaskIdOrderByUpdatedDesc(task.id());
            if (activityStatus != null) {
                String latestStatus = getLatestValue(activities, Activity::getStatusCode);
                if (latestStatus == null) {
                    throw new DataConflictException("Primary activity cannot be delete or update with null values");
                }
                task.setStatusCode(latestStatus);
            }
            if (activityType != null) {
                String latestType = getLatestValue(activities, Activity::getTypeCode);
                if (latestType == null) {
                    throw new DataConflictException("Primary activity cannot be delete or update with null values");
                }
                task.setTypeCode(latestType);
            }
        }
    }


    public Long calculateTaskCompletingTime (Task task) {
        List<Activity> activities = handler.getRepository()
                .findAllByTaskIdWhereStatusCodeIsReadyForReviewOrInProgress(task.id());
        return calculateTime(task.id(), activities, IN_PROGRESS_STATUS_CODE, IN_PROGRESS_MESSAGE);
    }

    public Long calculateTaskTestingTime (Task task) {
        List<Activity> activities = handler.getRepository()
                .findAllByTaskIdWhereStatusCodeIsReadyForReviewOrDone(task.id());
        return calculateTime(task.id(), activities, READY_FOR_REVIEW_STATUS_CODE, IN_TESTING_MESSAGE);
    }

    public Long calculateTime (Long taskId, List<Activity> activities, String statusCodeStart, String infoMessage) {
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        Long duration = null;
        if (activities.get(activities.size()-1).getStatusCode().equals(statusCodeStart)) {
            log.info(infoMessage, taskId);
            return null;
        }
        for (Activity activity: activities) {
            if (activity.getStatusCode().equals(statusCodeStart)) {
                startTime = activity.getUpdated();
            } else {
                endTime = activity.getUpdated();
                duration += ChronoUnit.MINUTES.between(startTime, endTime);
            }
        }
        return duration;
    }
}
