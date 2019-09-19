package andbook.example.smartorder;

public class LoginDTO {
    private String id;
    private int workplace_num;
    private String password;

    @Override
    public String toString() {
        return "LoginDTO{" +
                "id='" + id + '\'' +
                ", workplace_num=" + workplace_num +
                ", password='" + password + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getWorkplace_num() {
        return workplace_num;
    }

    public void setWorkplace_num(int workplace_num) {
        this.workplace_num = workplace_num;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
