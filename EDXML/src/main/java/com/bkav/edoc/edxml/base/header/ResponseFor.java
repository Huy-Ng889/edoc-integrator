package com.bkav.edoc.edxml.base.header;

import com.bkav.edoc.edxml.base.BaseElement;
import com.bkav.edoc.edxml.base.util.BaseXmlUtils;
import com.bkav.edoc.edxml.base.util.DateUtils;
import com.bkav.edoc.edxml.resource.StringPool;
import com.google.common.base.MoreObjects;
import org.jdom2.Element;

import javax.xml.bind.annotation.*;
import java.util.Date;

@XmlRootElement(name = "ResponseFor", namespace = StringPool.TARGET_NAMESPACE)
@XmlType(name = "ResponseFor", propOrder = {"organId", "code", "promulgationDate", "documentId"})
@XmlAccessorType(XmlAccessType.FIELD)
public class ResponseFor extends BaseElement {

    @XmlElement(name = "OrganId")
    private String organId;
    @XmlElement(name = "Code")
    private String code;
    @XmlElement(name = "PromulgationDate")
    private Date promulgationDate;
    @XmlElement(name = "DocumentId")
    private String documentId;

    public ResponseFor() {
    }

    public ResponseFor(String organId, String code, Date promulgationDate, String documentId) {
        this.organId = organId;
        this.code = code;
        this.promulgationDate = promulgationDate;
        this.documentId = documentId;
    }

    public ResponseFor(String organId, String code, Date promulgationDate) {
        this.organId = organId;
        this.code = code;
        this.promulgationDate = promulgationDate;
    }

    public String getOrganId() {
        return organId;
    }

    public void setOrganId(String organId) {
        this.organId = organId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getPromulgationDate() {
        return promulgationDate;
    }

    public void setPromulgationDate(Date promulgationDate) {
        this.promulgationDate = promulgationDate;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public static ResponseFor fromContent(Element paramElement) {
        return new ResponseFor(BaseXmlUtils.getString(paramElement, "OrganId"),
                BaseXmlUtils.getString(paramElement, "Code"),
                DateUtils.parse(BaseXmlUtils.getString(paramElement, "PromulgationDate"), DateUtils.DEFAULT_DATETIME_FORMAT),
                BaseXmlUtils.getString(paramElement, "DocumentId"));
    }

    public void accumulate(Element parentElement) {
        Element responseFor = this.accumulate(parentElement, "ResponseFor");
        this.accumulate(responseFor, "OrganId", this.organId);
        this.accumulate(responseFor, "Code", this.code);
        this.accumulate(responseFor, "DocumentId", this.documentId);
        this.accumulate(responseFor, "PromulgationDate", DateUtils.format(this.promulgationDate));
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(super.getClass()).add("OrganId",
                this.organId).add("Code", this.code).add("DocumentId",
                this.documentId).add("PromulgationDate", this.promulgationDate).toString();
    }
}