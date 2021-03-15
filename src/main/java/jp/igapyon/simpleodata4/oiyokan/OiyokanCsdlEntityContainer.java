/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */
package jp.igapyon.simpleodata4.oiyokan;

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
     * このコンテナをビルドし、紐づくエンティティセットをここで生成. このクラスの利用者は、機能呼び出し前にこのメソッドを呼ぶこと.
     * 
     * 確実なビルドのため何度も呼び出し可。この機能がこのクラスの主要目的。
     */
    public void ensureBuild() {
        if (getEntitySets() == null) {
            setEntitySets(new ArrayList<CsdlEntitySet>());
        }

        // テンプレートとそれから生成された複写物と2種類あるため、フラグではなくサイズで判定が必要だった.
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

    /**
     * EDMコンテナ名のFQN(完全修飾名).
     * 
     * @return EDMコンテナ名のFQN(完全修飾名).
     */
    public FullQualifiedName getContainerFqnIyo() {
        return new FullQualifiedName(getNamespaceIyo(), getContainerNameIyo());
    }

    /**
     * エンティティ名からエンティティセットを取得。
     * 
     * @param entitySetName エンティティ名.
     * @return エンティティセット.
     */
    public OiyokanCsdlEntitySet getEntitySetByEntityNameIyo(String entitySetName) {
        for (CsdlEntitySet look : this.getEntitySets()) {
            OiyokanCsdlEntitySet look2 = (OiyokanCsdlEntitySet) look;
            if (look2.getEntityNameIyo().equals(entitySetName)) {
                return look2;
            }
        }

        return null;
    }

    /**
     * エンティティ名FQNをもとに エンティティセットを取得.
     * 
     * @param entityNameFQN エンティティ名FQN.
     * @return エンティティセット.
     */
    public OiyokanCsdlEntitySet getEntitySetByEntityNameFqnIyo(FullQualifiedName entityNameFQN) {
        for (CsdlEntitySet look : getEntitySets()) {
            OiyokanCsdlEntitySet look2 = (OiyokanCsdlEntitySet) look;
            if (look2.getEntityNameFqnIyo().equals(entityNameFQN)) {
                return look2;
            }
        }

        return null;
    }
}
