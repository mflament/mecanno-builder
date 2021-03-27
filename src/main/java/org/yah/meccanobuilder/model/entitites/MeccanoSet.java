package org.yah.meccanobuilder.model.entitites;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.OptionalInt;

public class MeccanoSet implements Entity<String> {

    public static Builder builder() {
        return new Builder();
    }

    private final String number;
    private final String name;
    private final String serie;
    private final int yearReleased;
    private final Integer yearPhasedOut;

    private MeccanoSet(String number, String name, String serie, int yearReleased, Integer yearPhasedOut) {
        this.number = Objects.requireNonNull(number, "number is null");
        this.name = Objects.requireNonNull(name, "name is null");
        this.serie = Objects.requireNonNull(serie, "serie is null");
        this.yearReleased = yearReleased;
        this.yearPhasedOut = yearPhasedOut;
    }

    @Override
    public String getId() {
        return getNumber();
    }

    @Nullable
    public String getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public String getSerie() {
        return serie;
    }

    public int getYearReleased() {
        return yearReleased;
    }

    public OptionalInt getYearPhasedOut() {
        return yearPhasedOut == null ? OptionalInt.empty() : OptionalInt.of(yearPhasedOut);
    }

    @Override
    public String toString() {
        return name + " (" + serie + ") " + number;
    }

    public static final class Builder {
        private String number;
        private String name;
        private String serie;
        private int yearReleased;

        private Integer yearPhasedOut;

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

        public Builder withSerie(String serie) {
            this.serie = serie;
            return this;
        }

        public Builder withYearReleased(int yearReleased) {
            this.yearReleased = yearReleased;
            return this;
        }

        public Builder withYearPhasedOut(Integer yearPhasedOut) {
            this.yearPhasedOut = yearPhasedOut;
            return this;
        }

        public MeccanoSet build() {
            return new MeccanoSet(number, name, serie, yearReleased, yearPhasedOut);
        }
    }
}
