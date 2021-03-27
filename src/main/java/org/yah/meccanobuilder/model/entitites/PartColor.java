package org.yah.meccanobuilder.model.entitites;

import java.util.Objects;

public class PartColor implements Entity<String> {
    private final String id;
    private final String name;

    public PartColor(String id, String name) {
        this.id = Objects.requireNonNull(id, "id is null");
        this.name = Objects.requireNonNull(name, "name is null");
    }

    @Override
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
