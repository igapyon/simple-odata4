package jp.igapyon.simpleodata4.entity;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.edm.FullQualifiedName;

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
        System.err.println("TRACE: getLocalEntityInfoByEntityName(): " + entityName);
        // TODO FIXME
        return localEntityInfoList.get(0);
    }

    public SimpleEntityInfo getLocalEntityInfoByEntitySetName(String entitySetName) {
        ensureInit();
        System.err.println("TRACE: getLocalEntityInfoByEntitySetName(): " + entitySetName);
        // TODO FIXME
        return localEntityInfoList.get(0);
    }

    public SimpleEntityInfo getLocalEntityInfoByEntityNameFQN(FullQualifiedName entityNameFQN) {
        ensureInit();
        System.err.println("TRACE: getLocalEntityInfoByEntityNameFQN(): " + entityNameFQN);
        // TODO FIXME
        return localEntityInfoList.get(0);
    }

    ///////////////////////////////
    /////////////////

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
