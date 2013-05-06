package orderprocessing;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import customermanagement.CustomerKey;

/**
 * 
 * @author Michal Michaluk <michaluk.michal@gmail.com>
 */
@Entity
@Table(name = "Customers")
public class CustomerEntity implements Serializable {
    
    private static final long serialVersionUID = -1659473037158650337L;
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE) protected long id;
    @Version protected int version;
    
    @Embedded @NotNull @Valid protected CustomerKey customerKey;
    @OneToMany(mappedBy = "customer", orphanRemoval = true, cascade = CascadeType.ALL)//
    protected Set<OrderEntity> orders = new HashSet<OrderEntity>();
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CustomerEntity [customerKey=");
        builder.append(customerKey);
        builder.append("]");
        return builder.toString();
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (customerKey == null ? 0 : customerKey.hashCode());
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
        CustomerEntity other = (CustomerEntity) obj;
        if (customerKey == null) {
            if (other.customerKey != null) {
                return false;
            }
        } else if (!customerKey.equals(other.customerKey)) {
            return false;
        }
        if (version != other.version) {
            return false;
        }
        return true;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public CustomerKey getCustomerKey() {
        return customerKey;
    }

    public void setCustomerKey(CustomerKey customerKey) {
        this.customerKey = customerKey;
    }

    public Set<OrderEntity> getOrders() {
        return orders;
    }

    public void setOrders(Set<OrderEntity> orders) {
        this.orders = orders;
    }
    
}
