package jp.igapyon.simpleodata4;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.olingo.commons.api.edmx.EdmxReference;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataHttpHandler;
import org.apache.olingo.server.api.ServiceMetadata;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    @RequestMapping("/DemoService.svc/*")
    protected void service(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            // create odata handler and configure it with CsdlEdmProvider and Processor
            OData odata = OData.newInstance();
            ServiceMetadata edm = odata.createServiceMetadata(new DemoEdmProvider(), new ArrayList<EdmxReference>());
            ODataHttpHandler handler = odata.createHandler(edm);
            handler.register(new DemoEntityCollectionProcessor());

            // let the handler do the work
            // odata tutorial origin: handler.process(req, resp);

            handler.process(new HttpServletRequestWrapper(req) {
                // Spring MVC matches the whole path as the servlet path
                // Olingo wants just the prefix, ie upto /odata, so that it
                // can parse the rest of it as an OData path. So we need to override
                // getServletPath()
                @Override
                public String getServletPath() {
                    return "/DemoService.svc";
                }
            }, resp);

        } catch (RuntimeException e) {
            System.err.println("Server Error occurred in ExampleServlet");
            throw new ServletException(e);
        }
    }
}