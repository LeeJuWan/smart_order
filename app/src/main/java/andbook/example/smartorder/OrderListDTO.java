package andbook.example.smartorder;

public class OrderListDTO {
    private String order_time;
    private String order_info;
    private int workplace_num;

    public String getOrder_info() {
        return order_info;
    }

    public void setOrder_info(String order_info) {
        this.order_info = order_info;
    }

    public int getWorkplace_num() {
        return workplace_num;
    }

    public void setWorkplace_num(int workplace_num) {
        this.workplace_num = workplace_num;
    }

    public String getOrder_time() {
        return order_time;
    }

    public void setOrder_time(String order_time) {
        this.order_time = order_time;
    }

    @Override
    public String toString() {
        return "OrderListDTO{" +
                "order_time=" + order_time +
                ", order_info='" + order_info + '\'' +
                ", workplace_num=" + workplace_num +
                '}';
    }
}
