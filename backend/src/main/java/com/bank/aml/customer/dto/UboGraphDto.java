package com.bank.aml.customer.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class UboGraphDto {

    private String entityId;
    private String entityName;
    private List<UboNode> nodes = new ArrayList<>();
    private List<UboEdge> edges = new ArrayList<>();

    public UboGraphDto() {}

    public UboGraphDto(String entityId, String entityName) {
        this.entityId = entityId;
        this.entityName = entityName;
    }

    public static class UboNode {
        private String id;
        private String label;
        private String type; // ROOT_ENTITY, INDIVIDUAL_UBO, INTERMEDIARY_ENTITY, MINORITY_SHAREHOLDER
        private BigDecimal percentage;
        private boolean ubo;
        private boolean sanctioned;
        private boolean pep;

        public UboNode() {}

        public UboNode(String id, String label, String type, BigDecimal percentage, boolean ubo, boolean sanctioned, boolean pep) {
            this.id = id;
            this.label = label;
            this.type = type;
            this.percentage = percentage;
            this.ubo = ubo;
            this.sanctioned = sanctioned;
            this.pep = pep;
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public BigDecimal getPercentage() { return percentage; }
        public void setPercentage(BigDecimal percentage) { this.percentage = percentage; }
        public boolean isUbo() { return ubo; }
        public void setUbo(boolean ubo) { this.ubo = ubo; }
        public boolean isSanctioned() { return sanctioned; }
        public void setSanctioned(boolean sanctioned) { this.sanctioned = sanctioned; }
        public boolean isPep() { return pep; }
        public void setPep(boolean pep) { this.pep = pep; }
    }

    public static class UboEdge {
        private String source;
        private String target;
        private BigDecimal percentage;
        private String controlType;

        public UboEdge() {}

        public UboEdge(String source, String target, BigDecimal percentage, String controlType) {
            this.source = source;
            this.target = target;
            this.percentage = percentage;
            this.controlType = controlType;
        }

        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
        public String getTarget() { return target; }
        public void setTarget(String target) { this.target = target; }
        public BigDecimal getPercentage() { return percentage; }
        public void setPercentage(BigDecimal percentage) { this.percentage = percentage; }
        public String getControlType() { return controlType; }
        public void setControlType(String controlType) { this.controlType = controlType; }
    }

    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }
    public String getEntityName() { return entityName; }
    public void setEntityName(String entityName) { this.entityName = entityName; }
    public List<UboNode> getNodes() { return nodes; }
    public void setNodes(List<UboNode> nodes) { this.nodes = nodes; }
    public List<UboEdge> getEdges() { return edges; }
    public void setEdges(List<UboEdge> edges) { this.edges = edges; }
}
