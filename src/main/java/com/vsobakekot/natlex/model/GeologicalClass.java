package com.vsobakekot.natlex.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter @Setter
@ToString(of = {"id","name","code"})
@NoArgsConstructor
public class GeologicalClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String code;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "section_id", nullable = false)
    @NotNull
    private Section section;

    @JsonCreator
    public GeologicalClass(@JsonProperty("name") @NotNull String name, @JsonProperty("code") @NotNull String code) {
        this.name = name;
        this.code = code;
    }

    public GeologicalClass(@NotNull String name, @NotNull String code, @NotNull Section section) {
        this.name = name;
        this.code = code;
        this.section = section;
    }
}
