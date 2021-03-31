package org.yah.meccanobuilder.server.resources.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.yah.meccanobuilder.model.entitites.MeccanoSet;
import org.yah.meccanobuilder.model.entitites.Part;
import org.yah.meccanobuilder.model.entitites.PartColor;
import org.yah.meccanobuilder.model.entitites.PartMaterial;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class PartDetail {

    public static Builder builder() {
        return new Builder();
    }

    @JsonUnwrapped
    private final Part part;
    private final List<MeccanoSet> sets;
    private final List<PartColor> colors;
    private final List<PartMaterial> materials;

    private PartDetail(Part part, Collection<MeccanoSet> sets, Collection<PartColor> colors, Collection<PartMaterial> materials) {
        this.part = part;
        this.sets = List.copyOf(sets);
        this.colors = List.copyOf(colors);
        this.materials = List.copyOf(materials);
    }

    public List<MeccanoSet> getSets() {
        return sets;
    }

    public List<PartColor> getColors() {
        return colors;
    }

    public List<PartMaterial> getMaterials() {
        return materials;
    }

    public static final class Builder {
        private Part part;
        private Set<MeccanoSet> sets = new LinkedHashSet<>();
        private Set<PartColor> colors = new LinkedHashSet<>();
        private Set<PartMaterial> materials = new LinkedHashSet<>();

        private Builder() {
        }

        public Builder withPart(Part part) {
            this.part = part;
            return this;
        }

        public Builder withSet(MeccanoSet set) {
            this.sets.add(set);
            return this;
        }

        public Builder withColor(PartColor color) {
            colors.add(color);
            return this;
        }

        public Builder withMaterial(PartMaterial material) {
            this.materials.add(material);
            return this;
        }

        public PartDetail build() {
            return new PartDetail(part, sets, colors, materials);
        }
    }
}
