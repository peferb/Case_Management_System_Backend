package se.teknikhogskolan.springcasemanagement.model;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Issue extends AbstractEntity {

    @OneToOne(fetch = FetchType.EAGER, mappedBy = "issue")
    private WorkItem workItem;
    private String description;
    private boolean active;

    @CreatedBy
    private String createdBy;

    protected Issue() {
    }

    public Issue(String description) {
        this.description = description;
        this.active = true;
    }

    public WorkItem getWorkItem() {
        return workItem;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return active;
    }

    public Issue setDescription(String description) {
        this.description = description;
        return this;
    }

    public Issue setWorkItem(WorkItem workItem) {
        this.workItem = workItem;
        return this;
    }

    public Issue setActive(boolean active) {
        this.active = active;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Issue) {
            Issue otherIssue = (Issue) obj;
            return (isActive() == otherIssue.isActive()) && description.equals(otherIssue.getDescription());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result += 31 * description.hashCode();
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Issue [id=");
        builder.append(getId());
        builder.append(", description=");
        builder.append(description);
        builder.append(", active=");
        builder.append(active);
        builder.append(", workItemId=");
        builder.append(workItem == null ? "null" : workItem.getId());
        builder.append(", created=");
        builder.append(createdDateToString());
        builder.append(", lastModified=");
        builder.append(lastModifiedToString());
        builder.append("]");
        return builder.toString();
    }


}