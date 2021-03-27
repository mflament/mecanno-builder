package org.yah.meccanobuilder.model.repository;

import org.yah.meccanobuilder.model.entitites.*;

import java.util.List;

public class JsonParts {

    private static final int VERSION = 1;

    public static Builder builder() {
        return new Builder();
    }

    private final List<PartColor> colors;
    private final List<PartMaterial> materials;
    private final List<MeccanoSet> sets;
    private final List<Part> parts;
    private final List<SetPart> setPart;

    private JsonParts(List<PartColor> colors, List<PartMaterial> materials, List<MeccanoSet> sets, List<Part> parts, List<SetPart> setPart) {
        this.colors = colors;
        this.materials = materials;
        this.sets = sets;
        this.parts = parts;
        this.setPart = setPart;
    }

    public int getVersion() {
        return VERSION;
    }

    public List<PartColor> getColors() {
        return colors;
    }

    public List<PartMaterial> getMaterials() {
        return materials;
    }

    public List<MeccanoSet> getSets() {
        return sets;
    }

    public List<Part> getParts() {
        return parts;
    }

    public List<SetPart> getSetPart() {
        return setPart;
    }

    public static final class Builder {
        private List<PartColor> colors;
        private List<PartMaterial> materials;
        private List<MeccanoSet> sets;
        private List<Part> parts;
        private List<SetPart> setPart;

        private Builder() {
        }

        public Builder withColors(List<PartColor> colors) {
            this.colors = colors;
            return this;
        }

        public Builder withMaterials(List<PartMaterial> materials) {
            this.materials = materials;
            return this;
        }

        public Builder withSets(List<MeccanoSet> sets) {
            this.sets = sets;
            return this;
        }

        public Builder withParts(List<Part> parts) {
            this.parts = parts;
            return this;
        }

        public Builder withSetPart(List<SetPart> setPart) {
            this.setPart = setPart;
            return this;
        }

        public JsonParts build() {
            return new JsonParts(colors, materials, sets, parts, setPart);
        }
    }
}
