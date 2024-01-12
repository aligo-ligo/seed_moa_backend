package com.intouch.aligooligo.Config;

import static org.assertj.core.api.Fail.fail;

import java.sql.Connection;
import java.sql.DriverManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DBConfigTest {

    @Value("${spring.datasource.driver-class-name}")
    private static String className;

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbId;

    @Value("${spring.datasource.password}")
    private String dbPw;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void 디비_연결_테스트() {
        try (Connection con = DriverManager.getConnection(dbUrl, dbId, dbPw)) {
            System.out.println(con);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
