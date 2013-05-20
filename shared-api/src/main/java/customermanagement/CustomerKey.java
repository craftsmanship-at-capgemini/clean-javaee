package customermanagement;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Embeddable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 
 * @author Michal Michaluk <michaluk.michal@gmail.com>
 */
@Embeddable
public class CustomerKey implements Serializable {
    
    private static final long serialVersionUID = -3565100156471149574L;
    
    @NotNull @Min(1) @Basic private Long customerKey;
    
    
    public CustomerKey(Long customerKey) {
        this.customerKey = customerKey;
    }
    
    protected CustomerKey() {
    }
    
    public Long getCustomerKey() {
        return customerKey;
    }
    
    protected void setCustomerKey(Long customerKey) {
        this.customerKey = customerKey;
    }
    
    @Override
    public String toString() {
        return "" + customerKey;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (customerKey == null ? 0 : customerKey.hashCode());
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
        CustomerKey other = (CustomerKey) obj;
        if (customerKey == null) {
            if (other.customerKey != null) {
                return false;
            }
        } else if (!customerKey.equals(other.customerKey)) {
            return false;
        }
        return true;
    }
    
}
