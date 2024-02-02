package octopus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class SimpleRestAPI2Application {
    public static void main( String[] args ) {
        ConfigurableApplicationContext ctx = SpringApplication.run( SimpleRestAPI2Application.class, args );

        Environment env = ctx.getBean( Environment.class );
        String portValue = env.getProperty( "server.port" );

        System.out.println( "Tomcat Server Port :: " + portValue );
    }
}
