package orderprocessing;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 516244215/8R/2012
 * 
 * @author Michal Michaluk <michaluk.michal@gmail.com>
 */
@Embeddable
public class OrderKey implements Serializable {
    
    private static final long serialVersionUID = 8323302083294498099L;
    
    @NotNull @Size(min = 9, max = 9) @Column(length = 9) private String key;
    @NotNull @Size(min = 2, max = 2) @Column(length = 2) private String category;
    @NotNull @Size(min = 4, max = 4) @Column(length = 4) private String year;
    
    public OrderKey(String key, String category, String year) {
        this.key = key;
        this.category = category;
        this.year = year;
    }
    
    protected OrderKey() {
    }
    
    public String getKey() {
        return key;
    }
    
    public String getCategory() {
        return category;
    }
    
    public String getYear() {
        return year;
    }
    
    protected void setKey(String key) {
        this.key = key;
    }
    
    protected void setCategory(String category) {
        this.category = category;
    }
    
    protected void setYear(String year) {
        this.year = year;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(key);
        builder.append("/");
        builder.append(category);
        builder.append("/");
        builder.append(year);
        return builder.toString();
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (category == null ? 0 : category.hashCode());
        result = prime * result + (key == null ? 0 : key.hashCode());
        result = prime * result + (year == null ? 0 : year.hashCode());
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
        OrderKey other = (OrderKey) obj;
        if (category == null) {
            if (other.category != null) {
                return false;
            }
        } else if (!category.equals(other.category)) {
            return false;
        }
        if (key == null) {
            if (other.key != null) {
                return false;
            }
        } else if (!key.equals(other.key)) {
            return false;
        }
        if (year == null) {
            if (other.year != null) {
                return false;
            }
        } else if (!year.equals(other.year)) {
            return false;
        }
        return true;
    }
    
}
