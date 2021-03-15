package jp.igapyon.simpleodata4.entity;

import java.util.ArrayList;

import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainer;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;

/**
 * CsdlEntityContainer の Iyokan 拡張
 */
public class OiyokanCsdlEntityContainer extends CsdlEntityContainer {
    /**
     * ネームスペース名. CsdlEntityContainer の上位の概念をここで記述。
     */
    private String namespace = "Igapyon.Simple";

    /**
     * コンテナ名. CsdlEntityContainer の名前そのもの.
     */
    private String containerName = "Container";

    
    /**
     * このコンテナをビルド。確実なビルドのため何度も呼び出し可。
     */
    public void ensureBuild() {
        if (getEntitySets() == null) {
            setEntitySets(new ArrayList<CsdlEntitySet>());
        }

        if (getEntitySets().size() == 0) {
            // EntitySet の初期セットを実施。
            getEntitySets().add(new OiyokanCsdlEntitySet(this, "MyProducts", "MyProduct", "MyProducts"));
            getEntitySets().add(new OiyokanCsdlEntitySet(this, "ODataAppInfos", "ODataAppInfo", "ODataAppInfos"));
        }
    }

    /**
     * 名前空間を取得します。これが存在すると便利なため、これを追加。
     * 
     * @return 名前空間名.
     */
    public String getNamespaceIyo() {
        return namespace;
    }

    /**
     * コンテナ名を取得。これが存在すると便利なため、これを追加。
     * 
     * @return コンテナ名.
     */
    public String getContainerNameIyo() {
        return containerName;
    }

    ///////////////////////////////
    /////////////////

    public OiyokanCsdlEntitySet getLocalEntityInfoByEntityName(String entityName) {
        for (CsdlEntitySet look : this.getEntitySets()) {
            OiyokanCsdlEntitySet look2 = (OiyokanCsdlEntitySet) look;
            if (look2.getInternalEntityName().equals(entityName)) {
                return look2;
            }
        }

        return null;
    }

    public OiyokanCsdlEntitySet getLocalEntityInfoByEntityNameFQN(FullQualifiedName entityNameFQN) {
        ensureBuild();
        for (CsdlEntitySet look : getEntitySets()) {
            OiyokanCsdlEntitySet look2 = (OiyokanCsdlEntitySet) look;
            if (look2.getInternalEntityNameFQN().equals(entityNameFQN)) {
                return look2;
            }
        }
        return null;
    }

    ///////////////////////////////
    /////////////////

    /**
     * EDMコンテナ名のFQN(完全修飾名).
     * 
     * @return EDMコンテナ名のFQN(完全修飾名).
     */
    public FullQualifiedName getInternalContainerFQN() {
        return new FullQualifiedName(getNamespaceIyo(), getContainerNameIyo());
    }
}
