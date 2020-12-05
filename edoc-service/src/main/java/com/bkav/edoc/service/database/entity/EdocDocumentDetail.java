package com.bkav.edoc.service.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.Date;

public class EdocDocumentDetail implements Serializable {

    public enum SteeringType {
        NONE_STEER, STEER, STEER_REPORT
    }

    private Long documentId;
    private String content;
    private String signerCompetence;
    private String signerPosition;
    private String signerFullName;
    private Date dueDate;
    private String toPlaces;
    private String sphereOfPromulgation;
    private String typerNotation;
    private int promulgationAmount;
    private int pageAmount;
    private String appendixes;
    private String responseFor;
    private SteeringType steeringType;
    @JsonIgnore
    private EdocDocument document;

    public EdocDocumentDetail() {
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSignerCompetence() {
        return signerCompetence;
    }

    public void setSignerCompetence(String signerCompetence) {
        this.signerCompetence = signerCompetence;
    }

    public String getSignerPosition() {
        return signerPosition;
    }

    public void setSignerPosition(String signerPosition) {
        this.signerPosition = signerPosition;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getToPlaces() {
        return toPlaces;
    }

    public void setToPlaces(String toPlaces) {
        this.toPlaces = toPlaces;
    }

    public String getSphereOfPromulgation() {
        return sphereOfPromulgation;
    }

    public void setSphereOfPromulgation(String sphereOfPromulgation) {
        this.sphereOfPromulgation = sphereOfPromulgation;
    }

    public String getTyperNotation() {
        return typerNotation;
    }

    public void setTyperNotation(String typerNotation) {
        this.typerNotation = typerNotation;
    }

    public int getPromulgationAmount() {
        return promulgationAmount;
    }

    public void setPromulgationAmount(int promulgationAmount) {
        this.promulgationAmount = promulgationAmount;
    }

    public int getPageAmount() {
        return pageAmount;
    }

    public void setPageAmount(int pageAmount) {
        this.pageAmount = pageAmount;
    }

    public String getAppendixes() {
        return appendixes;
    }

    public void setAppendixes(String appendixes) {
        this.appendixes = appendixes;
    }

    public SteeringType getSteeringType() {
        return steeringType;
    }

    public void setSteeringType(SteeringType steeringType) {
        this.steeringType = steeringType;
    }

    public String getSignerFullName() {
        return signerFullName;
    }

    public void setSignerFullName(String signerFullName) {
        this.signerFullName = signerFullName;
    }

    public String getResponseFor() {
        return responseFor;
    }

    public void setResponseFor(String responseFor) {
        this.responseFor = responseFor;
    }

    public EdocDocument getDocument() {
        return document;
    }

    public void setDocument(EdocDocument document) {
        this.document = document;
    }
}
