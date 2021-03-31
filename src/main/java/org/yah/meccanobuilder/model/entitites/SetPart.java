package org.yah.meccanobuilder.model.entitites;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


public class SetPart implements Entity<SetPart.SetPartId> {

    public static Builder builder() {
        return new Builder();
    }

    private final SetPartId id;
    private final List<String> colorIds;
    @Nullable
    private final String materialId;
    private final int count;

    private SetPart(SetPartId id, List<String> colorIds, @Nullable String materialId, int count) {
        this.id = Objects.requireNonNull(id, "id is null");
        this.colorIds = Objects.requireNonNull(colorIds, "colorIds is null");
        this.materialId = materialId;
        if (count <= 0)
            throw new IllegalArgumentException("Invalid count " + count);
        this.count = count;
    }

    @Override
    public SetPartId getId() {
        return id;
    }

    public List<String> getColorIds() {
        return colorIds;
    }

    public Optional<String> getMaterialId() {
        return Optional.ofNullable(materialId);
    }

    public int getCount() {
        return count;
    }

    public String getSetId() {
        return id.getSetId();
    }

    public String getPartId() {
        return id.getPartId();
    }

    public static final class SetPartId {
        private final String setId;
        private final String partId;

        public SetPartId(String setId, String partId) {
            this.setId = setId;
            this.partId = partId;
        }

        public String getSetId() {
            return setId;
        }

        public String getPartId() {
            return partId;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;
            SetPartId setPartId = (SetPartId) object;
            return setId == setPartId.setId && partId == setPartId.partId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(setId, partId);
        }

        @Override
        public String toString() {
            return "SetPartId{" +
                    "partId='" + partId + '\'' +
                    ", setId='" + setId + '\'' +
                    '}';
        }

    }

    public static final class Builder {
        private String partId;
        private String setId;
        private List<String> colorIds;
        private String materialId;

        private int count;

        private Builder() {
        }

        public Builder withPartId(String partId) {
            this.partId = partId;
            return this;
        }

        public Builder withSetId(String setId) {
            this.setId = setId;
            return this;
        }

        public Builder withColorIds(String... colorIds) {
            this.colorIds = Arrays.asList(colorIds);
            return this;
        }

        public Builder withMaterialId(String materialId) {
            this.materialId = materialId;
            return this;
        }

        public Builder withCount(int count) {
            this.count = count;
            return this;
        }

        public SetPart build() {
            return new SetPart(new SetPartId(setId, partId), colorIds, materialId, count);
        }
    }
}
