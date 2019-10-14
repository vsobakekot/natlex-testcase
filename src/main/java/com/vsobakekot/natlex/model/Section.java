package com.vsobakekot.natlex.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@ToString(of = {"id", "name"})
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @NotNull
    private String name;

    @JsonManagedReference
    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<GeologicalClass> geologicalClasses;

    @JsonCreator
    public Section(@JsonProperty("name") @NotNull String name, @JsonProperty("geologicalClasses") List<GeologicalClass> geologicalClasses) {
        this.name = name;
        this.geologicalClasses = geologicalClasses;
    }

    public Section(@NotNull String name) {
        this.name = name;
    }
}
