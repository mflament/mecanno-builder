package org.yah.meccanobuilder.model.entitites;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface Entity<K> {
    @JsonIgnore
    K getId();
}
