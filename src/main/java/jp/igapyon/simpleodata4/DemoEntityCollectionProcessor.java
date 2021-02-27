package jp.igapyon.simpleodata4;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.processor.EntityCollectionProcessor;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;

public class DemoEntityCollectionProcessor implements EntityCollectionProcessor {
    private OData odata;
    private ServiceMetadata serviceMetadata;

    public void init(OData odata, ServiceMetadata serviceMetadata) {
        this.odata = odata;
        this.serviceMetadata = serviceMetadata;
    }

    public void readEntityCollection(ODataRequest request, ODataResponse response, UriInfo uriInfo,
            ContentType responseFormat) throws ODataApplicationException, SerializerException {

        // 1st we have retrieve the requested EntitySet from the uriInfo object
        // (representation of the parsed service URI)
        List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
        UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) resourcePaths.get(0); // in our example, the
                                                                                                 // first segment is the
                                                                                                 // EntitySet
        EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();

        // 2nd: fetch the data from backend for this requested EntitySetName
        // it has to be delivered as EntitySet object
        EntityCollection entitySet = getData(edmEntitySet);

        // 3rd: create a serializer based on the requested format (json)
        ODataSerializer serializer = odata.createSerializer(responseFormat);

        // 4th: Now serialize the content: transform from the EntitySet object to
        // InputStream
        EdmEntityType edmEntityType = edmEntitySet.getEntityType();
        ContextURL contextUrl = ContextURL.with().entitySet(edmEntitySet).build();

        final String id = request.getRawBaseUri() + "/" + edmEntitySet.getName();
        System.err.println("TRACE:id:" + id);
        EntityCollectionSerializerOptions opts = EntityCollectionSerializerOptions.with().id(id).contextURL(contextUrl)
                .build();
        SerializerResult serializerResult = serializer.entityCollection(serviceMetadata, edmEntityType, entitySet,
                opts);
        InputStream serializedContent = serializerResult.getContent();

        // Finally: configure the response object: set the body, headers and status code
        response.setContent(serializedContent);
        response.setStatusCode(HttpStatusCode.OK.getStatusCode());
        response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
    }

    private EntityCollection getData(EdmEntitySet edmEntitySet) {
        EntityCollection productsCollection = new EntityCollection();
        // check for which EdmEntitySet the data is requested
        if (DemoEdmProvider.ES_PRODUCTS_NAME.equals(edmEntitySet.getName())) {
            List<Entity> productList = productsCollection.getEntities();

            // add some sample product entities
            final Entity e1 = new Entity().addProperty(new Property(null, "ID", ValueType.PRIMITIVE, 1))
                    .addProperty(new Property(null, "Name", ValueType.PRIMITIVE, "Notebook Basic 15"))
                    .addProperty(new Property(null, "Description", ValueType.PRIMITIVE,
                            "Notebook Basic, 1.7GHz - 15 XGA - 1024MB DDR2 SDRAM - 40GB"));
            e1.setId(createId("Products", 1));
            productList.add(e1);

            final Entity e2 = new Entity().addProperty(new Property(null, "ID", ValueType.PRIMITIVE, 2))
                    .addProperty(new Property(null, "Name", ValueType.PRIMITIVE, "1UMTS PDA"))
                    .addProperty(new Property(null, "Description", ValueType.PRIMITIVE,
                            "Ultrafast 3G UMTS/HSDPA Pocket PC, supports GSM network"));
            e2.setId(createId("Products", 1));
            productList.add(e2);

            final Entity e3 = new Entity().addProperty(new Property(null, "ID", ValueType.PRIMITIVE, 3))
                    .addProperty(new Property(null, "Name", ValueType.PRIMITIVE, "Ergo Screen"))
                    .addProperty(new Property(null, "Description", ValueType.PRIMITIVE,
                            "19 Optimum Resolution 1024 x 768 @ 85Hz, resolution 1280 x 960"));
            e3.setId(createId("Products", 1));
            productList.add(e3);
        }

        return productsCollection;
    }

    private URI createId(String entitySetName, Object id) {
        try {
            return new URI(entitySetName + "(" + String.valueOf(id) + ")");
        } catch (URISyntaxException e) {
            throw new ODataRuntimeException("Unable to create id for entity: " + entitySetName, e);
        }
    }
}
