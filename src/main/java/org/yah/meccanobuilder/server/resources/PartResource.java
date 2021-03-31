package org.yah.meccanobuilder.server.resources;


import com.codahale.metrics.annotation.Timed;
import org.yah.meccanobuilder.model.entitites.Part;
import org.yah.meccanobuilder.model.repository.PartsRepository;
import org.yah.meccanobuilder.server.resources.dto.PartDetail;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Objects;

@Produces(MediaType.APPLICATION_JSON)
@Path("/part")
@Timed
public class PartResource {

    private final PartsRepository partsRepository;

    @Inject
    public PartResource(PartsRepository partsRepository) {
        this.partsRepository = Objects.requireNonNull(partsRepository, "partsRepository is null");
    }

    @GET
    @Path("/")
    public List<Part> getParts() {
        return partsRepository.getParts();
    }

    @GET
    @Path("{partNumber}")
    public PartDetail part(@PathParam("partNumber") String partNumber) {
        return newPartDetail(partsRepository.getPart(partNumber));
    }

    private PartDetail newPartDetail(Part part) {
        final PartDetail.Builder builder = PartDetail.builder().withPart(part);
        partsRepository.getSetParts().stream()
                .filter(sp -> sp.getPartId().equals(part.getId()))
                .forEach(sp -> {
                    builder.withSet(partsRepository.getSet(sp.getSetId()));
                    sp.getMaterialId().map(partsRepository::getMaterial).ifPresent(builder::withMaterial);
                    sp.getColorIds().stream().map(partsRepository::getColor).forEach(builder::withColor);
                });
        return builder.build();
    }

}
