package orderprocessing;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "SequenceElements")
public class SequenceElementEntity implements Serializable {
    
    private static final long serialVersionUID = -1412211194985885586L;
    
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE) protected long id;
    @NotNull @Size(min = 50, max = 50) @Column(length = 50) protected String operator;
    @NotNull @Min(0) @Basic protected int sequenceNumber;
    @Embedded @NotNull @Valid protected OrderKey orderKey;
    
    protected SequenceElementEntity(String operator, int sequenceNumber, OrderKey orderKey) {
        this.operator = operator;
        this.sequenceNumber = sequenceNumber;
        this.orderKey = orderKey;
    }
    
    protected SequenceElementEntity() {
    }
    
    public String getOperator() {
        return operator;
    }
    
    public int getSequenceNumber() {
        return sequenceNumber;
    }
    
    public OrderKey getOrderKey() {
        return orderKey;
    }
    
    protected void setOperator(String operator) {
        this.operator = operator;
    }
    
    protected void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }
    
    protected void setOrderKey(OrderKey orderKey) {
        this.orderKey = orderKey;
    }
    
    @Override
    public String toString() {
        return "SequenceElementEntity [operator=" + operator +
                ", sequenceNumber=" + sequenceNumber +
                ", orderKey=" + orderKey + "]";
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((operator == null) ? 0 : operator.hashCode());
        result = prime * result + ((orderKey == null) ? 0 : orderKey.hashCode());
        result = prime * result + sequenceNumber;
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SequenceElementEntity other = (SequenceElementEntity) obj;
        if (operator == null) {
            if (other.operator != null)
                return false;
        } else if (!operator.equals(other.operator))
            return false;
        if (orderKey == null) {
            if (other.orderKey != null)
                return false;
        } else if (!orderKey.equals(other.orderKey))
            return false;
        if (sequenceNumber != other.sequenceNumber)
            return false;
        return true;
    }
    
}
