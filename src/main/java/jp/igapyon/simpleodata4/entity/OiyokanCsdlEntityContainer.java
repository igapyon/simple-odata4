package jp.igapyon.simpleodata4.entity;

import java.util.ArrayList;

import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainer;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;

public class OiyokanCsdlEntityContainer extends CsdlEntityContainer {

    /**
     * ネームスペース名.
     */
    private String namespace = "Igapyon.Simple";

    /**
     * EDMコンテナ名.
     */
    private String containerName = "Container";

    public void ensureOpen() {
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
     * 名前空間を取得します。これが存在するととても便利なため、これを追加。
     * 
     * @return 名前空間名.
     */
    public String getNamespace() {
        return namespace;
    }

    public String getInternalContainerName() {
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
        ensureOpen();
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
        return new FullQualifiedName(getNamespace(), getInternalContainerName());
    }
}
