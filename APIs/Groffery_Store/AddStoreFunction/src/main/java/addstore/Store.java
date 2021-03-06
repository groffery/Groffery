package addstore;

import java.util.Objects;

public class Store {
    private String storeName;
    private String storeManager;
    private String storeEmail;
    private String storePhoneNo;
    private String storeLocation;

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreManager() {
        return storeManager;
    }

    public void setStoreManager(String storeManager) {
        this.storeManager = storeManager;
    }

    public String getStoreEmail() {
        return storeEmail;
    }

    public void setStoreEmail(String storeEmail) {
        this.storeEmail = storeEmail;
    }

    public String getStorePhoneNo() {
        return storePhoneNo;
    }

    public void setStorePhoneNo(String storePhoneNo) {
        this.storePhoneNo = storePhoneNo;
    }

    public String getStoreLocation() {
        return storeLocation;
    }

    public void setStoreLocation(String storeLocation) {
        this.storeLocation = storeLocation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Store store = (Store) o;
        return Objects.equals(storeName, store.storeName) &&
                Objects.equals(storeManager, store.storeManager) &&
                Objects.equals(storeEmail, store.storeEmail) &&
                Objects.equals(storePhoneNo, store.storePhoneNo) &&
                Objects.equals(storeLocation, store.storeLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(storeName, storeManager, storeEmail, storePhoneNo, storeLocation);
    }

    @Override
    public String toString() {
        return "Store{" +
                "storeName='" + storeName + '\'' +
                ", storeManager='" + storeManager + '\'' +
                ", storeEmail='" + storeEmail + '\'' +
                ", storePhoneNo='" + storePhoneNo + '\'' +
                ", storeLocation='" + storeLocation + '\'' +
                '}';
    }
}
