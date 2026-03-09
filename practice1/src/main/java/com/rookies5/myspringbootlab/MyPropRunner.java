package com.rookies5.myspringbootlab;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MyPropRunner implements ApplicationRunner {
    
    @Value("${myprop.username}")
    private String username;

    @Value("${myprop.port}")
    private int port;

    private final MyPropProperties myPropProperties;
    
    @Autowired(required = false)
    private MyEnvironment myEnvironment;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("=========================================");
        // @Value 어노테이션을 사용하여 Load 한 환경변수 출력
        log.info("[@Value] username: {}, port: {}", username, port);
        
        // MyPropProperties 객체를 주입받아 getter 메서드 출력
        log.info("[MyPropProperties] username: {}, port: {}", 
                 myPropProperties.getUsername(), myPropProperties.getPort());

        // 로그 레벨 확인을 위한 로그 출력
        log.debug("--- [DEBUG LEVEL LOG] This will only show in 'test' profile ---");
        log.info("--- [INFO LEVEL LOG] This will show in both 'prod' and 'test' profiles ---");

        if (myEnvironment != null) {
            log.info("[Profile Mode Environment] {}", myEnvironment.getMode());
        } else {
            log.info("[Profile Mode Environment] No Active Profile (prod or test)");
        }
        log.info("=========================================");
    }
}
