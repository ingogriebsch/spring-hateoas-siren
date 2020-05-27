package com.github.ingogriebsch.spring.hateoas.siren;

import java.util.List;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
class Capital extends RepresentationModel<Capital> {

    @NonNull
    private String name;

    Capital(@NonNull String name, @NonNull Link initialLink) {
        super(initialLink);
        this.name = name;
    }

    Capital(@NonNull String name, @NonNull List<Link> initialLinks) {
        super(initialLinks);
        this.name = name;
    }
}
