package chat.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfiguration {

    @Value ("${auth.server.base.url}")
    private  String authServerBaseUrl;

    @Value ("${auth.server.time.out}")
    private  String authServerTimeOut;

    @Bean(name = "authWenClient")
    public WebClient webClient () {
        final var timeOut = Integer.parseInt (authServerTimeOut);
        final var tcpClient = TcpClient
                .create ()
                .option (ChannelOption.CONNECT_TIMEOUT_MILLIS, timeOut)
                .doOnConnected (connection -> {
                    connection.addHandlerLast (new ReadTimeoutHandler (timeOut, TimeUnit.MILLISECONDS));
                    connection.addHandlerLast (new WriteTimeoutHandler (timeOut, TimeUnit.MILLISECONDS));
                });

        return WebClient.builder ()
                .baseUrl (authServerBaseUrl)
                .clientConnector (new ReactorClientHttpConnector (HttpClient.from (tcpClient)))
                .build ();
    }
}