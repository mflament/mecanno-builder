package org.yah.meccanobuilder.server.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.NoSuchElementException;

@Provider
public class NoSuchElementMapper implements ExceptionMapper<NoSuchElementException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NoSuchElementMapper.class);

    @Override
    public Response toResponse(NoSuchElementException exception) {
        LOGGER.info("Entity not found", exception);
        return Response.status(Response.Status.NOT_FOUND).entity(exception.getMessage()).build();
    }
}
