package org.yah.meccanobuilder.model.entitites;

import javax.annotation.Nullable;
import java.util.Objects;

public class Part implements Entity<String> {
    public static Builder builder() {
        return new Builder();
    }
    private final String name;
    private final String number;
    @Nullable
    private final String newNumber;

    private Part(String number, @Nullable String newNumber, String name) {
        this.number = Objects.requireNonNull(number, "number is null");
        this.newNumber = newNumber;
        this.name = Objects.requireNonNull(name, "name is null");
    }

    @Override
    public String getId() {
        return getName();
    }

    public String getNumber() {
        return number;
    }

    @Nullable
    public String getNewNumber() {
        return newNumber;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Part{" +
                ", number='" + number + '\'' +
                ", newNumber='" + newNumber + '\'' +
                ", description='" + name + '\'' +
                '}';
    }

    public static final class Builder {
        private String number;
        private String newNumber;
        private String name;

        private Builder() {
        }

        public Builder withNumber(String number) {
            this.number = number;
            return this;
        }

        public Builder withNewNumber(String newNumber) {
            this.newNumber = newNumber;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Part build() {
            return new Part(number, newNumber, name);
        }
    }
}
