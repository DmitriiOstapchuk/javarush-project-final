package com.javarush.jira.bugtracking.task;

import com.javarush.jira.bugtracking.Handlers;
import com.javarush.jira.bugtracking.task.to.TagTo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TagService {
    private final TaskRepository taskRepository;

    private final Handlers.TagHandler handler;

    @Transactional
    public Tag create(TagTo tagTo) {
        return handler.createFromTo(tagTo);
    }

    @Transactional
    public void update(TagTo oldTagTo, TagTo newTagTo) {
        Long taskId = oldTagTo.getTaskId();
        String oldTag = oldTagTo.getTag();
        String newTag = newTagTo.getTag();
        handler.getRepository().updateByTaskIdAndTag(taskId, oldTag, newTag);
    }

    @Transactional
    public void delete(TagTo tagTo) {
        Long taskId = tagTo.getTaskId();
        String tagString = tagTo.getTag();
        handler.getRepository().deleteByTaskIdAndTag(taskId, tagString);
    }

    public List<Task> findAllTasksByTag(String tag) {
        return taskRepository.findAllByTag(tag);
    }

}
