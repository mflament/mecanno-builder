package org.yah.meccanobuilder.server.resources.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.yah.meccanobuilder.model.entitites.Part;
import org.yah.meccanobuilder.model.entitites.PartColor;
import org.yah.meccanobuilder.model.entitites.PartMaterial;
import org.yah.meccanobuilder.model.entitites.SetPart;

import java.util.List;

public class SetPartDetail {
    @JsonUnwrapped
    private final Part part;
    private final List<PartColor> colors;
    private final PartMaterial material;
    private final int count;

    public SetPartDetail(Part part, List<PartColor> colors, PartMaterial material, int count) {
        this.part = part;
        this.colors = List.copyOf(colors);
        this.material = material;
        this.count = count;
    }

    public List<PartColor> getColors() {
        return colors;
    }

    public PartMaterial getMaterial() {
        return material;
    }

    public int getCount() {
        return count;
    }
}
