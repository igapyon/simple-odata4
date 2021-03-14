package jp.igapyon.simpleodata4.entity;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.edm.FullQualifiedName;

/**
 * コンテナに関するローカル情報構造.
 */
public class SimpleContainerInfo {
    /**
     * ネームスペース名.
     */
    private String namespace = "Igapyon.Simple";

    /**
     * EDMコンテナ名.
     */
    private String containerName = "Container";

    private List<SimpleEntityInfo> localEntityInfoList = new ArrayList<>();

    private void ensureInit() {
        if (localEntityInfoList.size() == 0) {
            localEntityInfoList.add(new SimpleEntityInfo(this, "MyProducts", "MyProduct", "MyProducts"));
            localEntityInfoList.add(new SimpleEntityInfo(this, "ODataAppInfos", "ODataAppInfo", "ODataAppInfos"));
        }
    }

    public String getNamespace() {
        ensureInit();
        return namespace;
    }

    public void setNamespace(String namespace) {
        ensureInit();
        this.namespace = namespace;
    }

    public String getContainerName() {
        ensureInit();
        return containerName;
    }

    public void setContainerName(String containerName) {
        ensureInit();
        this.containerName = containerName;
    }

    ///////////////////////////////
    /////////////////

    public SimpleEntityInfo getLocalEntityInfoByEntityName(String entityName) {
        ensureInit();
        for (SimpleEntityInfo look : localEntityInfoList) {
            if (look.getEntityName().equals(entityName)) {
                return look;
            }
        }
        return null;
    }

    public SimpleEntityInfo getLocalEntityInfoByEntitySetName(String entitySetName) {
        ensureInit();
        for (SimpleEntityInfo look : localEntityInfoList) {
            if (look.getEntitySetName().equals(entitySetName)) {
                return look;
            }
        }
        return null;
    }

    public SimpleEntityInfo getLocalEntityInfoByEntityNameFQN(FullQualifiedName entityNameFQN) {
        ensureInit();
        for (SimpleEntityInfo look : localEntityInfoList) {
            if (look.getEntityNameFQN().equals(entityNameFQN)) {
                return look;
            }
        }
        return null;
    }

    ///////////////////////////////
    /////////////////

    public List<SimpleEntityInfo> getLocalEntityInfoList() {
        return localEntityInfoList;
    }

    /**
     * EDMコンテナ名のFQN(完全修飾名).
     * 
     * @return EDMコンテナ名のFQN(完全修飾名).
     */
    public FullQualifiedName getContainerFQN() {
        ensureInit();
        return new FullQualifiedName(getNamespace(), getContainerName());
    }
}
