package com.team18.FleetForge.config;

import com.team18.FleetForge.util.JwtTokenUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtTokenUtils jwtTokenUtils;
    private final UserDetailsService userDetailsService;

    /**
     * Configure message broker.
     * - /topic: for broadcasting to multiple subscribers
     * - /queue: for point-to-point messaging
     * - /app: application destination prefix for @MessageMapping
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");

        // Set application destination prefix for @MessageMapping
        config.setApplicationDestinationPrefixes("/app");

        // Set user destination prefix (for sending to specific users)
        config.setUserDestinationPrefix("/user");
    }

    /**
     * Register STOMP endpoints.
     * Clients connect to /ws with SockJS fallback support.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    /**
     * Configure client inbound channel to intercept CONNECT frames
     * and authenticate using JWT token.
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                System.out.println("Accessor: " + accessor);
                System.out.println("Command: " + (accessor != null ? accessor.getCommand() : "null"));

                if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {

                    System.out.println("✅ CONNECT FRAME DETECTED!");

                    String authToken = accessor.getFirstNativeHeader("Authorization");
                    System.out.println("Auth Token: " + authToken);

                    if (authToken != null && authToken.startsWith("Bearer ")) {
                        String token = authToken.substring(7);

                        try {
                            String username = jwtTokenUtils.getUsernameFromToken(token);
                            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                            if (jwtTokenUtils.validateToken(token, userDetails.getUsername())) {
                                UsernamePasswordAuthenticationToken authentication =
                                        new UsernamePasswordAuthenticationToken(
                                                userDetails,
                                                null,
                                                userDetails.getAuthorities()
                                        );

                                accessor.setUser(authentication);

                                SecurityContextHolder.getContext().setAuthentication(authentication);
                            }
                        } catch (Exception e) {
                            System.err.println("WebSocket authentication failed: " + e.getMessage());
                        }
                    }
                }

                return message;
            }
        });
    }
}