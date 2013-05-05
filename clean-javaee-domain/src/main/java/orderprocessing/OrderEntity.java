package orderprocessing;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * 
 * @author Michal Michaluk <michaluk.michal@gmail.com>
 */
@Entity
@Table(name = "Orders")
public class OrderEntity implements Serializable {
    
    private static final long serialVersionUID = 1599682747342086056L;
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE) protected long id;
    @Version protected int version;
    
    @Embedded @NotNull @Valid protected OrderKey orderKey;
    @Enumerated @NotNull protected OrderState orderState;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER) @NotNull//
    protected CustomerEntity customer;
    @Temporal(TemporalType.TIMESTAMP) @NotNull protected Date creationTime;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)//
    protected Set<OrderLineEntity> orderLines = new HashSet<OrderLineEntity>();
    
    protected OrderEntity() {
    }
    
    public OrderKey getOrderKey() {
        return orderKey;
    }
    
    public OrderState getOrderState() {
        return orderState;
    }
    
    public Set<OrderLineEntity> getOrderLines() {
        return Collections.unmodifiableSet(orderLines);
    }
    
    public void markAsScheduled() {
        if (orderState == OrderState.OPEN ||
                orderState == OrderState.SCHEDULED) {
            orderState = OrderState.SCHEDULED;
            // e.g. log info to auditing database
        } else {
            throw new IllegalStateException("Order state transition: " +
                    orderState + " -> " + OrderState.SCHEDULED +
                    " is illegal");
        }
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("OrderEntity [orderKey=");
        builder.append(orderKey);
        builder.append(", orderState=");
        builder.append(orderState);
        builder.append(", customer=");
        builder.append(customer);
        builder.append(", creationTime=");
        builder.append(creationTime);
        builder.append(", orderLines=");
        builder.append(orderLines);
        builder.append("]");
        return builder.toString();
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (creationTime == null ? 0 : creationTime.hashCode());
        result = prime * result + (customer == null ? 0 : customer.hashCode());
        result = prime * result + (orderKey == null ? 0 : orderKey.hashCode());
        result = prime * result + (orderState == null ? 0 : orderState.hashCode());
        result = prime * result + version;
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        OrderEntity other = (OrderEntity) obj;
        if (creationTime == null) {
            if (other.creationTime != null) {
                return false;
            }
        } else if (!creationTime.equals(other.creationTime)) {
            return false;
        }
        if (customer == null) {
            if (other.customer != null) {
                return false;
            }
        } else if (!customer.equals(other.customer)) {
            return false;
        }
        if (orderKey == null) {
            if (other.orderKey != null) {
                return false;
            }
        } else if (!orderKey.equals(other.orderKey)) {
            return false;
        }
        if (orderState != other.orderState) {
            return false;
        }
        if (version != other.version) {
            return false;
        }
        return true;
    }
}
