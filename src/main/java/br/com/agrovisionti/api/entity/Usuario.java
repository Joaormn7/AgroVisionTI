package br.com.agrovisionti.api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
public class Usuario {

    public static final String PERFIL_ADMINISTRADOR = "Administrador";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    // Hash da senha. Pode estar no formato legado (SHA-256 + salt, vindo do
    // app desktop) ou em BCrypt (usuarios novos / apos troca de senha) -
    // ver LegacyAwarePasswordEncoder.
    @Column(nullable = false)
    private String senha;

    // Usado apenas pelo hash legado. Fica nulo para senhas ja em BCrypt.
    private String salt;

    @Column(nullable = false)
    private String perfil;

    @Column(nullable = false)
    private boolean ativo = true;

    public boolean isAdministrador() {
        return PERFIL_ADMINISTRADOR.equals(perfil);
    }
}
