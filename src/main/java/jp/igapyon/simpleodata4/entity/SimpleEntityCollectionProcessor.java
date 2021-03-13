package jp.igapyon.simpleodata4.entity;

import java.util.List;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
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
import org.apache.olingo.server.core.uri.queryoption.CountOptionImpl;

import jp.igapyon.simpleodata4.h2data.TinyH2EntityDataBuilder;

/**
 * OData 要素コレクションを処理するクラス.
 * 
 * コードの多くは olingo のための基礎的な記述に該当.
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
     * @param odata           ODataインスタンス.
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
        // TODO FIXME いまは1番目の項目のみ処理.
        UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) resourcePaths.get(0);
        // 要素セットの指定からEDM要素セットを取得.
        EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();

        // 要素セットの指定をもとに要素コレクションを取得.
        // これがデータ本体に該当.
        final EntityCollection eCollection = TinyH2EntityDataBuilder.buildData(edmEntitySet, uriInfo);

        // 指定のレスポンスフォーマットに合致する直列化を準備.
        ODataSerializer serializer = odata.createSerializer(responseFormat);

        // 要素セットから要素型のEDM情報を取得してコンテキストURLをビルド.
        EdmEntityType edmEntityType = edmEntitySet.getEntityType();
        ContextURL conUrl = ContextURL.with().entitySet(edmEntitySet).build();

        // 要素のIdを作成.
        final String id = request.getRawBaseUri() + "/" + edmEntitySet.getName();
        final CountOptionImpl copt = new CountOptionImpl();
        copt.setValue(true);
        // 直列化の処理.
        EntityCollectionSerializerOptions opts = EntityCollectionSerializerOptions.with() //
                .id(id).count(copt).contextURL(conUrl).build();
        SerializerResult serResult = serializer.entityCollection( //
                serviceMetadata, edmEntityType, eCollection, opts);

        // OData レスポンスを返却.
        response.setContent(serResult.getContent());
        response.setStatusCode(HttpStatusCode.OK.getStatusCode());
        response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
    }
}
