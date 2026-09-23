package Project.Model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "users")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; 

    @NotBlank(message = "name không được để trống")
    private String name;

    @NotBlank(message = "email không được để trống")
    private String email;
    
    @NotBlank(message = "password không được để trống")
    private String password;

    @NotBlank(message = "phone không được để trống")
    private String phone;

    @NotBlank(message = "address không được để trống")
    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "role_id")
    private Role role;

    @OneToMany(mappedBy = "user")
    private List<Order> orders;

    @OneToMany(mappedBy = "user")
	private List<Comment> comments;

    @OneToMany(mappedBy = "user")
	private List<RefreshToken> refreshTokens;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "cart_id", unique = true)
    private Cart cart;
}
