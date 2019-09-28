package dto;

import java.io.Serializable;

// 매장 정보 DTO
public class AddressDTO implements Serializable
{
    private String place_name;
    private String address;
    private String phone_number;
    private int workplace_num;

    public String getPlace_name() {
        return place_name;
    }

    public void setPlace_name(String place_name) {
        this.place_name = place_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public int getWorkplace_num() {
        return workplace_num;
    }

    public void setWorkplace_num(int workplace_num) {
        this.workplace_num = workplace_num;
    }
}
