package cn.hush.Coupra.engine.config;


import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program: Coupra
 * @description: 设置文档 API Swagger 配置信息，为了让 <a href="http://127.0.0.1:{server.port}{server.servlet.context-path}/doc.html" /> 中的信息看着更饱满
 * @author: Hush
 * @create: 2025-08-21 01:34
 **/
@Slf4j
@Configuration
public class SwaggerConfiguration implements ApplicationRunner {

    @Value("${server.port:8080}")
    private String serverPort;
    @Value("${server.servlet.context-path:}")
    private String contextPath;

    /**
     * 自定义 openAPI 个性化信息
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().
                info(new Info()// 基本信息配置
                        .title("Coupra-商家后台管理系统")// 标题
                        .description("负责优惠券单个查看、列表查看、锁定以及核销等功能等")// 描述 Api 接口文档的基本信息
                        .version("v1.0.0") // 版本
                        // 设置 OpenAPI 文档的联系信息
                        .contact(new Contact().name("Hush").email("1422419663@qq.com"))
                );
    }

    /**
     * 启动项目后可以直接点击链接跳转
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("API Document: http://127.0.0.1:{}{}/doc.html", serverPort, contextPath);
    }
}
