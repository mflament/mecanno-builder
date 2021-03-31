package org.yah.meccanobuilder.server;

import io.dropwizard.Configuration;

import javax.annotation.Nullable;

public class MBServerConfiguration extends Configuration {

    private String assetsServer = "http://localhost:9080";

    @Nullable
    public String getAssetsServer() {
        return assetsServer;
    }

    public void setAssetsServer(@Nullable String assetsServer) {
        this.assetsServer = assetsServer;
    }
}
