package com.ubitech.aim.routeparser;

/**
 * Container for route fix attributes' names and values.
 */
public class DisplayAttribute {
    String attributeName = null;
    String attributeValue = null;

    DisplayAttribute() {
    }

    DisplayAttribute(String attrName, String attrValue) {
        this.attributeName = attrName;
        this.attributeValue = attrValue;
    }

    /**
     * Returns String value, which contains the attribute name.
     * 
     * @return String value, which contains the attribute name.
     */
    public String getAttrName() {
        return this.attributeName;
    }

    /**
     * Returns String value, which contains the attribute value.
     * 
     * @return String value, which contains the attribute value.
     */
    public String getAttrValue() {
        return this.attributeValue;
    }
}
