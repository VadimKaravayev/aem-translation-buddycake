package com.aem.translation.connector.buddycake.core.servlets;

import com.aem.translation.connector.buddycake.core.servlets.annotations.SlingServletMethods;
import com.ddy.kotlin.core.services.GreetService;
import com.ddy.kotlin.core.services.HelloKotlin;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.servlets.annotations.SlingServletPathsStrict;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import java.util.Map;


@Component(service = Servlet.class)
@SlingServletPathsStrict(paths = "/bin/buddycake/test")
@SlingServletMethods(value = HttpConstants.METHOD_GET)
public class TestServlet extends BuddyCakeBaseServlet {

    @Reference
    private GreetService greetService;

    @Override
    protected Object handleRequest(SlingHttpServletRequest request) {

        var service = new HelloKotlin();

        return Map.of(
                "name", "Buddy Cake",
                "message", service.hello(),
                "salutation", greetService.greet("Ake")
        );
    }
}
