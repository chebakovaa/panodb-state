package com.concordsoft.navi;

import com.concordsoft.navi.controller.NaviController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import java.util.logging.*;


@SpringBootApplication
@EnableSwagger2
@ComponentScan(basePackageClasses = {
        NaviController.class
})
public class NaviApplication {

    private static final Logger LOG
            = Logger.getLogger(NaviApplication.class.getName());

	public static void main(String[] args) {
		SpringApplication.run(NaviApplication.class, args);
	}

//    @PostConstruct
//    public void init() {
//        LOG.info("init start.");
//    }

}
