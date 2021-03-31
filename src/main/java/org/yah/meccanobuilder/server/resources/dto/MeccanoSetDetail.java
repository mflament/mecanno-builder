package org.yah.meccanobuilder.server.resources.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.yah.meccanobuilder.model.entitites.MeccanoSet;

import java.util.List;

public class MeccanoSetDetail {

    @JsonUnwrapped
    private final MeccanoSet set;
    private final List<SetPartDetail> parts;

    public MeccanoSetDetail(MeccanoSet set, List<SetPartDetail> parts) {
        this.set = set;
        this.parts = List.copyOf(parts);
    }

    public MeccanoSet getSet() {
        return set;
    }

    public List<SetPartDetail> getParts() {
        return parts;
    }

}
