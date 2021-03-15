package jp.igapyon.simpleodata4.entity;

import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;

import jp.igapyon.simpleodata4.h2data.TinyH2EdmBuilder;

public class OiyokanCsdlEntitySet extends CsdlEntitySet {
    private TinyH2EdmBuilder edmBuilder = null;

    public TinyH2EdmBuilder getEdmBuilder() {
        return edmBuilder;
    }

    public void setEdmBuilder(TinyH2EdmBuilder edmBuilder) {
        this.edmBuilder = edmBuilder;
    }

}
