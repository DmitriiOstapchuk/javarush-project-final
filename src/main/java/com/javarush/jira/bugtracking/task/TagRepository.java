package com.javarush.jira.bugtracking.task;

import com.javarush.jira.bugtracking.task.to.TagTo;
import com.javarush.jira.common.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface TagRepository extends BaseRepository<Tag> {
    void deleteByTaskIdAndTag(Long taskId, String tag);
    @Query("UPDATE Tag t SET t.tag = :newTag WHERE t.taskId = :taskId AND t.tag = :oldTag")
    void updateByTaskIdAndTag(Long taskId, String oldTag, String newTag);

    List<Tag> findAllByTaskId(Long taskId);
}
