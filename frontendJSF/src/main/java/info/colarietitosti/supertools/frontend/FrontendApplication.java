package info.colarietitosti.supertools.frontend;

import com.sun.faces.config.ConfigureListener;
import info.colarietitosti.supertools.frontend.ui.feignClient.PingHomeConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;

import javax.faces.webapp.FacesServlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

@Configuration
@EnableFeignClients
@SpringBootApplication
public class FrontendApplication {

	@Autowired
	PingHomeConsumer pingHomeConsumer;

	public static void main(String[] args) {
		SpringApplication.run(FrontendApplication.class, args);
	}

	@Bean
	public FacesServlet facesServlet() { return new FacesServlet(); }

	@Bean
	public ServletRegistrationBean facesServletRegistration() {
		ServletRegistrationBean registration = new  ServletRegistrationBean(facesServlet(), new String[] { "*.xhtml" });
		registration.setName("FacesServlet");
		registration.setLoadOnStartup(1);
		return registration;
	}

	@Configuration
	static class ConfigureJSFContextParameters implements ServletContextInitializer {

		@Override
		public void onStartup(ServletContext servletContext) throws ServletException {
			servletContext.setInitParameter("com.sun.faces.forceLoadConfiguration", "true");
			servletContext.setInitParameter("javax.faces.DEFAULT_SUFFIX", ".xhtml");
			servletContext.setInitParameter("encoding", "UTF-8");
			servletContext.setInitParameter("primefaces.THEME", "omega");
			servletContext.setInitParameter("com.sun.faces.enableRestoreView11Compatibility", "true");
			servletContext.setInitParameter("org.ajax4jsf.handleViewExpiredOnClient", "true");
		}
	}

	@Bean
	public ServletListenerRegistrationBean<ConfigureListener> jsfConfigureListener() {
		return new ServletListenerRegistrationBean<ConfigureListener>(new ConfigureListener());
	}
}
