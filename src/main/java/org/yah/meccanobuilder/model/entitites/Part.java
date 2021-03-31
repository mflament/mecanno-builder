package org.yah.meccanobuilder.model.entitites;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Part implements Entity<String> {
    public static Builder builder() {
        return new Builder();
    }

    private final String name;
    private final String number;

    private Part(String number, String name) {
        this.number = Objects.requireNonNull(number, "number is null");
        this.name = Objects.requireNonNull(name, "name is null");
    }

    @Override
    public String getId() {
        return getNumber();
    }

    public String getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Part{" +
                ", number='" + number + '\'' +
                ", description='" + name + '\'' +
                '}';
    }

    public static final class Builder {
        private String number;
        private String name;

        private Builder() {
        }

        public Builder withNumber(String number) {
            this.number = number;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Part build() {
            return new Part(number, name);
        }
    }
}
