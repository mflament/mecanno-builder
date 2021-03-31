package org.yah.meccanobuilder.server.resources;


import com.codahale.metrics.annotation.Timed;
import org.yah.meccanobuilder.model.entitites.*;
import org.yah.meccanobuilder.model.repository.PartsRepository;
import org.yah.meccanobuilder.server.resources.dto.MeccanoSetDetail;
import org.yah.meccanobuilder.server.resources.dto.SetPartDetail;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Produces(MediaType.APPLICATION_JSON)
@Path("/set")
@Timed
public class SetResource {
    private final PartsRepository partsRepository;

    @Inject
    public SetResource(PartsRepository partsRepository) {
        this.partsRepository = Objects.requireNonNull(partsRepository, "partsRepository is null");
    }

    @GET
    @Path("/")
    public List<MeccanoSet> getSets() {
        return partsRepository.getSets();
    }

    @GET
    @Path("/{number}")
    public MeccanoSetDetail getSet(@PathParam("number") String number) {
        return newSetDetail(partsRepository.getSet(number));
    }

    private MeccanoSetDetail newSetDetail(MeccanoSet set) {
        final var partDetails = partsRepository.getSetParts(set.getId()).stream()
                .map(this::newSetPartDetail)
                .collect(Collectors.toList());
        return new MeccanoSetDetail(set, partDetails);
    }

    private SetPartDetail newSetPartDetail(SetPart sp) {
        final Part part = partsRepository.getPart(sp.getPartId());
        final List<PartColor> colors = sp.getColorIds().stream().map(partsRepository::getColor).collect(Collectors.toList());
        final PartMaterial material = sp.getMaterialId().map(partsRepository::getMaterial).orElse(null);
        return new SetPartDetail(part, colors, material, sp.getCount());
    }

}
