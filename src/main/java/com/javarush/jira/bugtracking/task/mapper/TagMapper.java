package com.javarush.jira.bugtracking.task.mapper;

import com.javarush.jira.bugtracking.task.Activity;
import com.javarush.jira.bugtracking.task.Tag;
import com.javarush.jira.bugtracking.task.to.ActivityTo;
import com.javarush.jira.bugtracking.task.to.TagTo;
import com.javarush.jira.common.BaseMapper;
import com.javarush.jira.common.error.DataConflictException;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TagMapper  extends BaseMapper<Tag, TagTo> {
    static long checkTaskBelong(long taskId, Tag dbTag) {
        if (taskId != dbTag.getTaskId())
            throw new DataConflictException("Tag " + dbTag.id() + " doesn't belong to Task " + taskId);
        return taskId;
    }

    @Override
    @Mapping(target = "taskId", expression = "java(TagMapper.checkTaskBelong(tagTo.getTaskId(), tag))")
    Tag updateFromTo(TagTo tagTo, @MappingTarget Tag tag);
}
