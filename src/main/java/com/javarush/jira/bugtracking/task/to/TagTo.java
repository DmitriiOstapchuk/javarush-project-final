package com.javarush.jira.bugtracking.task.to;

import com.javarush.jira.common.to.BaseTo;
import com.javarush.jira.common.util.validation.NoHtml;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class TagTo  extends BaseTo {
    @NotNull
    Long taskId;
    @NoHtml
    @Size(max = 4096)
    @NotNull
    String comment;
}
