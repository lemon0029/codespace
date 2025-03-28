package io.nullptr.ch_client;

import com.google.protobuf.ByteString;
import io.grpc.Channel;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.nullptr.ch.client.grpc.ClickHouseGrpc;
import io.nullptr.ch.client.grpc.QueryInfo;
import io.nullptr.ch.client.grpc.Result;

import java.net.URI;
import java.util.concurrent.TimeUnit;

public class ClickHouseClient {

    private URI serverUri;
    private String serverVersion;

    private String username;
    private String password;

    private Channel channel;

    public static ClickHouseClient connect(String url, String username, String password) {

        if (!url.startsWith("grpc://")) {
            url = "grpc://" + url;
        }

        ClickHouseClient clickHouseClient = new ClickHouseClient();
        clickHouseClient.serverUri = URI.create(url);
        clickHouseClient.username = username;
        clickHouseClient.password = password;

        String serverHost = clickHouseClient.serverUri.getHost();
        int serverPort = clickHouseClient.serverUri.getPort();

        clickHouseClient.channel = Grpc.newChannelBuilderForAddress(serverHost, serverPort, InsecureChannelCredentials.create())
                .keepAliveTime(10, TimeUnit.SECONDS)
                .keepAliveTimeout(30, TimeUnit.SECONDS)
                .userAgent("clickhouse-client-grpc-v0.1")
                .build();

        String serverVersion = clickHouseClient.queryServerVersion();

        System.out.printf("Connected to ClickHouse Server: %s:%d;version=%s%n", serverHost, serverPort, serverVersion);

        return clickHouseClient;
    }

    public void query(String sql) {
        Result result = executeQuery(sql);
        System.out.println(result);
    }

    private synchronized String queryServerVersion() {
        if (serverVersion != null) {
            return serverVersion;
        }

        Result result = executeQuery("select version()");
        ByteString output = result.getOutput();
        serverVersion = new String(output.toByteArray());
        return serverVersion;
    }

    private Result executeQuery(String query) {
        ClickHouseGrpc.ClickHouseBlockingStub clickHouseBlockingStub = ClickHouseGrpc.newBlockingStub(channel);
        QueryInfo queryInfo = QueryInfo.newBuilder()
                .setQuery(query)
                .setUserName(username)
                .setPassword(password)
                .build();

        return clickHouseBlockingStub.executeQuery(queryInfo);
    }

    public static void main(String[] args) {
        ClickHouseClient client = ClickHouseClient.connect("localhost:9100", "default", "passwd-1m39z");
        client.query("select rand64()");
    }
}
