package io.nullptr.ch_client;

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

        return clickHouseClient;
    }

    public void query(String sql) {
        ClickHouseGrpc.ClickHouseBlockingStub clickHouseBlockingStub = ClickHouseGrpc.newBlockingStub(channel);
        QueryInfo queryInfo = QueryInfo.newBuilder()
                .setQuery(sql)
                .setUserName(username)
                .setPassword(password)
                .build();

        Result result = clickHouseBlockingStub.executeQuery(queryInfo);
        System.out.println(result);
    }

    public static void main(String[] args) {
        ClickHouseClient client = ClickHouseClient.connect("localhost:9100", "default", "passwd-1m39z");
        client.query("select version() as ch_server_version");
    }
}
