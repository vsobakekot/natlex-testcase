package com.vsobakekot.natlex.model;

import com.vsobakekot.natlex.model.enums.JobResultStatus;
import com.vsobakekot.natlex.model.enums.JobType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@ToString
@NoArgsConstructor
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private JobType type;

    @Enumerated(EnumType.STRING)
    private JobResultStatus status;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public Job(JobType type) {
        this.type = type;
    }
}
