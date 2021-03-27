package org.yah.meccanobuilder.server.assets;

import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Environment;

import javax.servlet.AsyncContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Flow;
import java.util.function.Consumer;
import java.util.function.Function;

public class AssetsProxyBundle<T> implements ConfiguredBundle<T> {

    public static AssetsProxyBundle<?> create(String assetsServer) {
        return new AssetsProxyBundle<>(c -> assetsServer);
    }

    public static <T> AssetsProxyBundle<T> create(Function<T, String> serverPathFactory) {
        return new AssetsProxyBundle<>(serverPathFactory);
    }

    private final Function<T, String> serverPathFactory;

    private AssetsProxyBundle(Function<T, String> serverPathFactory) {
        this.serverPathFactory = Objects.requireNonNull(serverPathFactory, "serverPathFactory is null");
    }

    @Override
    public void run(T configuration, Environment environment) {
        final String server = serverPathFactory.apply(configuration);
        if (server != null) {
            environment.servlets()
                       .addServlet("assets", new AssetsProxyServlet(server))
                       .addMapping("/*");
        }
    }

    protected static class AssetsProxyServlet extends HttpServlet {

        private static final Set<String> RESTRICTED_HEADERS = Set.of("connection", "host");

        private final String target;
        private final HttpClient httpClient;

        public AssetsProxyServlet(String target) {
            this.target = target;
            httpClient = HttpClient.newBuilder().build();
        }

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            String queryPath = req.getPathInfo();
            final HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(target + queryPath)).GET();
            forEachHeader(req.getHeaderNames(), name -> forEach(req.getHeaders(name), value -> builder
                    .header(name, value)));
            final ForwardBodySubscriber forwardBodySubscriber = new ForwardBodySubscriber(resp);

            AsyncContext asyncContext = req.startAsync(req, resp);
            httpClient.sendAsync(builder.build(), response -> {
                resp.setStatus(response.statusCode());
                response.headers().map()
                        .forEach((name, values) -> {
                            if (!RESTRICTED_HEADERS.contains(name.toLowerCase()))
                                values.forEach(value -> resp.setHeader(name, value));
                        });
                return forwardBodySubscriber;
            }).whenComplete((result, error) -> asyncContext.complete());
        }

        private static void forEachHeader(Enumeration<String> enumeration, Consumer<String> consumer) {
            forEach(enumeration, header -> {
                if (!RESTRICTED_HEADERS.contains(header.toLowerCase())) consumer.accept(header);
            });
        }

        private static void forEach(Enumeration<String> enumeration, Consumer<String> consumer) {
            enumeration.asIterator().forEachRemaining(consumer);
        }

    }

    private static class ForwardBodySubscriber implements HttpResponse.BodySubscriber<Void> {

        private final HttpServletResponse target;
        private final CompletableFuture<Void> result = new CompletableFuture<>();
        private final ServletOutputStream output;
        private Flow.Subscription subscription;
        private byte[] buffer;


        public ForwardBodySubscriber(HttpServletResponse target) throws IOException {
            this.target = target;
            this.output = target.getOutputStream();
        }

        @Override
        public CompletionStage<Void> getBody() {
            return result;
        }

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            subscription.request(target.getBufferSize());
            this.subscription = subscription;
        }

        @Override
        public void onNext(List<ByteBuffer> item) {
            try {
                item.forEach(bytes -> {
                    int length = bytes.remaining();
                    if (bytes.hasArray())
                        write(bytes.array(), bytes.arrayOffset(), length);
                    else {
                        if (buffer == null || buffer.length < length)
                            buffer = new byte[length];
                        bytes.get(buffer, 0, length);
                        write(buffer, 0, length);
                    }
                });
            } catch (Exception e) {
                subscription.cancel();
                result.completeExceptionally(e);
            }
        }

        @Override
        public void onError(Throwable throwable) {
            subscription.cancel();
            result.completeExceptionally(throwable);
        }

        @Override
        public void onComplete() {
            result.complete(null);
        }

        private void write(byte[] bytes, int offset, int length) {
            try {
                output.write(bytes, offset, length);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

    }

}
