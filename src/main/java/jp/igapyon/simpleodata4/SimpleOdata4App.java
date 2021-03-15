package jp.igapyon.simpleodata4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * OData サーバ Spring Boot アプリのエントリポイント.
 */
@SpringBootApplication
public class SimpleOdata4App {
    /**
     * OData サーバ (Spring Boot ベース) を起動するエントリポイント.
     * 
     * @param args コマンドライン引数.
     */
    public static void main(String[] args) {
        SpringApplication.run(SimpleOdata4App.class, args);
    }
}
