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

/**
 * OData を Servlet として動作.
 */
@RestController
public class DemoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    @RequestMapping("/simple.svc/*")
    protected void service(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            OData odata = OData.newInstance();
            ServiceMetadata edm = odata.createServiceMetadata(new DemoEdmProvider(), new ArrayList<EdmxReference>());
            ODataHttpHandler handler = odata.createHandler(edm);
            // EntityCollectionProcessor を登録.
            handler.register(new DemoEntityCollectionProcessor());
            // Spring と Servlet を調整.
            handler.process(new HttpServletRequestWrapper(req) {
                @Override
                public String getServletPath() {
                    return "/simple.svc";
                }
            }, resp);
        } catch (RuntimeException e) {
            System.err.println("Server Error occurred in ExampleServlet");
            throw new ServletException(e);
        }
    }
}