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

/**
 * OData 要素コレクションを処理するクラス.
 */
public class SimpleEntityCollectionProcessor implements EntityCollectionProcessor {
    /**
     * OData.
     */
    private OData odata;

    /**
     * サービスメタデータ.
     */
    private ServiceMetadata serviceMetadata;

    /**
     * 初期化タイミングにて ODataやサービスメタデータの情報を記憶.
     * 
     * @param odata           OData.
     * @param serviceMetadata サービスメタデータ.
     */
    @Override
    public void init(OData odata, ServiceMetadata serviceMetadata) {
        this.odata = odata;
        this.serviceMetadata = serviceMetadata;
    }

    /**
     * 要素コレクションを読み込み.
     * 
     * @param request        OData リクエスト.
     * @param response       OData レスポンス.
     * @param uriInfo        URI情報.
     * @param responseFormat レスポンスのフォーマット.
     */
    @Override
    public void readEntityCollection(ODataRequest request, ODataResponse response, //
            UriInfo uriInfo, ContentType responseFormat) //
            throws ODataApplicationException, SerializerException {

        // URI情報からURIリソースの指定を取得.
        List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
        // URIリソースの最初のものを要素セット指定とみなす.
        UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) resourcePaths.get(0);
        // 要素セットの指定からEDM要素セットを取得.
        EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();

        // 要素セットの指定をもとに要素コレクションを取得.
        EntityCollection entitySet = getData(edmEntitySet);

        // 指定のレスポンスフォーマットに合致するように直列化の準備.
        ODataSerializer serializer = odata.createSerializer(responseFormat);

        // 要素セットから要素型のEDM情報を取得してコンテキストURLをビルド.
        EdmEntityType edmEntityType = edmEntitySet.getEntityType();
        ContextURL contextUrl = ContextURL.with().entitySet(edmEntitySet).build();

        // 要素のIdを作成.
        final String id = request.getRawBaseUri() + "/" + edmEntitySet.getName();
        // 直列化の処理.
        EntityCollectionSerializerOptions opts = EntityCollectionSerializerOptions.with() //
                .id(id).contextURL(contextUrl).build();
        SerializerResult serializerResult = serializer.entityCollection( //
                serviceMetadata, edmEntityType, entitySet, opts);
        InputStream serializedContent = serializerResult.getContent();

        // OData レスポンスを返却.
        response.setContent(serializedContent);
        response.setStatusCode(HttpStatusCode.OK.getStatusCode());
        response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
    }

    ///////////////////////////////////////////////////
    // 実際に返却するデータの組み上げ.

    /**
     * 指定のEDM要素セットに対応する要素コレクションを取得.
     * 
     * @param edmEntitySet EDM要素セット.
     * @return 要素コレクション.
     */
    private EntityCollection getData(EdmEntitySet edmEntitySet) {
        EntityCollection productsCollection = new EntityCollection();
        // check for which EdmEntitySet the data is requested
        if (SimpleEdmProvider.ES_PRODUCTS_NAME.equals(edmEntitySet.getName())) {
            List<Entity> productList = productsCollection.getEntities();

            // add some sample product entities
            final Entity e1 = new Entity() //
                    .addProperty(new Property(null, "ID", ValueType.PRIMITIVE, 1))
                    .addProperty(new Property(null, "Name", ValueType.PRIMITIVE, "Notebook Basic 15"))
                    .addProperty(new Property(null, "Description", ValueType.PRIMITIVE,
                            "Notebook Basic, 1.7GHz - 15 XGA - 1024MB DDR2 SDRAM - 40GB"));
            e1.setId(createId("Products", 1));
            productList.add(e1);

            final Entity e2 = new Entity() //
                    .addProperty(new Property(null, "ID", ValueType.PRIMITIVE, 2))
                    .addProperty(new Property(null, "Name", ValueType.PRIMITIVE, "1UMTS PDA"))
                    .addProperty(new Property(null, "Description", ValueType.PRIMITIVE,
                            "Ultrafast 3G UMTS/HSDPA Pocket PC, supports GSM network"));
            e2.setId(createId("Products", 2));
            productList.add(e2);

            final Entity e3 = new Entity() //
                    .addProperty(new Property(null, "ID", ValueType.PRIMITIVE, 3))
                    .addProperty(new Property(null, "Name", ValueType.PRIMITIVE, "Ergo Screen"))
                    .addProperty(new Property(null, "Description", ValueType.PRIMITIVE,
                            "19 Optimum Resolution 1024 x 768 @ 85Hz, resolution 1280 x 960"));
            e3.setId(createId("Products", 3));
            productList.add(e3);
        }

        return productsCollection;
    }

    /**
     * 与えられた情報をもとにURIを作成.
     * 
     * @param entitySetName 要素セット名.
     * @param id ユニーク性を実現するId.
     * @return 要素セット名およびユニーク性を実現するIdをもとにつくられた部分的なURI.
     */
    private URI createId(String entitySetName, Object id) {
        try {
            return new URI(entitySetName + "(" + String.valueOf(id) + ")");
        } catch (URISyntaxException ex) {
            throw new ODataRuntimeException("Fail to create ID EntitySet name: " + entitySetName, ex);
        }
    }
}
