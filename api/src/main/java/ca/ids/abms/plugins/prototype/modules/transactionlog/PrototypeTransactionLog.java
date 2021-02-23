package ca.ids.abms.plugins.prototype.modules.transactionlog;

import javax.persistence.*;

@Entity
@Table(name = "transaction_logs")
public class PrototypeTransactionLog {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "transaction_id")
    private Integer transactionId;

    @Column(name = "user_name")
    private String userName;

    public PrototypeTransactionLog() {
        this(null, null);
    }

    public PrototypeTransactionLog(Integer transactionId, String userName) {
        this.transactionId = transactionId;
        this.userName = userName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
