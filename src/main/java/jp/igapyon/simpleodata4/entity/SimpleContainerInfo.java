package jp.igapyon.simpleodata4.entity;

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

    private SimpleEntityInfo localEntityInfo = null;

    private void ensureInit() {
        if (localEntityInfo == null) {
            localEntityInfo = new SimpleEntityInfo(this, "MyProducts", "MyProduct", "MyProducts");
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
        return localEntityInfo;
    }

    public SimpleEntityInfo getLocalEntityInfoByEntitySetName(String entitySetName) {
        ensureInit();
        System.err.println("TRACE: getLocalEntityInfoByEntitySetName(): " + entitySetName);
        return localEntityInfo;
    }

    public SimpleEntityInfo getLocalEntityInfoByEntityNameFQN(FullQualifiedName entityNameFQN) {
        ensureInit();
        System.err.println("TRACE: getLocalEntityInfoByEntityNameFQN(): " + entityNameFQN);
        return localEntityInfo;
    }

    public void setLocalEntityInfo(SimpleEntityInfo localEntityInfo) {
        ensureInit();
        this.localEntityInfo = localEntityInfo;
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
