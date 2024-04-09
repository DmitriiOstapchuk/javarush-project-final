package com.javarush.jira.bugtracking.task;

import com.javarush.jira.common.model.BaseEntity;
import com.javarush.jira.common.util.validation.NoHtml;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "task_tag")
@Getter
@Setter
@NoArgsConstructor
public class Tag extends BaseEntity {
    @NotNull
    @Column(name = "task_id")
    private Long taskId;
    @NoHtml
    @Size(max = 4096)
    @NotNull
    @Column(name = "tag")
    private String tag;
}
